plugins {
    `embedded-kotlin`
}

group = "build-logic"

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation(libs.vespene)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.jetbrains.compose.compiler.gradle.plugin)
    implementation(libs.kotlin.serialization.plugin)
    implementation(libs.moko.gradle.plugin)
    implementation(libs.jetbrains.compose)
}
