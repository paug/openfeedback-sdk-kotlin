plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("io.openfeedback.plugins.publishing")
    id("org.jetbrains.compose")
}

library(
    namespace = "io.openfeedback.viewmodels",
    artifactName = "feedback-sdk-viewmodels"
)

kotlin {
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
