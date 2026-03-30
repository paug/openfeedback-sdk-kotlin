rootProject.name = "openfeedback-sdk-kotlin"

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
