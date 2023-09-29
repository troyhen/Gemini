group = "gemini"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

subprojects {
    repositories {
        mavenCentral()
    }
}
