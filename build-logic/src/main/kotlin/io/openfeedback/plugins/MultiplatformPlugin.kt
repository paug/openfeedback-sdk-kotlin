package io.openfeedback.plugins

import com.android.build.gradle.LibraryExtension
import io.openfeedback.extensions.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class MultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.multiplatform")
            apply("org.jetbrains.kotlin.plugin.serialization")
        }

        target.extensions.configure<LibraryExtension> {
            configureKotlinAndroid()
            defaultConfig.targetSdk = 34
        }
    }
}
