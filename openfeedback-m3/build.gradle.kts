plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.compose")
}

library(
    namespace = "io.openfeedback.m3",
    publish = true,
) { kotlinMultiplatformExtension ->
    kotlinMultiplatformExtension.sourceSets {
        findByName("commonMain")!!.apply {
            dependencies {
                api(projects.openfeedback)
                api(projects.openfeedbackViewmodel)

                implementation(libs.moko.resources.compose)
                implementation(libs.moko.mvvm.compose)

                implementation(kotlinMultiplatformExtension.compose.material3)
                implementation(kotlinMultiplatformExtension.compose.ui)
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
