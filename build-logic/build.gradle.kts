plugins {
    `embedded-kotlin`
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "build-logic"

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.vespene)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.serialization.plugin)
    implementation(libs.moko.gradle.plugin)
    implementation(libs.jetbrains.compose)

}

gradlePlugin {
    plugins {
        register("io.openfeedback.plugins.publishing") {
            id = "io.openfeedback.plugins.publishing"
            implementationClass = "io.openfeedback.plugins.PublishingPlugin"
        }
    }
}
