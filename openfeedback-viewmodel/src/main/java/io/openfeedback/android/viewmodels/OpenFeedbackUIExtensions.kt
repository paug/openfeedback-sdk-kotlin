package io.openfeedback.android.viewmodels

import io.openfeedback.android.OpenFeedbackConfig
import io.openfeedback.android.viewmodels.models.UISessionFeedback
import kotlinx.coroutines.flow.combine

@Deprecated(
    message = "Use getUISessionFeedback(projectId: String, sessionId: String, language: String) instead of this one.",
    replaceWith = ReplaceWith("getUISessionFeedback(openFeedbackProjectId, sessionId, language)")
)
internal suspend fun OpenFeedbackConfig.getUISessionFeedback(
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

/**
 * A bunch of extensions to get UI models from a openFeedback object
 */
internal suspend fun OpenFeedbackConfig.getUISessionFeedback(
    projectId: String,
    sessionId: String,
    language: String
) = combine(
    getProject(projectId),
    getUserVotes(projectId, sessionId),
    getTotalVotes(projectId, sessionId)
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
