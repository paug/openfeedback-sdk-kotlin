import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

library(
    namespace = "io.openfeedback.shared",
    compose = true
) { kotlinMultiplatformExtension ->
    with(kotlinMultiplatformExtension) {
        targets.forEach {
            if (it is KotlinNativeTarget) {
                it.binaries {
                    this.framework {
                        baseName = "SampleApp"
                        isStatic = true
                    }
                }
            }
        }

        kotlinMultiplatformExtension.sourceSets {
            getByName("commonMain") {
                dependencies {
                    implementation(libs.compose.foundation)
                    implementation(libs.compose.material3)
                    implementation(libs.compose.runtime)
                    implementation(libs.compose.ui)
                    implementation(projects.openfeedbackViewmodel)
                }
            }
        }
    }
}

