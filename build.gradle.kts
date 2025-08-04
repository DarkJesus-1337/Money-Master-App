plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.24" apply false  // Updated to match Kotlin 2.0.20
    id("com.google.gms.google-services") version "4.4.3" apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false  // Updated Hilt version
}