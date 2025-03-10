plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
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

                implementation(kotlinMultiplatformExtension.compose.material3)
                implementation(kotlinMultiplatformExtension.compose.runtime)
                // Not sure why this is needed 🤷
                implementation(libs.jetbrains.kotlin.stdlib)

                api(libs.androidx.lifecycle.viewmodel.compose)
                api(libs.vanniktech.multiplatform.locale)
            }
        }
    }
}
