plugins {
    id("com.android.application") version "8.7.2" // Android Gradle Plugin
    id("org.jetbrains.kotlin.android") version "2.0.0" // Kotlin Plugin
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" // Compose Plugin
}

android {
    compileSdk = 34 // SDK version

    defaultConfig {
        applicationId = "com.ravago.kargotakip" // Application ID
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.3"

        namespace = "com.ravago.kargotakip" // Namespace (matches applicationId)
    }

    buildFeatures {
        compose = true // Enable Jetpack Compose
        viewBinding = true // Enable ViewBinding
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8" // Compose Compiler version
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11" // Kotlin JVM target
    }
}

dependencies {
    // Jetpack Compose dependencies
    implementation("androidx.compose.ui:ui:1.6.0") // Compose UI
    implementation("androidx.compose.material:material:1.6.0") // Compose Material
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0") // Compose Tooling Preview
    implementation("androidx.activity:activity-compose:1.8.2") // Compose Activity support
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0") // Compose ViewModel support
    implementation("androidx.compose.material3:material3:1.0.0")

    // Other dependencies
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5") // AndroidX Test JUnit
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0") // Compose Testing
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0") // Debug Compose Tooling
}