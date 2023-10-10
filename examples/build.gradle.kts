import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
//    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.multiplatform)
}

//android {
//    defaultConfig {
//
//    }
//}

kotlin {
//    android()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":gemini"))
                implementation(compose.desktop.currentOs)
                implementation(libs.kamel.image)
//                implementation(libs.ktor.client)
//                implementation(libs.ktor.client.okhttp)
            }
        }
//        val androidMain by getting {
//            dependsOn(commonMain)
//            implementation(compose.foundation)
//            implementation(libs.activity.compose)
//        }
        val desktopMain by getting {
            dependsOn(commonMain)
        }
    }
}

//dependencies {
//    implementation(project(":gemini"))
//    implementation(compose.desktop.currentOs)
//    implementation(libs.kamel.image)
//    implementation(libs.ktor.client)
//    implementation(libs.ktor.client.okhttp)
//    testImplementation(platform("org.junit:junit-bom:5.9.1"))
//    testImplementation("org.junit.jupiter:junit-jupiter")
//}

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

//tasks.test {
//    useJUnitPlatform()
//}
//val compileKotlin: KotlinCompile by tasks
//compileKotlin.kotlinOptions {
//    jvmTarget = "17"
//}
//val compileTestKotlin: KotlinCompile by tasks
//compileTestKotlin.kotlinOptions {
//    jvmTarget = "17"
//}