plugins {
    id("io.openfeedback.plugins.app")
    alias(libs.plugins.jetbrains.compose)
}

android {
    namespace = "io.openfeedback.android.sample"
    defaultConfig {
        versionCode = 1
        versionName = "1"
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.activity:activity-compose:1.7.0-alpha02")

    implementation(compose.material)
    implementation(compose.material3)

    implementation(projects.openfeedbackM2)
    implementation(projects.openfeedbackM3)
}
