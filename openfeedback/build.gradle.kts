import org.gradle.internal.os.OperatingSystem

plugins {
    id("io.openfeedback.plugins.multiplatform")
}

openfeedback {
    configurePublishing("feedback-android-sdk")
}

kotlin {
    android()

    if (OperatingSystem.current().isMacOsX) {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        ).forEach {
            it.binaries.framework {
                baseName = "openfeedback"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {

            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.kotlin.coroutines.core)
                api(libs.kotlin.coroutines.android)
                api(libs.kotlin.coroutines.play.services)

                // Firestore
                implementation(libs.firebase.firestore)
                implementation(libs.firebase.auth)
            }
        }
        if (OperatingSystem.current().isMacOsX) {
            val iosX64Main by getting
            val iosArm64Main by getting
            val iosSimulatorArm64Main by getting
            val iosMain by creating {
                dependsOn(commonMain)
                iosX64Main.dependsOn(this)
                iosArm64Main.dependsOn(this)
                iosSimulatorArm64Main.dependsOn(this)
                dependencies {
                }
            }
        }
    }
}
