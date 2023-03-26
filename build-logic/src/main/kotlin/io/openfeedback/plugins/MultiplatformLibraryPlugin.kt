package io.openfeedback.plugins

import com.android.build.gradle.LibraryExtension
import io.openfeedback.extensions.configureKotlinAndroid
import io.openfeedback.extensions.configureKotlinCompiler
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class MultiplatformLibraryPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }
            extensions.configure<LibraryExtension> {
                configureKotlinAndroid()
                sourceSets.getByName("main").manifest.srcFile("src/androidMain/AndroidManifest.xml")
                defaultConfig.targetSdk = 32
            }
            target.tasks.configureKotlinCompiler()
        }
    }
}