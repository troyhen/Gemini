pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
//        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "Gemini"

include("examples", "gemini")
