@file:Suppress("UnstableApiUsage")

package io.openfeedback.extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion

internal fun CommonExtension<*, *, *, *, *>.configureKotlinAndroid() {
    compileSdk = 34

    defaultConfig {
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
