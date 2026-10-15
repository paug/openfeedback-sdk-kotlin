plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
}

library(
    namespace = "io.openfeedback.viewmodels",
    compose = true,
) { kotlinMultiplatformExtension ->
    kotlinMultiplatformExtension.sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(projects.openfeedback)
                api(projects.openfeedbackM3)
                api(projects.openfeedbackUiModels)

                implementation(libs.compose.material3)
                implementation(libs.compose.runtime)
                // Not sure why this is needed 🤷
                implementation(libs.jetbrains.kotlin.stdlib)

                api(libs.androidx.lifecycle.viewmodel.compose)
                api(libs.vanniktech.multiplatform.locale)
            }
        }
        getByName("androidMain") {
            dependencies {
                implementation(libs.compose.ui.tooling.preview)
            }
        }
    }
}
