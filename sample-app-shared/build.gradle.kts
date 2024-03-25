import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

library(
    namespace =  "io.openfeedback.shared",
    artifactName = null,
    moko = true,
) {
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
}

kotlin {
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

