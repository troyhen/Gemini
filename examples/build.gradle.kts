import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.multiplatform)
}

android {
    namespace = "gemini.examples"
    compileSdk = 37

    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.desktop {
    application {
        mainClass = System.getProperty("mainClass") ?: "examples.AsteroidsKt"

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
        commonMain.dependencies {
            api(project(":gemini"))
            api(libs.compose.foundation)
            implementation(libs.compose.material.icons.extended)
            api(libs.compose.material3)
            api(libs.compose.ui)
            api(libs.kamel.image)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
        }
        androidMain.dependencies {
            implementation(libs.activity)
            implementation(libs.activity.compose)
        }
        named("desktopMain").dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}
