package io.openfeedback.extensions

import org.gradle.api.JavaVersion
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun TaskContainer.configureKotlinCompiler() = withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}
