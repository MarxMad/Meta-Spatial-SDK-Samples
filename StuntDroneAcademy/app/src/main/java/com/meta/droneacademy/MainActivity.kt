package com.meta.droneacademy

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import com.meta.droneacademy.Drone
import com.meta.droneacademy.ui.HudPanel
import com.meta.droneacademy.ui.RoomScanPanel
import com.meta.droneacademy.ui.ScanState
import com.meta.roomcast.ui.theme.RoomCastTheme
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SpatialContext
import com.meta.spatial.core.Vector3
import com.meta.spatial.mruk.MRUKAnchor
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKLabel
import com.meta.spatial.mruk.MRUKLoadDeviceResult
import com.meta.spatial.mruk.MRUKRoom
import com.meta.spatial.mruk.MRUKSceneEventListener
import com.meta.spatial.physics.Physics
import com.meta.spatial.physics.PhysicsFeature
import com.meta.spatial.physics.PhysicsState
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.DpPerMeterDisplayOptions
import com.meta.spatial.toolkit.GLXFModel
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.PanelStyleOptions
import com.meta.spatial.toolkit.QuadShapeOptions
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.UIPanelSettings
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.VRFeature
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf

class MainActivity : AppSystemActivity(), MRUKSceneEventListener {

  private lateinit var mrukFeature: MRUKFeature

  // ECS Entities
  private var scanPanelEntity: Entity? = null
  private var hudPanelEntity: Entity? = null
  private var droneEntity: Entity? = null

  // Compose State bindings for HUD
  private var droneIsFlying by mutableStateOf(false)
  private var droneThrottle by mutableFloatStateOf(0.0f)
  private var droneHealth by mutableIntStateOf(100)
  private var droneSpeed by mutableFloatStateOf(0.0f)

  // Scan states
  private var scanState by mutableStateOf(ScanState.IDLE)
  private var hasScannedBefore by mutableStateOf(false)
  private var sceneDataLoaded by mutableStateOf(false)

  // Simulation physics helpers
  private var lastVelocity = Vector3(0f)
  private var frameCounter = 0

  companion object {
    private const val PERMISSION_USE_SCENE = "com.oculus.permission.USE_SCENE"
    private const val REQUEST_CODE_SCENE = 101
  }

  override fun registerFeatures(): List<com.meta.spatial.core.Feature> {
    val spatial = SpatialContext.getSpatial()
    return listOf(
        VRFeature(this),
        MRUKFeature(this),
        PhysicsFeature(spatial, useGrabbablePhysics = true)
    )
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        // Scan welcome panel (small format)
        PanelRegistration(
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
                      onCancelScan = if (hasScannedBefore) { { cancelRoomScan() } } else null,
                      onExitApp = { finish() }
                  )
                }
              }
            },
            settingsCreator = {
              UIPanelSettings(
                  shape = QuadShapeOptions(width = 0.7f, height = 0.85f),
                  style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
                  display = DpPerMeterDisplayOptions()
              )
            }
        ),
        // HUD Pilot panel (large dashboard format)
        PanelRegistration(
            R.id.hud_panel,
            composeViewCreator = { _, ctx ->
              ComposeView(ctx).apply {
                setContent {
                  HudPanel(
                      isFlying = droneIsFlying,
                      throttle = droneThrottle,
                      health = droneHealth,
                      speed = droneSpeed,
                      onTogglePower = { toggleDronePower() },
                      onAdjustThrottle = { delta -> adjustDroneThrottle(delta) },
                      onResetPosition = { resetDronePosition() },
                      onExitApp = { finish() }
                  )
                }
              }
            },
            settingsCreator = {
              UIPanelSettings(
                  shape = QuadShapeOptions(width = 1.6f, height = 0.9f),
                  style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
                  display = DpPerMeterDisplayOptions()
              )
            }
        )
    )
  }

  override fun onSpatialConnect() {
    super.onSpatialConnect()

    mrukFeature = feature()
    mrukFeature.addSceneEventListener(this)

    // Set view origin for Quest simulation
    val scene = SpatialContext.getScene()
    scene.setViewOrigin(0.0f, 0.0f, 1.5f, 180.0f)

    spawnPanels()

    // Automatic scan permission check
    if (checkSelfPermission(PERMISSION_USE_SCENE) == PackageManager.PERMISSION_GRANTED) {
      loadSceneFromDevice()
    } else {
      scanState = ScanState.ERROR
    }
  }

  override fun onSpatialShutdown() {
    mrukFeature.removeSceneEventListener(this)
    super.onSpatialShutdown()
  }

  private fun spawnPanels() {
    // Spawns 1.3m in front of eyes
    scanPanelEntity = Entity.createPanelEntity(
        R.id.scan_panel,
        Transform(Pose(Vector3(0f, 1.4f, 1.3f), Quaternion(0f, 180f, 0f))),
        Grabbable(),
        Visible(true)
    )

    // Spawns 1.4m in front of eyes
    hudPanelEntity = Entity.createPanelEntity(
        R.id.hud_panel,
        Transform(Pose(Vector3(0f, 1.3f, 1.4f), Quaternion(0f, 180f, 0f))),
        Grabbable(),
        Visible(false)
    )
  }

  private fun startRoomScan() {
    if (checkSelfPermission(PERMISSION_USE_SCENE) == PackageManager.PERMISSION_GRANTED) {
      scanState = ScanState.SCANNING
      loadSceneFromDevice()
    } else {
      requestPermissions(arrayOf(PERMISSION_USE_SCENE), REQUEST_CODE_SCENE)
    }
  }

  private fun cancelRoomScan() {
    scanState = ScanState.DONE
    scanPanelEntity?.setComponent(Visible(false))
    hudPanelEntity?.setComponent(Visible(true))
  }

  private fun loadSceneFromDevice() {
    mrukFeature.loadSceneFromDevice().thenAccept { result ->
      runOnUiThread {
        if (result == MRUKLoadDeviceResult.SUCCESS) {
          onSceneDataLoaded()
        } else {
          // Fallback to test room asset JSON on failure (e.g. emulator)
          mrukFeature.loadSceneFromAsset("fallback_room.json").thenAccept { assetResult ->
            runOnUiThread {
              if (assetResult) {
                onSceneDataLoaded()
              } else {
                scanState = ScanState.ERROR
              }
            }
          }
        }
      }
    }
  }

  private fun onSceneDataLoaded() {
    scanState = ScanState.DONE
    hasScannedBefore = true
    sceneDataLoaded = true

    // Swap HUD panels
    scanPanelEntity?.setComponent(Visible(false))
    hudPanelEntity?.setComponent(Visible(true))

    spawnDrone()
  }

  private fun spawnDrone() {
    // Spawn the drone in the center (1.2m away, 1.2m high)
    val drone = Entity.create(
        GLXFModel("furniture/RoundLamp.glb"),
        Transform(Pose(Vector3(0f, 1.2f, 1.2f), Quaternion(0f, 180f, 0f))),
        Physics(
            mass = 1.0f,
            dynamic = true,
            friction = 0.5f,
            linearDamping = 0.3f, // Air drag emulation
            angularDamping = 0.4f
        ),
        Drone(throttle = 0.0f, health = 100, isFlying = false),
        Visible(true),
        Grabbable()
    )
    droneEntity = drone
  }

  private fun toggleDronePower() {
    val drone = droneEntity?.tryGetComponent<Drone>() ?: return
    droneIsFlying = !drone.isFlying
    
    // Set entity properties
    drone.isFlying = droneIsFlying
    if (!droneIsFlying) {
      droneThrottle = 0.0f
      drone.throttle = 0.0f
    }
    droneEntity?.setComponent(drone)
  }

  private fun adjustDroneThrottle(delta: Float) {
    val drone = droneEntity?.tryGetComponent<Drone>() ?: return
    if (!drone.isFlying) return

    droneThrottle = (droneThrottle + delta).coerceIn(0.0f, 1.0f)
    drone.throttle = droneThrottle
    droneEntity?.setComponent(drone)
  }

  private fun resetDronePosition() {
    val drone = droneEntity?.tryGetComponent<Drone>() ?: return
    val physics = droneEntity?.tryGetComponent<Physics>() ?: return

    // Telemetry resets
    droneIsFlying = false
    droneThrottle = 0.0f
    droneHealth = 100
    droneSpeed = 0.0f

    // Model resets
    drone.isFlying = false
    drone.throttle = 0.0f
    drone.health = 100
    droneEntity?.setComponent(drone)

    // Physics resets
    physics.linearVelocity = Vector3(0f)
    physics.angularVelocity = Vector3(0f)
    physics.state = PhysicsState.DYNAMIC
    physics.applyForce = Vector3(0f)
    droneEntity?.setComponent(physics)

    // Relocate to safe center space
    droneEntity?.setComponent(Transform(Pose(Vector3(0f, 1.5f, 1.2f), Quaternion(0f, 180f, 0f))))
  }

  override fun onSceneTick() {
    super.onSceneTick()

    if (!sceneDataLoaded || droneEntity == null) return

    val drone = droneEntity?.tryGetComponent<Drone>() ?: return
    val physics = droneEntity?.tryGetComponent<Physics>() ?: return
    val transform = droneEntity?.tryGetComponent<Transform>() ?: return

    // Update Speed display in telemetry
    val velocity = physics.linearVelocity
    droneSpeed = velocity.length()

    frameCounter++

    if (drone.isFlying) {
      // ── FLIGHT PHYSICS ENGINE ──────────────────────────────────────────────
      // Gravity pulls at -9.81m/s^2.
      // E.g., Throttle of 55% matches gravity perfectly for steady hover.
      // > 55% climbs, < 55% falls.
      val thrustY = drone.throttle * 17.5f 
      physics.applyForce = Vector3(0f, thrustY, 0f)
      droneEntity?.setComponent(physics)

      // ── PROPELLER SPINNING AND GYROSCOPE SIMULATION ────────────────────────
      // We apply a minor rotational spin around the Y axis to simulate rotor active state
      val currentPose = transform.transform
      val spinRate = 8.0f * (0.2f + drone.throttle)
      val spinRotation = Quaternion(0f, spinRate, 0f)
      val newRotation = currentPose.q.times(spinRotation)
      droneEntity?.setComponent(Transform(Pose(currentPose.t, newRotation)))

      // ── IMPACT / CRASH DETECTOR ───────────────────────────────────────────
      // Checks for sharp deceleration/velocity change between frames (indicating collision)
      if (frameCounter > 10) {
        val velocityDelta = (lastVelocity - velocity).length()
        if (velocityDelta > 6.0f) { // High velocity impact threshold
          val damage = (velocityDelta * 5).toInt().coerceIn(10, 50)
          droneHealth = (droneHealth - damage).coerceIn(0, 100)
          drone.health = droneHealth
          droneEntity?.setComponent(drone)

          // If drone health drops to 0, kill engines and crash!
          if (droneHealth <= 0) {
            toggleDronePower()
          }
        }
      }
    }

    lastVelocity = velocity
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_CODE_SCENE) {
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        loadSceneFromDevice()
      } else {
        scanState = ScanState.ERROR
      }
    }
  }

  // MRUK Scene Listeners
  override fun onRoomCreated(room: MRUKRoom) {}
  override fun onRoomUpdated(room: MRUKRoom) {}
  override fun onRoomRemoved(room: MRUKRoom) {}
}
