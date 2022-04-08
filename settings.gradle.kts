pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
}

includeBuild("build-logic")

include(":openfeedback", ":openfeedback-ui", ":sample-app")

