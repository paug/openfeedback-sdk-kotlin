package io.openfeedback.resources

data class Strings(
    val notReady: NotReadyStrings,
    val comments: CommentStrings,
    val poweredBy: String,
    val fromYou: String
)

data class NotReadyStrings(
    val title: String,
    val description: String
)

data class CommentStrings(
    val titleSection: String,
    val titleInput: String,
    val actionSend: String,
    val nbVotes: (nbVotes: Int) -> String,
)
