import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "gemini"
version = "1.0-SNAPSHOT"

plugins {
    alias(libs.plugins.compose)
    kotlin("jvm") version libs.versions.kotlin
}

dependencies {
    implementation(project(":gemini"))
    implementation(compose.desktop.currentOs)
    implementation(libs.kamel.image)
    implementation(libs.ktor.client)
    implementation(libs.ktor.client.okhttp)
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Gemini"
            packageVersion = "1.0.0"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}