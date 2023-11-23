plugins {
    id("io.openfeedback.plugins.lib.multiplatform")
    id("io.openfeedback.plugins.publishing")
    alias(libs.plugins.moko.resources.generator)
}

android {
    namespace = "io.openfeedback"
    sourceSets {
        getByName("main").java.srcDirs("build/generated/moko/androidMain/src")
    }
}

openfeedback {
    configurePublishing("feedback-android-sdk")
}

kotlin {
    androidTarget()

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
