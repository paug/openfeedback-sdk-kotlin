package io.openfeedback.plugins

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import io.openfeedback.extensions.configureAndroidCompose
import io.openfeedback.extensions.configureKotlinAndroid
import io.openfeedback.extensions.configureKotlinCompiler
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
        }

        target.extensions.configure<BaseAppModuleExtension> {
            configureKotlinAndroid()
            configureAndroidCompose(target)
        }
        target.tasks.configureKotlinCompiler()
    }
}
