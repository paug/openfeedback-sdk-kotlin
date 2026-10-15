plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
}

library(
    namespace = "io.openfeedback.m3",
    compose = true,
) { kotlinMultiplatformExtension ->
    kotlinMultiplatformExtension.sourceSets {
        getByName("commonMain") {
            dependencies {
                api(projects.openfeedbackResources)
                api(projects.openfeedbackUiModels)

                implementation(libs.compose.material3)
                implementation(libs.compose.material.icons.extended)
            }
        }

        getByName("androidMain") {
            dependencies {
                with (kotlinMultiplatformExtension) {
                    implementation(libs.compose.ui.tooling)
                    implementation(libs.compose.ui.tooling.preview)
                }
            }
        }
    }
}
