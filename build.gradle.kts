import io.openfeedback.extensions.configureRoot

plugins {
    id("io.openfeedback.plugins.compose.lib") apply false
}

version = "0.2.0-SNAPSHOT"
subprojects {
    repositories {
        google()
        mavenCentral()
    }
}

configureRoot()