plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    android {
        namespace = "gemini"
        compileSdk = 37
        minSdk = 23
    }
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.coroutines.core)
            api(libs.kamel.image)
            api(libs.ktor.client.core)
            api(libs.ktor.client.logging)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
        }

        androidMain.dependencies {
            api(libs.ktor.client.okhttp)
        }

        jvmMain.dependencies {
            api(libs.ktor.client.java)
        }
    }
}

tasks.register("test") {
    dependsOn("allTests")
}
