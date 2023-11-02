package io.openfeedback.android.caches

import io.openfeedback.android.model.SessionVotes
import io.openfeedback.android.model.VoteStatus
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
}
