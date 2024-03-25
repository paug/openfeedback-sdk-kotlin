plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

library(
    namespace = "io.openfeedback",
    moko = true,
    publish = true
) {
    it.sourceSets {
        getByName("commonMain").apply {
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
        getByName("androidMain"){
            dependencies {
                api(libs.firebase.common)
                api(libs.firebase.firestore)
                implementation(libs.firebase.auth)
            }
        }
    }
}

