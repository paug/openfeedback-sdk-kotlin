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
    implementation(projects.openfeedbackM3)

    implementation(libs.androidx.core)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-compose:1.8.1")

    implementation(compose.material3)

}
