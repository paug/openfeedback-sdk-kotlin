
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
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.0")

    // Firestore
    implementation("com.google.firebase:firebase-firestore:24.0.1")
    implementation("com.google.firebase:firebase-auth:21.0.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.0.1")
}
