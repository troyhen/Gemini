group = "gemini"

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
}
//
//buildscript {
//
//}
//repositories {
//    mavenCentral()
//    google()
//    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
//}

subprojects {
    repositories {
        mavenCentral()
        google()
    }
}
