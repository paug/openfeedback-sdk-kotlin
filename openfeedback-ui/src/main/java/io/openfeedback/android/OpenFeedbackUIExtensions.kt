package io.openfeedback.android

import io.openfeedback.android.model.UISessionFeedback
import kotlinx.coroutines.Job

/**
 * A bunch of extensions to get UI models from a openFeedback object
 */

fun OpenFeedback.getUISessionFeedback(sessionId: String, callback: (UISessionFeedback, List<String>) -> Unit): Job {
    return getSessionFeedback(sessionId) { project, userVotes, totalVotes ->
        callback(OpenFeedbackModelHelper.toUISessionFeedback(project, userVotes, totalVotes), project.chipColors)
    }
}