plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    alias(libs.plugins.jetbrains.compose)
}

android {
    namespace = "io.openfeedback.shared"
}

configureAndroid()
configureKotlin()

kotlin {
    androidTarget()
    listOf(iosArm64(), iosSimulatorArm64()).forEach {

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

