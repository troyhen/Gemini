plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.multiplatform)
}

android {
    namespace = "gemini"
    compileSdk = 34
    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {

//    explicitApi = ExplicitApiMode.Warning

    androidTarget()
//    androidTarget {
//        publishAllLibraryVariants()
//    }
    jvm()
//    js(IR) {
//        browser()
//    }
//    for (target in Targets.nativeTargets) {
//        targets.add(presets.getByName(target).createTarget(target))
//    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(libs.coroutines.core)
                api(libs.kamel.image)
                api(libs.ktor.client.core)
                api(libs.ktor.client.logging)
            }
        }

        val commonTest by getting {
            dependencies {
//                testImplementation(platform("org.junit:junit-bom:5.9.1"))
//                testImplementation("org.junit.jupiter:junit-jupiter")
//                implementation(project(":kamel-tests"))
//                implementation(kotlin("test"))
//                implementation(libs.ktor.client.mock)
//                implementation(libs.kotlinx.coroutines.test)
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                api(libs.ktor.client.okhttp)
            }
        }

        val jvmMain by getting {
            dependsOn(commonMain)
            dependencies {
                api(libs.ktor.client.java)
            }
        }
    }
}

//dependencies {
//    implementation(compose.desktop.common)
//    implementation(libs.kamel.image)
//    implementation(libs.ktor.client)
//    implementation(libs.ktor.client.okhttp)
//    testImplementation(platform("org.junit:junit-bom:5.9.1"))
//    testImplementation("org.junit.jupiter:junit-jupiter")
//}

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
