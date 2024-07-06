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

/**
 * An user vote. This is a document in firebase.
 * [UserVote] may represent:
 * - a vote on a voteItem
 * - a plus on a comment
 * - a comment
 *
 * Note that this can not represent the absence of a vote.
 *
 * @param voteItemId the voteItemId
 * @param id only if this is an upvote for a comment
 * @param text only if this is a comment
 */
@Serializable
data class UserVote(
    val projectId: String,
    val talkId: String,
    val id: String?,
    val voteItemId: String,
    val text: String?,
    val userId: String?,
    val status: String
)

/**
 * Not serializable using kotlinx-serialization because there is no type discriminator
 * See https://github.com/Kotlin/kotlinx.serialization/issues/2223
 */
//@Serializable
sealed interface SessionThing

class VoteItemCount(val count: Long): SessionThing

/**
 * A SessionThing representing all the comments for that session
 */
class CommentsMap(
    /**
     * The key is the comment.id
     */
    val all: Map<String, Comment>
): SessionThing

data class Comment(
    val id: String,
    val text: String,
    val plus: Long = 0L,
    val createdAt: Instant,
    val updatedAt: Instant,
    val userId: String?,
): SessionThing
