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
                isStatic = true
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                api(libs.kotlinx.serialization.json)
                api(libs.gitlive.firebase.app)
                implementation(libs.gitlive.firebase.auth)
                implementation(libs.gitlive.firebase.firestore)
                api(libs.gitlive.firebase.common)
            }
        }
        val androidMain by getting {
            dependencies {
                // FIXME https://github.com/GitLiveApp/firebase-kotlin-sdk/issues/356
                api(libs.firebase.common)
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
            }
        }
    }
}
