package io.openfeedback.build

import org.gradle.api.Plugin
import org.gradle.api.Project

class AppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.apply(mapOf("plugin" to "com.android.application"))
        target.apply(mapOf("plugin" to "org.jetbrains.kotlin.android"))
    }
}

