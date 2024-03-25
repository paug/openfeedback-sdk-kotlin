package io.openfeedback.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val chipColors: List<String> = emptyList(),
    val voteItems: List<VoteItem> = emptyList()
)

@Serializable
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

@Serializable
enum class VoteStatus(val value: String) {
    Active("active"),
    Deleted("deleted")
}

@Serializable
data class UserVote(
    val voteItemId: String,
    val voteId: String?
)

@Serializable
data class SessionVotes(
    val votes: Map<String, Long>,
    val comments: Map<String, Comment>
)

@Serializable
data class Comment(
    val id: String = "",
    val voteItemId: String = "",
    val text: String = "",
    val plus: Long = 0L,
    val createdAt: Instant,
    val updatedAt: Instant
)
