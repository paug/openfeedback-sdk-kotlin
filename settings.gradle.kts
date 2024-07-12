rootProject.name = "openfeedback-android-sdk"

pluginManagement {
    listOf(repositories, dependencyResolutionManagement.repositories).forEach {
        it.apply {
            mavenCentral()
            google()
            gradlePluginPortal()
        }
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
