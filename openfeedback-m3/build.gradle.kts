plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

library(
    namespace = "io.openfeedback.m3",
    compose = true,
) { kotlinMultiplatformExtension ->
    kotlinMultiplatformExtension.sourceSets {
        findByName("commonMain")!!.apply {
            dependencies {
                api(projects.openfeedbackResources)
                api(projects.openfeedbackUiModels)

                implementation(kotlinMultiplatformExtension.compose.material3)
                implementation(kotlinMultiplatformExtension.compose.materialIconsExtended)
            }
        }
        val androidMain by getting {
            dependencies {
                with (kotlinMultiplatformExtension) {
                    implementation(compose.uiTooling)
                }
            }
        }
    }
}
