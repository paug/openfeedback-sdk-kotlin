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
internal enum class VoteStatus(val value: String) {
    Active("active"),
    Deleted("deleted")
}

/** Raw user vote document, used only for deserialization purposes */
@Serializable
internal data class RawUserVote(
    val id: String,
    val projectId: String,
    val talkId: String,
    val voteId: String?,
    val voteItemId: String,
    val text: String?,
    val userId: String?,
    val status: String,
    val voteType: String?,
)

internal fun RawUserVote.toUserVote(): UserVote = when {
    text != null -> UserVote.TextVote(id, projectId, talkId, userId, status, voteItemId, text)
    voteId != null -> UserVote.CommentUpvote(id, projectId, talkId, userId, status, voteItemId, commentId = voteId)
    else -> UserVote.BooleanVote(id, projectId, talkId, userId, status, voteItemId)
}

internal sealed class UserVote {
    abstract val id: String
    abstract val projectId: String
    abstract val talkId: String
    abstract val userId: String?
    abstract val status: String
    abstract val voteItemId: String

    data class BooleanVote(
        override val id: String,
        override val projectId: String,
        override val talkId: String,
        override val userId: String?,
        override val status: String,
        override val voteItemId: String,
    ) : UserVote()

    data class TextVote(
        override val id: String,
        override val projectId: String,
        override val talkId: String,
        override val userId: String?,
        override val status: String,
        override val voteItemId: String,
        val text: String,
    ) : UserVote()

    data class CommentUpvote(
        override val id: String,
        override val projectId: String,
        override val talkId: String,
        override val userId: String?,
        override val status: String,
        override val voteItemId: String,
        val commentId: String,
    ) : UserVote()
}

/**
 * Not serializable using kotlinx-serialization because there is no type discriminator
 * See https://github.com/Kotlin/kotlinx.serialization/issues/2223
 */
//@Serializable
internal sealed interface SessionThing

internal class VoteItemCount(val count: Long): SessionThing

/**
 * A SessionThing representing all the comments for that session
 */
internal class CommentsMap(
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
