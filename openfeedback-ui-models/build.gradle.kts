plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

library(
    namespace = "io.openfeedback.ui.models",
    compose = true,
    publish = true,
) { kotlinMultiplatformExtension ->
    kotlinMultiplatformExtension.sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(kotlinMultiplatformExtension.compose.runtime)
                api(libs.vanniktech.multiplatform.locale)
                api(libs.jetbrains.kotlinx.collections.immutable)
            }
        }
    }
}