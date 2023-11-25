
plugins {
    id("io.openfeedback.plugins.lib.multiplatform")
    id("io.openfeedback.plugins.publishing")
    alias(libs.plugins.jetbrains.compose)
}

android {
    namespace = "io.openfeedback.m3"
}

openfeedback {
    configurePublishing("feedback-sdk-m3")
}

kotlin {
    androidTarget()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.openfeedback)
                api(projects.openfeedbackViewmodel)

                implementation(libs.moko.resources.compose)
                implementation(libs.moko.mvvm.compose)

                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.uiTooling)
            }
        }
        val androidMain by getting
    }
}
