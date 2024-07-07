package io.openfeedback.resources

data class Strings(
    val comments: CommentStrings,
    val poweredBy: String,
    val fromYou: String
)

data class CommentStrings(
    val titleSection: String,
    val titleInput: String,
    val actionSend: String,
    val nbVotes: (nbVotes: Int) -> String,
)
