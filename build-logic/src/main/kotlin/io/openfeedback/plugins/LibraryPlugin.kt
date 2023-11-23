package io.openfeedback.plugins

import com.android.build.gradle.LibraryExtension
import io.openfeedback.extensions.configureKotlinAndroid
import io.openfeedback.extensions.configureKotlinCompiler
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class LibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.android")
            apply("org.jetbrains.kotlin.plugin.serialization")
        }

        target.extensions.configure<LibraryExtension> {
            configureKotlinAndroid()
            defaultConfig.targetSdk = 34
        }
        target.tasks.configureKotlinCompiler()
    }
}
