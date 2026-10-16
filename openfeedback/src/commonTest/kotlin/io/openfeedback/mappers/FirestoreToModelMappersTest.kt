package io.openfeedback.mappers

import io.openfeedback.model.Comment
import io.openfeedback.model.CommentsMap
import io.openfeedback.model.Project
import io.openfeedback.model.SessionData
import io.openfeedback.model.UserVote
import io.openfeedback.model.VoteItem
import io.openfeedback.model.VoteItemCount
import io.openfeedback.model.VoteStatus
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class FirestoreToModelMappersTest {

    companion object {
        private const val USER_ID = "1"

        private fun booleanVoteItem(id: String) = VoteItem(id = id, name = "Boolean $id", type = "boolean")
        private fun textVoteItem(id: String) = VoteItem(id = id, name = "Text $id", type = "text")

        private fun userVote(voteItemId: String) = UserVote(
            projectId = "",
            talkId = "",
            voteId = null,
            voteItemId = voteItemId,
            text = null,
            userId = USER_ID,
            status = VoteStatus.Active.value
        )

        private fun userCommentUpvote(voteItemId: String, commentId: String) = UserVote(
            projectId = "",
            talkId = "",
            voteId = commentId,
            voteItemId = voteItemId,
            text = null,
            userId = USER_ID,
            status = VoteStatus.Active.value
        )

        private fun comment(id: Int) = Comment(
            id = "COMMENT_$id",
            text = "Comment $id",
            plus = 10,
            createdAt = Instant.fromEpochMilliseconds((1791797145L + id) * 1000),
            updatedAt = Instant.fromEpochMilliseconds((1791797145L + id) * 1000),
            userId = ""
        )
    }

    @Test
    fun test() {
        val project = Project(
            chipColors = emptyList(),
            voteItems = listOf(
                booleanVoteItem("VOTE_OPTION_1"),
                booleanVoteItem("VOTE_OPTION_2"),
                textVoteItem("NEW_COMMENTS")
            )
        )

        val sessionData = mapToSessionData(
            userId = USER_ID,
            project = Project(
                chipColors = emptyList(),
                voteItems = listOf(
                    booleanVoteItem("VOTE_OPTION_1"),
                    booleanVoteItem("VOTE_OPTION_2"),
                    textVoteItem("NEW_COMMENTS")
                )
            ),
            userVotes = listOf(
                // User boolean votes
                userVote("VOTE_OPTION_1"),
                userVote("VOTE_OPTION_2"),
                // User vote on comment map (should not be possible)
                userVote("OLD_COMMENTS"),
                userVote("NEW_COMMENTS"),
                // User vote on a comment within an old comment map
                userCommentUpvote("OLD_COMMENTS", "COMMENT_1"),
                // User vote on a comment within the current comment map
                userCommentUpvote("NEW_COMMENTS", "COMMENT_2"),
            ),
            sessionThings = mapOf(
                "VOTE_OPTION_1" to VoteItemCount(1),
                "VOTE_OPTION_2" to VoteItemCount(5),
                "VOTE_OPTION_THAT_NO_LONGER_EXISTS" to VoteItemCount(10),
                // Comments related to an old comment map
                "OLD_COMMENTS" to CommentsMap(all = mapOf(
                    "COMMENT_10" to comment(10),
                    "COMMENT_11" to comment(11),
                    "COMMENT_12" to comment(12),
                )),
                // Comments related to the actual comment map
                "NEW_COMMENTS" to CommentsMap(all = mapOf(
                    "COMMENT_1" to comment(1),
                    "COMMENT_2" to comment(2),
                    "COMMENT_3" to comment(3),
                ))
            ),
        )

        assertEquals(SessionData(
            project = project,
            userId = USER_ID,
            votedItemIds = setOf(
                "VOTE_OPTION_1",
                "VOTE_OPTION_2",
                "OLD_COMMENTS", // Could probably be filtered-out
            ),
            votedCommentIds = setOf(
                "COMMENT_1",
                "COMMENT_2" // Could probably be filtered-out
            ),
            voteItemAggregates = mapOf(
                "VOTE_OPTION_1" to 1,
                "VOTE_OPTION_2" to 5,
            ),
            comments = listOf(
                comment(3),
                comment(2),
                comment(1),
            )
        ), sessionData)
    }
}