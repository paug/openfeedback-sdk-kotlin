rootProject.name = "build-logic"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    listOf(
        repositories,
        dependencyResolutionManagement.repositories,
    ).forEach {
        it.apply {
            mavenCentral()
            google()
            maven("https://storage.googleapis.com/gradleup/m2")
            gradlePluginPortal()
//            mavenLocal()
        }
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
