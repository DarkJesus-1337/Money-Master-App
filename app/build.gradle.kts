import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    alias(libs.plugins.google.firebase.crashlytics)
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.pixelpioneer.moneymaster"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pixelpioneer.moneymaster"
        minSdk = 24
        targetSdk = 35
        versionCode = project.property("app.version.code").toString().toInt()
        versionName = project.property("app.version.name").toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "OCR_SPACE_API_KEY",
            "\"\""
        )

        buildConfigField(
            "String",
            "COINCAP_API_KEY",
            "\"\""
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
            pickFirsts += "**/libimage_processing_util_jni.so"
            pickFirsts += "**/libc++_shared.so"
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:2.0.20")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.20")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.20")
    }
}

val updateJsonTask by tasks.registering {
    val versionName = project.property("app.version.name").toString()
    val changelog = project.property("app.version.changelog").toString()
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
    val updateJsonFile = rootProject.file("assets/update.json")

    doLast {
        if (updateJsonFile.exists()) {
            try {
                val jsonContent = """{
  "version": "$versionName",
  "changelog": "$changelog",
  "apkUrl": "https://github.com/DarkJesus-1337/Money-Master-App/releases/download/v$versionName/app-release.apk"
}"""

                updateJsonFile.writeText(jsonContent)
                println("✅ update.json erfolgreich aktualisiert auf Version $versionName")

            } catch (e: Exception) {
                println("❌ Fehler beim Aktualisieren der update.json: ${e.message}")
            }
        } else {
            println("❌ update.json nicht gefunden am Pfad: ${updateJsonFile.absolutePath}")
        }
    }
}

tasks.named("preBuild") {
    dependsOn(updateJsonTask)
}

dependencies {
    // Basic-Dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.timber)

    // Navigation for Compose
    implementation(libs.androidx.navigation.compose)

    // Room for Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.androidx.constraintlayout)
    ksp(libs.androidx.room.compiler)

    // ViewModel and LiveData
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // UI & Graphics
    implementation(libs.coil.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui.text.google.fonts)

    // Utils
    implementation(libs.kotlinx.datetime)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)

    // Charts
    implementation(libs.mpandroidchart)

    // ML Kit Text Recognition
    implementation(libs.play.services.mlkit.text.recognition)
    implementation(libs.text.recognition)

    // Camera
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Other Android Components
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.exifinterface)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config.ktx)
    implementation(libs.firebase.config)
    implementation(libs.firebase.appdistribution)
    implementation(libs.firebase.crashlytics)

    // Hilt Dependencies
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.androidx.hilt.compiler)

    // SPLASH SCREEN API FOR ANDROID 12+
    implementation(libs.androidx.core.splashscreen)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}