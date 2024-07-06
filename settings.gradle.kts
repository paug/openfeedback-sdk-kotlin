rootProject.name = "openfeedback-android-sdk"

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
    ":openfeedback-ui-models",
    ":openfeedback-m3",
    ":openfeedback-resources",
    ":sample-app-android",
    ":sample-app-shared",
)

