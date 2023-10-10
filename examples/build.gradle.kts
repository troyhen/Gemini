import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.multiplatform)
}

android {
    namespace = "gemini.examples"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
    }
}

compose.desktop {
    application {
        mainClass = "examples.asteroids.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Gemini"
            packageVersion = "1.0.0"
        }
    }
}

kotlin {
    androidTarget()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":gemini"))
                implementation(compose.desktop.currentOs)
                implementation(libs.kamel.image)
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(libs.activity)
                implementation(libs.activity.compose)
            }
        }
        val desktopMain by getting {
            dependsOn(commonMain)
        }
    }
}
