plugins {
    id("io.openfeedback.plugins.lib.multiplatform")
    id("io.openfeedback.plugins.publishing")
    alias(libs.plugins.jetbrains.compose)
}

android {
    namespace = "io.openfeedback.viewmodels"
}

openfeedback {
    configurePublishing("feedback-sdk-viewmodels")
}

kotlin {
    androidTarget()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.openfeedback)
                implementation(compose.runtime)
                api(libs.moko.mvvm.core)
                api(libs.kmm.locale)
            }
        }
        val androidMain by getting
    }
}
