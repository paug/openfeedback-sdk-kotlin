import io.openfeedback.extensions.configureRoot

plugins {
    id("io.openfeedback.plugins.compose.lib") apply false
    alias(libs.plugins.moko.resources.generator) apply false
}

version = "0.1.1"
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

configureRoot()