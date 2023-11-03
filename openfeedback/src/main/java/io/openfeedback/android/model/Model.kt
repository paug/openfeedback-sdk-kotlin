package io.openfeedback.android.model

import com.google.firebase.Timestamp

data class Project(
    val chipColors: List<String> = emptyList(),
    val voteItems: List<VoteItem> = emptyList()
)

data class VoteItem(
    val id: String = "",
    val languages: Map<String, String> = emptyMap(),
    val name: String = "",
    val position: Int = 0,
    val type: String = ""
) {
    fun localizedName(language: String): String {
        return languages.getOrElse(language) { name }
    }
}

enum class VoteStatus(val value: String) {
    Active("active"),
    Deleted("deleted")
}

data class SessionVotes(
    val votes: Map<String, Long>,
    val comments: Map<String, Comment>
)

data class Comment(
    val id: String = "",
    val voteItemId: String = "",
    val text: String = "",
    val plus: Long = 0L,
    val createdAt: Timestamp,
    val updatedAt: Timestamp
)
