import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.compose)
//    kotlin("jvm") version libs.versions.kotlin
    kotlin("jvm") version libs.versions.kotlin
}

dependencies {
    implementation(compose.desktop.common)
    implementation(libs.kamel.image)
    implementation(libs.ktor.client)
    implementation(libs.ktor.client.okhttp)
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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
