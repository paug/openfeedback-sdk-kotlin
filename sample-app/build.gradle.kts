plugins {
    id("io.openfeedback.build.app")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.extra["composeVersion"] as String
    }

    buildFeatures.compose = true
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.activity:activity-compose:1.5.0-alpha01")

    val composeVersion = rootProject.extra["composeVersion"]
    implementation("androidx.compose.material:material:$composeVersion")

    implementation(project(":openfeedback-ui"))
}
