plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("io.openfeedback.plugins.publishing")
    id("org.jetbrains.compose")
}

library(
    namespace = "io.openfeedback.m3",
    artifactName = "feedback-sdk-m3"
)

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.openfeedback)
                api(projects.openfeedbackViewmodel)

                implementation(libs.moko.resources.compose)
                implementation(libs.moko.mvvm.compose)

                implementation(compose.material3)
                implementation(compose.ui)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(compose.uiTooling)
            }
        }
    }
}
