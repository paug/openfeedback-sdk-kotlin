pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

includeBuild("build-logic")

include(
    ":openfeedback",
    ":openfeedback-viewmodel",
    ":openfeedback-m3",
    ":sample-app-android"
)

