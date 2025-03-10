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
    implementation(libs.librarian)
    implementation(libs.jetbrains.kotlinx.coroutines)
    implementation(libs.android.gradle.plugin)
    implementation(libs.jetbrains.kotlin.gradle.plugin)
    implementation(libs.jetbrains.compose.compiler.gradle.plugin)
    implementation(libs.jetbrains.kotlin.serialization.plugin)
    implementation(libs.jetbrains.compose.gradle.plugin)
    implementation(libs.jetbrains.kotlinx.binary.compatibility.validator)
    implementation(libs.ben.manes.versions)
    implementation(libs.version.catalog.update)
}
