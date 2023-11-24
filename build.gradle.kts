import io.openfeedback.extensions.configureRoot

plugins {
    id("io.openfeedback.plugins.lib") apply false
    alias(libs.plugins.moko.resources.generator) apply false
    alias(libs.plugins.jetbrains.compose) apply false
}

version = "0.1.2"
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

configureRoot()