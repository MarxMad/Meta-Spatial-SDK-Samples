package com.meta.spatial.samples.devworkstation

import android.os.Bundle
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
import com.meta.spatial.okhttp3.OkHttpAssetFetcher
import com.meta.spatial.ovrmetrics.OVRMetricsDataModel
import com.meta.spatial.ovrmetrics.OVRMetricsFeature
import com.meta.spatial.runtime.NetworkedAssetLoader
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.samples.devworkstation.ui.EditorPanel
import com.meta.spatial.samples.devworkstation.ui.EnvType
import com.meta.spatial.samples.devworkstation.ui.TerminalPanel
import com.meta.spatial.samples.devworkstation.ui.WebPanel
import com.meta.spatial.samples.devworkstation.ui.WorkspaceDockPanel
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.DpPerMeterDisplayOptions
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.Material
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
import java.io.File

class WorkstationActivity : AppSystemActivity() {

  // Entity references
  private var dockPanelEntity: Entity? = null
  private var editorPanelEntity: Entity? = null
  private var terminalPanelEntity: Entity? = null
  private var browserPanelEntity: Entity? = null
  private var skyboxEntity: Entity? = null

  // Compose observed states
  private var editorOpen by mutableStateOf(false)
  private var terminalOpen by mutableStateOf(false)
  private var browserOpen by mutableStateOf(false)
  private var currentEnv by mutableStateOf(EnvType.PASSTHROUGH)

  override fun registerFeatures(): List<SpatialFeature> {
    val features =
        mutableListOf<SpatialFeature>(
            VRFeature(this),
            ComposeFeature(),
        )
    if (BuildConfig.DEBUG) {
      features.add(CastInputForwardFeature(this))
      features.add(HotReloadFeature(this))
      features.add(OVRMetricsFeature(this, OVRMetricsDataModel() { numberOfMeshes() }))
      features.add(DataModelInspectorFeature(spatial, this.componentManager))
    }
    return features
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Initialize the networked asset loader for remote assets
    NetworkedAssetLoader.init(
        File(applicationContext.getCacheDir().canonicalPath),
        OkHttpAssetFetcher(),
    )
  }

  override fun onSceneReady() {
    super.onSceneReady()

    // Set the reference space so the user is grounded to the floor level
    scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)

    // Set up IBL lighting using the bundled environment file
    scene.setLightingEnvironment(
        ambientColor = Vector3(0f),
        sunColor = Vector3(7.0f, 7.0f, 7.0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.3f,
    )
    scene.updateIBLEnvironment("environment.env")

    // Position the user origin
    scene.setViewOrigin(0.0f, 0.0f, 2.0f, 180.0f)

    // Create the VR Skybox entity (hidden initially — starts in passthrough mode)
    skyboxEntity = Entity.create(
        listOf(
            Mesh("mesh://skybox".toUri(), hittable = MeshCollision.NoCollision),
            Material().apply {
              baseTextureAndroidResourceId = R.drawable.skydome
              unlit = true
            },
            Transform(Pose(Vector3(x = 0f, y = 0f, z = 0f))),
            Visible(false)
        )
    )

    // Start with passthrough so the user sees their real room
    scene.enablePassthrough(true)

    // Create all floating workspace panels
    spawnPanels()
  }

  private fun spawnPanels() {
    // 1. Control Dock — always visible, sits slightly in front and below eye level
    dockPanelEntity = Entity.createPanelEntity(
        R.id.dock_panel,
        Transform(Pose(Vector3(0f, 1.0f, 0.8f), Quaternion(0f, 180f, 0f))),
        Grabbable(),
        Visible(true)
    )

    // 2. Editor Panel — large centered panel in front, starts hidden
    editorPanelEntity = Entity.createPanelEntity(
        R.id.editor_panel,
        Transform(Pose(Vector3(0f, 1.4f, 1.5f), Quaternion(0f, 180f, 0f))),
        Grabbable(),
        Visible(false)
    )

    // 3. Terminal Panel — to the left, angled toward the user
    terminalPanelEntity = Entity.createPanelEntity(
        R.id.terminal_panel,
        Transform(Pose(Vector3(-1.1f, 1.3f, 1.3f), Quaternion(0f, 145f, 0f))),
        Grabbable(),
        Visible(false)
    )

    // 4. Browser Panel — to the right, angled toward the user
    browserPanelEntity = Entity.createPanelEntity(
        R.id.web_panel,
        Transform(Pose(Vector3(1.1f, 1.3f, 1.3f), Quaternion(0f, -145f, 0f))),
        Grabbable(),
        Visible(false)
    )
  }

  // ── Panel visibility toggles ──────────────────────────────────────────────

  private fun toggleEditor() {
    editorOpen = !editorOpen
    editorPanelEntity?.setComponent(Visible(editorOpen))
  }

  private fun toggleTerminal() {
    terminalOpen = !terminalOpen
    terminalPanelEntity?.setComponent(Visible(terminalOpen))
  }

  private fun toggleBrowser() {
    browserOpen = !browserOpen
    browserPanelEntity?.setComponent(Visible(browserOpen))
  }

  // ── Skybox / environment switching ───────────────────────────────────────

  private fun selectEnv(env: EnvType) {
    currentEnv = env
    when (env) {
      EnvType.PASSTHROUGH -> {
        scene.enablePassthrough(true)
        skyboxEntity?.setComponent(Visible(false))
      }
      EnvType.CYBER_CAFE -> {
        scene.enablePassthrough(false)
        updateSkybox(R.drawable.cyber_cafe)
      }
      EnvType.NEON_OFFICE -> {
        scene.enablePassthrough(false)
        updateSkybox(R.drawable.neon_office)
      }
      EnvType.ZEN_NATURE -> {
        scene.enablePassthrough(false)
        updateSkybox(R.drawable.skydome)
      }
    }
  }

  private fun updateSkybox(drawableId: Int) {
    skyboxEntity?.let { entity ->
      entity.setComponent(Visible(true))
      val material = entity.tryGetComponent<Material>() ?: Material()
      material.baseTextureAndroidResourceId = drawableId
      material.unlit = true
      entity.setComponent(material)
    }
  }

  // ── Panel registrations ────────────────────────────────────────────────────

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        // Control Dock
        ComposeViewPanelRegistration(
            R.id.dock_panel,
            composeViewCreator = { _, ctx ->
              ComposeView(ctx).apply {
                setContent {
                  WorkspaceDockPanel(
                      editorOpen = editorOpen,
                      terminalOpen = terminalOpen,
                      browserOpen = browserOpen,
                      currentEnv = currentEnv,
                      onToggleEditor = { toggleEditor() },
                      onToggleTerminal = { toggleTerminal() },
                      onToggleBrowser = { toggleBrowser() },
                      onSelectEnv = { selectEnv(it) }
                  )
                }
              }
            },
            settingsCreator = {
              UIPanelSettings(
                  shape = QuadShapeOptions(width = 1.0f, height = 0.3f),
                  style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
                  display = DpPerMeterDisplayOptions(),
              )
            },
        ),
        // Code Editor
        ComposeViewPanelRegistration(
            R.id.editor_panel,
            composeViewCreator = { _, ctx ->
              ComposeView(ctx).apply { setContent { EditorPanel() } }
            },
            settingsCreator = {
              UIPanelSettings(
                  shape = QuadShapeOptions(width = 1.2f, height = 0.8f),
                  style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
                  display = DpPerMeterDisplayOptions(),
              )
            },
        ),
        // Terminal
        ComposeViewPanelRegistration(
            R.id.terminal_panel,
            composeViewCreator = { _, ctx ->
              ComposeView(ctx).apply { setContent { TerminalPanel() } }
            },
            settingsCreator = {
              UIPanelSettings(
                  shape = QuadShapeOptions(width = 0.9f, height = 0.6f),
                  style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
                  display = DpPerMeterDisplayOptions(),
              )
            },
        ),
        // Web Browser
        ComposeViewPanelRegistration(
            R.id.web_panel,
            composeViewCreator = { _, ctx ->
              ComposeView(ctx).apply { setContent { WebPanel() } }
            },
            settingsCreator = {
              UIPanelSettings(
                  shape = QuadShapeOptions(width = 0.9f, height = 0.6f),
                  style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
                  display = DpPerMeterDisplayOptions(),
              )
            },
        )
    )
  }
}
