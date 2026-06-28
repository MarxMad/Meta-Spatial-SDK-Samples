plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.meta.spatial.plugin)
  alias(libs.plugins.jetbrains.kotlin.plugin.compose)
}

android {
  namespace = "com.meta.roomcast"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.meta.roomcast"
    minSdk = 34
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    ndkVersion = "27.0.12077973"
  }

  packaging { resources.excludes.add("META-INF/LICENSE") }

  lint {
    abortOnError = false
    checkReleaseBuilds = false
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  buildFeatures {
    buildConfig = true
    compose = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  kotlinOptions { jvmTarget = "17" }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)

  // Meta Spatial SDK
  implementation(libs.meta.spatial.sdk.base)
  implementation(libs.meta.spatial.sdk.compose)
  implementation(libs.meta.spatial.sdk.ovrmetrics)
  implementation(libs.meta.spatial.sdk.toolkit)
  implementation(libs.meta.spatial.sdk.physics)
  implementation(libs.meta.spatial.sdk.vr)
  implementation(libs.meta.spatial.sdk.isdk)
  implementation(libs.meta.spatial.sdk.mruk)
  implementation(libs.meta.spatial.sdk.castinputforward)
  implementation(libs.meta.spatial.sdk.hotreload)
  implementation(libs.meta.spatial.sdk.datamodelinspector)

  // Compose
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.ui.tooling.preview)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}
