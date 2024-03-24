package io.openfeedback

import io.openfeedback.extensions.configurePublishingInternal
import org.gradle.api.Project

open class OpenFeedback {
    fun Project.configurePublishing(artifactName: String) {
        project.configurePublishingInternal(artifactName)
    }
}
