enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
}

includeBuild("build-logic")

include(
    ":openfeedback",
    ":openfeedback-ui",
    ":openfeedback-m2",
    ":openfeedback-m3",
    ":openfeedback-viewmodel",
    ":sample-app"
)

