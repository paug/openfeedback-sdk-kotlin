import io.openfeedback.extensions.configureRoot

plugins {
    id("io.openfeedback.plugins.compose.lib") apply false
}

version = "0.1.2"
subprojects {
    repositories {
        google()
        mavenCentral()
    }
}

configureRoot()