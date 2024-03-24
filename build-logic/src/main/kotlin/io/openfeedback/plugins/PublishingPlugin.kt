package io.openfeedback.plugins

import io.openfeedback.OpenFeedback
import org.gradle.api.Plugin
import org.gradle.api.Project

class PublishingPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("maven-publish")
            apply("signing")
        }
        target.extensions.create("openfeedback", OpenFeedback::class.java)
    }
}
