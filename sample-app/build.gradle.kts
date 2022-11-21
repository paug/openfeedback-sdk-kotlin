plugins {
    id("io.openfeedback.plugins.app")
}

android {
    defaultConfig {
        versionCode = 1
        versionName = "1"
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.activity:activity-compose:1.7.0-alpha02")

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material)

    implementation(projects.openfeedbackM2)
}
