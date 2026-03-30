plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

androidApp("io.openfeedback.android")

android {
    defaultConfig {
        versionCode = 1
        versionName = "1"
    }
}

dependencies {
    implementation(projects.openfeedbackViewmodel)
    implementation(projects.sampleAppShared)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)

    implementation(compose.material3)
}
