plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

library(
    namespace = "io.openfeedback.resources",
    publish = true,
) {
    it.sourceSets {
        findByName("commonMain")!!.apply {
            dependencies {
                implementation(it.compose.ui)
                api(it.compose.components.resources)

                api(libs.lyricist)
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "io.openfeedback.resources"
    generateResClass = always
}
