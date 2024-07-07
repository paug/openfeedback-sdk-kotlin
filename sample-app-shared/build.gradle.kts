import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("com.android.library")
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
                    implementation(compose.ui)
                    implementation(compose.foundation)
                    implementation(compose.runtime)
                    implementation(projects.openfeedbackViewmodel)
                    implementation(compose.material3)
                }
            }
        }
    }
}

