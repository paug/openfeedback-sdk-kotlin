@file:Suppress("UnstableApiUsage")

package io.openfeedback.extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun CommonExtension<*, *, *, *, *>.configureKotlinAndroid() {
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

internal fun CommonExtension<*, *, *, *, *>.configureAndroidCompose(project: Project) {
    val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.findVersion("androidx_compose_compiler").get().toString()
    }

    project.dependencies {
        add("debugImplementation", "androidx.customview:customview-poolingcontainer:1.0.0")
        add("debugImplementation", "androidx.savedstate:savedstate-ktx:1.2.0")
    }
}
