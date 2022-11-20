package io.openfeedback.build

import com.android.build.gradle.internal.tasks.factory.dependsOn
import io.openfeedback.EnvVarKeys
import io.openfeedback.extensions.configurePublishingInternal
import io.openfeedback.extensions.publishIfNeededTaskProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

class LibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.android")
            apply("maven-publish")
            apply("signing")
        }

        target.extensions.create("openfeedback", OpenFeedback::class.java, target)
    }
}

open class OpenFeedback(val project: Project) {
    fun Project.configurePublishing(artifactName: String) {
        project.configurePublishingInternal(artifactName)

        val publishIfNeeded = project.rootProject.publishIfNeededTaskProvider()

        val eventName = System.getenv(EnvVarKeys.GitHub.event)
        val ref = System.getenv(EnvVarKeys.GitHub.ref)
        project.logger.log(LogLevel.LIFECYCLE, "publishIfNeeded eventName=$eventName ref=$ref")

        if (eventName == "push" && ref == "refs/heads/main") {
            project.logger.log(LogLevel.LIFECYCLE, "Deploying snapshot to OssSnapshot...")
            publishIfNeeded.dependsOn(project.tasks.named("publishAllPublicationsToOssSnapshotsRepository"))
        }

        if (ref?.startsWith("refs/tags/") == true) {
            project.logger.log(LogLevel.LIFECYCLE, "Deploying release to OssStaging...")
            publishIfNeeded.dependsOn(project.tasks.named("publishAllPublicationsToOssStagingRepository"))
        }
    }
}
