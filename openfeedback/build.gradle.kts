plugins {
    id("io.openfeedback.plugins.lib.multiplatform")
    id("io.openfeedback.plugins.publishing")
    kotlin("native.cocoapods")
    alias(libs.plugins.moko.resources.generator)
}

android {
    namespace = "io.openfeedback"
    sourceSets {
        getByName("main").java.srcDirs("build/generated/moko/androidMain/src")
    }
}

openfeedback {
    configurePublishing("feedback-sdk")
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "OpenFeedbackKit"
            isStatic = true
            export(libs.moko.resources)
        }
    }

    cocoapods {
        version = "1.0"
        ios.deploymentTarget = "14.1"
        noPodspec()
        pod("FirebaseAuth") {
            linkOnly = true
        }
        pod("FirebaseFirestore") {
            linkOnly = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlin.coroutines.core)
                api(libs.kotlinx.datetime)
                api(libs.kotlinx.serialization.json)

                api(libs.gitlive.app)
                api(libs.gitlive.firestore)
                implementation(libs.gitlive.auth)
                implementation(libs.gitlive.common)

                api(libs.moko.resources)

                implementation(libs.kermit)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.firebase.common)
                api(libs.firebase.firestore)
                implementation(libs.firebase.auth)
            }
        }
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "io.openfeedback"
}
