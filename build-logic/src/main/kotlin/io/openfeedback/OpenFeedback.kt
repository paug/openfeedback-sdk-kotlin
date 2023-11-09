package io.openfeedback

import com.android.build.gradle.internal.tasks.factory.dependsOn
import io.openfeedback.extensions.closeAndReleaseStagingRepository
import io.openfeedback.extensions.configurePublishingInternal
import io.openfeedback.extensions.getOssStagingRepoId
import io.openfeedback.extensions.publishIfNeededTaskProvider
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.PathSensitivity

open class OpenFeedback(val project: Project) {
    fun Project.configurePublishing(artifactName: String) {
        project.configurePublishingInternal(artifactName)

        val publishIfNeeded = project.rootProject.publishIfNeededTaskProvider()

        val ossStagingReleaseTask = project.tasks.register("ossStagingRelease") {
            dependsOn("publishAllPublicationsToOssStagingRepository")
            inputs.property("repoId", getOssStagingRepoId() ?: error("You need to publish to OssStaging in the same Gradle invocation before calling closeAndReleaseStagingRepository."))
            doLast {
                closeAndReleaseStagingRepository(inputs.properties.get("repoId") as String)
            }
        }

        val eventName = System.getenv(EnvVarKeys.GitHub.event)
        val ref = System.getenv(EnvVarKeys.GitHub.ref)

        if (eventName == "push" && ref == "refs/heads/main") {
            project.logger.log(LogLevel.LIFECYCLE, "Deploying snapshot to OssSnapshot...")
            publishIfNeeded.dependsOn(project.tasks.named("publishAllPublicationsToOssSnapshotsRepository"))
        }

        if (ref?.startsWith("refs/tags/") == true) {
            project.logger.log(LogLevel.LIFECYCLE, "Deploying release to OssStaging...")
            publishIfNeeded.dependsOn(ossStagingReleaseTask)
        }
    }
}
