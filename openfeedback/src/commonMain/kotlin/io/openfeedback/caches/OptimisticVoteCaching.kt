package io.openfeedback.caches

import io.openfeedback.model.SessionVotes
import io.openfeedback.model.VoteStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class OptimisticVoteCaching {
    private val _votes = MutableStateFlow(
        SessionVotes(votes = mutableMapOf(), comments = mutableMapOf())
    )
    val votes: StateFlow<SessionVotes> = _votes

    fun setSessionVotes(votes: SessionVotes) {
        _votes.update { votes }
    }

    fun updateVotes(voteItemId: String, status: VoteStatus) {
        _votes.update {
            val map = it.votes.toMutableMap()
            var count = it.votes.getOrElse(voteItemId) { 0L }
            count += if (status == VoteStatus.Deleted) -1 else 1
            if (count < 0) {
                count = 0L
            }
            map[voteItemId] = count
            it.copy(votes = map)
        }
    }

    fun updateCommentVote(commentItemId: String, status: VoteStatus) {
        _votes.update {
            val map = it.comments.toMutableMap()
            val comment = it.comments[commentItemId] ?: return
            var count = comment.plus
            count += if (status == VoteStatus.Deleted) -1 else 1
            if (count < 0) {
                count = 0L
            }
            map[commentItemId] = comment.copy(plus = count)
            it.copy(comments = map)
        }
    }
}
