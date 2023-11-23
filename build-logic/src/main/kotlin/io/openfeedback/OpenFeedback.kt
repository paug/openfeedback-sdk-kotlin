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
    }
}
