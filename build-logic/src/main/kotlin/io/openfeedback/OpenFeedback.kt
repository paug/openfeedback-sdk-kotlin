package io.openfeedback

import com.android.build.gradle.internal.tasks.factory.dependsOn
import io.openfeedback.extensions.*
import io.openfeedback.extensions.configurePublishingInternal
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.PathSensitivity

open class OpenFeedback(val project: Project) {
    fun Project.configurePublishing(artifactName: String) {
        project.configurePublishingInternal(artifactName)

        val publishIfNeeded = project.rootProject.publishIfNeededTaskProvider()

        val ossStagingReleaseTask = project.registerReleaseTask("ossStagingRelease") {
            dependsOn("publishAllPublicationsToOssStagingRepository")
        }

        val eventName = System.getenv(EnvVarKeys.GitHub.event)
        val ref = System.getenv(EnvVarKeys.GitHub.ref)

        if (eventName == "push" && ref == "refs/heads/main" && project.rootProject.version.toString().endsWith("SNAPSHOT")) {
            project.logger.log(LogLevel.LIFECYCLE, "Deploying snapshot to OssSnapshot...")
            publishIfNeeded.dependsOn(project.tasks.named("publishAllPublicationsToOssSnapshotsRepository"))
        }

        if (ref?.startsWith("refs/tags/") == true) {
            project.logger.log(LogLevel.LIFECYCLE, "Deploying release to OssStaging...")
            publishIfNeeded.dependsOn(ossStagingReleaseTask)
        }
    }
}
