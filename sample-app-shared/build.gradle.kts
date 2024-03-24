import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

library(
    namespace =  "io.openfeedback.shared",
    artifactName = null,
    moko = true
)

kotlin {
    targets.configureEach {
        if (this is KotlinNativeTarget) {
            binaries {
                this.framework {
                    baseName = "SampleApp"
                    isStatic = true

//                linkerOpts()
//                freeCompilerArgs += listOf(
//                    "-linker-option", "-framework", "-linker-option", "Metal",
//                    "-linker-option", "-framework", "-linker-option", "CoreText",
//                    "-linker-option", "-framework", "-linker-option", "CoreGraphics"
//                )

                }
            }
        }
    }

    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.runtime)
                implementation(projects.openfeedbackM3)
                implementation(compose.material3)
            }
        }
    }
}

