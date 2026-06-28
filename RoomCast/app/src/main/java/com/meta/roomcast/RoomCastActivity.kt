package com.meta.roomcast

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.net.toUri
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.ComposeViewPanelRegistration
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.datamodelinspector.DataModelInspectorFeature
import com.meta.spatial.debugtools.HotReloadFeature
import com.meta.spatial.mruk.AnchorProceduralMesh
import com.meta.spatial.mruk.AnchorProceduralMeshConfig
import com.meta.spatial.mruk.MRUKAnchor
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKLabel
import com.meta.spatial.mruk.MRUKLoadDeviceResult
import com.meta.spatial.mruk.MRUKRoom
import com.meta.spatial.mruk.MRUKSceneEventListener
import com.meta.spatial.mruk.MRUKPlane
import com.meta.spatial.mruk.getSize
import com.meta.spatial.mruk.hasLabel
import com.meta.spatial.core.Query
import com.meta.spatial.toolkit.Material
import com.meta.spatial.ovrmetrics.OVRMetricsDataModel
import com.meta.spatial.ovrmetrics.OVRMetricsFeature
import com.meta.spatial.physics.PhysicsFeature
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.DpPerMeterDisplayOptions
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.PanelStyleOptions
import com.meta.spatial.toolkit.QuadShapeOptions
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.UIPanelSettings
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.VRFeature
import com.meta.roomcast.data.FurnitureCatalog
import com.meta.roomcast.data.FurnitureItem
import com.meta.roomcast.ui.CatalogPanel
import com.meta.roomcast.ui.FitStatusUi
import com.meta.roomcast.ui.RoomScanPanel
import com.meta.roomcast.ui.ScanState

/**
 * RoomCastActivity — Actividad principal de la app.
 *
 * Flujo:
 *  1. App abre → passthrough ON → panel de escaneo en el centro
 *  2. Usuario pulsa "Iniciar escaneo" → MRUK escanea la habitación (~5s)
 *  3. Escaneo completo → panel catálogo (izquierda) + panel detalle (derecha) aparecen
 *  4. Usuario selecciona mueble → aparece el modelo .glb en su piso real
 *  5. Puede moverlo con el controlador (Grabbable)
 *  6. La app valida si el mueble cabe en el espacio disponible
 */
class RoomCastActivity : AppSystemActivity(), MRUKSceneEventListener {

  companion object {
    private const val TAG = "RoomCast"
    private const val PERMISSION_USE_SCENE = "com.oculus.permission.USE_SCENE"
    private const val REQUEST_CODE_SCENE = 1001
  }

  // ── MRUK ─────────────────────────────────────────────────────────────────────
  private lateinit var mrukFeature: MRUKFeature
  private lateinit var physicsFeature: PhysicsFeature
  private var procMeshSpawner: AnchorProceduralMesh? = null

  // ── Panel entities ────────────────────────────────────────────────────────────
  private var scanPanelEntity: Entity? = null
  private var catalogPanelEntity: Entity? = null

  // ── Placed furniture entities (id → Entity) ───────────────────────────────────
  private val placedFurniture = mutableMapOf<String, Entity>()

  // ── Compose observable state ──────────────────────────────────────────────────
  private var scanState by mutableStateOf(ScanState.IDLE)
  private var selectedItem by mutableStateOf<FurnitureItem?>(null)
  private var fitStatus by mutableStateOf(FitStatusUi.UNKNOWN)
  private var sceneDataLoaded = false

  // ── Feature registration ──────────────────────────────────────────────────────

  override fun registerFeatures(): List<SpatialFeature> {
    mrukFeature = MRUKFeature(this, systemManager)
    physicsFeature = PhysicsFeature(spatial)

    val features = mutableListOf<SpatialFeature>(
        VRFeature(this),
        ComposeFeature(),
        physicsFeature,
        mrukFeature,
    )

    if (BuildConfig.DEBUG) {
      features.add(CastInputForwardFeature(this))
      features.add(HotReloadFeature(this))
      features.add(OVRMetricsFeature(this, OVRMetricsDataModel { numberOfMeshes() }))
      features.add(DataModelInspectorFeature(spatial, componentManager))
    }

    return features
  }

  // ── Lifecycle ─────────────────────────────────────────────────────────────────

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mrukFeature.addSceneEventListener(this)

    // Start with passthrough so user sees their real room immediately
    scene.enablePassthrough(true)

    // Request scene scanning permission if needed
    if (checkSelfPermission(PERMISSION_USE_SCENE) != PackageManager.PERMISSION_GRANTED) {
      scanState = ScanState.ERROR
      requestPermissions(arrayOf(PERMISSION_USE_SCENE), REQUEST_CODE_SCENE)
    }
    // Scene is loaded after onSceneReady() when the user taps "Iniciar escaneo"
  }

  override fun onSceneReady() {
    super.onSceneReady()

    scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)
    scene.setLightingEnvironment(
        ambientColor = Vector3(0.25f),
        sunColor = Vector3(1.0f, 1.0f, 1.0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.3f,
    )
    scene.updateIBLEnvironment("environment.env")
    scene.setViewOrigin(0.0f, 0.0f, 1.5f, 180.0f)

    // Spawn all panels — catalog starts hidden
    spawnPanels()
  }

  override fun onSpatialShutdown() {
    mrukFeature.removeSceneEventListener(this)
    procMeshSpawner?.destroy()
    super.onSpatialShutdown()
  }

  // ── Panel spawning ────────────────────────────────────────────────────────────

  private fun spawnPanels() {
    // Scan panel — center, right in front of user at comfortable reading distance
    scanPanelEntity = Entity.createPanelEntity(
        R.id.scan_panel,
        Transform(Pose(Vector3(0f, 1.4f, 0.9f), Quaternion(0f, 180f, 0f))),
        Grabbable(),
        Visible(true),
    )

    // Catalog panel — floats in the center, angled toward user, hidden until scan done
    // Set size to a wider, dashboard-style aspect ratio: 1.6f wide, 0.9f high
    catalogPanelEntity = Entity.createPanelEntity(
        R.id.catalog_panel,
        Transform(Pose(Vector3(0f, 1.3f, 1.1f), Quaternion(0f, 180f, 0f))),
        Grabbable(),
        Visible(false),
    )
  }

  // ── Scan flow ─────────────────────────────────────────────────────────────────

  /** Called when user taps "Iniciar escaneo" in RoomScanPanel */
  private fun startRoomScan() {
    if (checkSelfPermission(PERMISSION_USE_SCENE) == PackageManager.PERMISSION_GRANTED) {
      scanState = ScanState.SCANNING
      loadSceneFromDevice()
    } else {
      scanState = ScanState.ERROR
      requestPermissions(arrayOf(PERMISSION_USE_SCENE), REQUEST_CODE_SCENE)
    }
  }

  private fun loadSceneFromDevice() {
    if (sceneDataLoaded) return
    sceneDataLoaded = true

    val future = mrukFeature.loadSceneFromDevice()
    future.whenComplete { result: MRUKLoadDeviceResult, _ ->
      if (result == MRUKLoadDeviceResult.SUCCESS) {
        Log.i(TAG, "Room scanned from device successfully")
        onScanComplete()
      } else {
        Log.e(TAG, "MRUK device scan failed: $result — falling back to JSON")
        loadFallbackScene()
      }
    }
  }

  private fun loadFallbackScene() {
    try {
      val json = assets.open("fallback_room.json").bufferedReader().use { it.readText() }
      mrukFeature.loadSceneFromJsonString(json)
      Log.i(TAG, "Loaded fallback room from JSON")
      onScanComplete()
    } catch (e: Exception) {
      Log.e(TAG, "Error loading fallback scene", e)
      scanState = ScanState.ERROR
    }
  }

  private fun onScanComplete() {
    scanState = ScanState.DONE

    // Generate physics collision meshes for floor and walls so furniture lands on real floor
    procMeshSpawner = AnchorProceduralMesh(
        mrukFeature,
        mapOf(
            MRUKLabel.FLOOR to AnchorProceduralMeshConfig(null as Material?, true),
            MRUKLabel.WALL_FACE to AnchorProceduralMeshConfig(null as Material?, true),
        ),
    )

    // Transition panels: hide scan panel, show unified catalog dashboard
    scanPanelEntity?.setComponent(Visible(false))
    catalogPanelEntity?.setComponent(Visible(true))
  }

  // ── Furniture placement ────────────────────────────────────────────────────────

  /** Place (or replace) a furniture item in the scene */
  private fun placeInRoom(item: FurnitureItem) {
    selectedItem = item

    // Remove ALL currently placed entities so selecting another article deletes the previous one
    for (entity in placedFurniture.values) {
      entity.destroy()
    }
    placedFurniture.clear()

    // Create entity with the .glb mesh, positioned 1 meter in front of the user (closer than before)
    val furnitureEntity = Entity.create(
        listOf(
            Mesh(
                "apk:///${item.glbAsset}".toUri(),
                hittable = com.meta.spatial.toolkit.MeshCollision.LineTest,
            ),
            com.meta.spatial.toolkit.Box(
                min = Vector3(-item.widthM / 2f, 0f, -item.depthM / 2f),
                max = Vector3(item.widthM / 2f, item.heightM, item.depthM / 2f)
            ),
            Transform(
                Pose(
                    t = Vector3(0f, 0f, 1.0f), // Spawned closer (1.0m) to fit well inside guardian boundaries
                    q = Quaternion(0f, 180f, 0f),
                )
            ),
            Grabbable(type = GrabbableType.PIVOT_Y), // User can grab it and pivot/rotate it easily around Y axis
            Visible(true),
        )
    )

    placedFurniture[item.id] = furnitureEntity

    // Basic space validation against room size
    validateFit(item)
  }

  /** Rotate the currently placed furniture item by a relative angle in degrees around the Y axis */
  private fun rotatePlacedFurniture(angleDegrees: Float) {
    val item = selectedItem ?: return
    val entity = placedFurniture[item.id] ?: return
    val transform = entity.tryGetComponent<Transform>() ?: return

    val currentPose = transform.transform
    val newRotation = currentPose.q.times(Quaternion(0f, angleDegrees, 0f))
    val newPose = Pose(currentPose.t, newRotation)

    entity.setComponent(Transform(newPose))
  }

  /** Remove the currently placed item from the room */
  private fun removeFromRoom() {
    for (entity in placedFurniture.values) {
      entity.destroy()
    }
    placedFurniture.clear()
    selectedItem = null
    fitStatus = FitStatusUi.UNKNOWN
  }

  /**
   * Basic fit validation: check if the furniture dimensions fit within the
   * largest detected room. In a more advanced version, this would use the
   * exact remaining free space detected by MRUK spatial anchors.
   */
  private fun validateFit(item: FurnitureItem) {
    val planes = Query.where { has(MRUKPlane.id, Transform.id, MRUKAnchor.id) }.eval()
    val floorEntity = planes.firstOrNull { entity ->
      val anchor = entity.getComponent<MRUKAnchor>()
      anchor.hasLabel(MRUKLabel.FLOOR)
    }

    fitStatus = if (floorEntity != null) {
      val mrukPlane = floorEntity.getComponent<MRUKPlane>()
      val size = mrukPlane.getSize() // Returns Vector2
      if (item.widthM <= size.x && item.depthM <= size.y) {
        FitStatusUi.FITS
      } else {
        FitStatusUi.DOES_NOT_FIT
      }
    } else {
      FitStatusUi.FITS // Optimistic fallback
    }
  }

  // ── Permissions ───────────────────────────────────────────────────────────────

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray,
  ) {
    if (requestCode == REQUEST_CODE_SCENE) {
      val granted = grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
      if (granted) {
        scanState = ScanState.IDLE
      } else {
        Log.w(TAG, "Scene permission denied by user")
      }
    }
  }

  // ── MRUK event callbacks ──────────────────────────────────────────────────────

  override fun onRoomAdded(room: MRUKRoom) {
    Log.d(TAG, "Room added: ${room.anchor}")
  }

  override fun onRoomRemoved(room: MRUKRoom) {
    Log.d(TAG, "Room removed: ${room.anchor}")
  }

  override fun onRoomUpdated(room: MRUKRoom) {
    Log.d(TAG, "Room updated: ${room.anchor}")
  }

  // ── Panel registrations ───────────────────────────────────────────────────────

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(

        // ── Scan / Welcome Panel ─────────────────────────────────────────────
        ComposeViewPanelRegistration(
            R.id.scan_panel,
            composeViewCreator = { _, ctx ->
              ComposeView(ctx).apply {
                setContent {
                  RoomScanPanel(
                      scanState = scanState,
                      onStartScan = { startRoomScan() },
                      onGrantPermission = {
                        requestPermissions(arrayOf(PERMISSION_USE_SCENE), REQUEST_CODE_SCENE)
                      },
                  )
                }
              }
            },
            settingsCreator = {
              UIPanelSettings(
                  shape = QuadShapeOptions(width = 0.7f, height = 0.85f),
                  style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
                  display = DpPerMeterDisplayOptions(),
              )
            },
        ),

        // ── Catalog Dashboard Panel ──────────────────────────────────────────
        ComposeViewPanelRegistration(
            R.id.catalog_panel,
            composeViewCreator = { _, ctx ->
              ComposeView(ctx).apply {
                setContent {
                  CatalogPanel(
                      selectedItem = selectedItem,
                      fitStatus = fitStatus,
                      isPlaced = selectedItem?.let { placedFurniture.containsKey(it.id) } ?: false,
                      onItemSelected = { item -> selectedItem = item },
                      onPlaceInRoom = { item -> placeInRoom(item) },
                      onRemoveFromRoom = { removeFromRoom() },
                      onRotate = { angle -> rotatePlacedFurniture(angle) }
                  )
                }
              }
            },
            settingsCreator = {
              UIPanelSettings(
                  shape = QuadShapeOptions(width = 1.6f, height = 0.9f),
                  style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
                  display = DpPerMeterDisplayOptions(),
              )
            },
        ),
    )
  }
}
