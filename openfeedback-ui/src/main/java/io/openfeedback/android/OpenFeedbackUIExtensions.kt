package io.openfeedback.android

import io.openfeedback.android.model.UISessionFeedback
import kotlinx.coroutines.flow.combine

/**
 * A bunch of extensions to get UI models from a openFeedback object
 */
internal suspend fun OpenFeedback.getUISessionFeedback(
    sessionId: String,
    language: String
) = combine(
    getProject(),
    getUserVotes(sessionId),
    getTotalVotes(sessionId)
) { project, userVotes, totalVotes ->
    return@combine UISessionFeedbackWithColors(
        OpenFeedbackModelHelper.toUISessionFeedback(project, userVotes, totalVotes, language),
        project.chipColors
    )
}

internal data class UISessionFeedbackWithColors(
    val session: UISessionFeedback,
    val colors: List<String>
)
