buildscript {
    project.extra.set("composeVersion", "1.2.0-alpha02")
}

version = "0.0.7"
subprojects {
    repositories {
        google()
        mavenCentral()
    }
}
