
plugins {
    id("io.openfeedback.build.lib")
}

openfeedback {
    configurePublishing("feedback-android-sdk")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    api(libs.kotlin.coroutines.core)
    api(libs.kotlin.coroutines.android)
    api(libs.kotlin.coroutines.play.services)

    // Firestore
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
}
