plugins {
//    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {

//    explicitApi = ExplicitApiMode.Warning

//    androidTarget()
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
                implementation(compose.desktop.common)
                implementation(libs.kamel.image)
                implementation(libs.ktor.client)
                implementation(libs.ktor.client.okhttp)
//                api(project(":kamel-core"))
//                implementation(compose.ui)
//                implementation(compose.foundation)
//                implementation(compose.runtime)
//                implementation(libs.ktor.client.core)
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
