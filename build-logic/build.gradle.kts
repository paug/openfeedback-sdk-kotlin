plugins {
    `embedded-kotlin`
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(libs.vespene)
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.serialization.plugin)
}

gradlePlugin {
    plugins {
        register("io.openfeedback.plugins.lib") {
            id = "io.openfeedback.plugins.lib"
            implementationClass = "io.openfeedback.plugins.LibraryPlugin"
        }
        register("io.openfeedback.plugins.compose.lib") {
            id = "io.openfeedback.plugins.compose.lib"
            implementationClass = "io.openfeedback.plugins.ComposeLibraryPlugin"
        }
        register("io.openfeedback.plugins.app") {
            id = "io.openfeedback.plugins.app"
            implementationClass = "io.openfeedback.plugins.AppPlugin"
        }
    }
}
