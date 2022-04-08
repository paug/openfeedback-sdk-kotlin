buildscript {
    project.extra.set("composeVersion", "1.2.0-alpha02")
}

version = "0.0.6-SNAPSHOT"

subprojects {
    repositories {
        google()
        mavenCentral()
    }
}
