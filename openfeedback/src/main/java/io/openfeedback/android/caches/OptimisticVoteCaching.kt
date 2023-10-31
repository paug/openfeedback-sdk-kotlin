package io.openfeedback.android.caches

import io.openfeedback.android.model.VoteStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update

class OptimisticVoteCaching {
    private val _votes = MutableStateFlow<Map<String, Long>>(mutableMapOf())
    val votes: StateFlow<Map<String, Long>> = _votes

    fun setVotes(votes: Map<String, Long>) {
        _votes.update { votes }
    }

    fun updateVotes(voteItemId: String, status: VoteStatus) {
        _votes.update {
            val map = it.toMutableMap()
            var count = it.getOrElse(voteItemId) { 0L }
            count += if (status == VoteStatus.Deleted) -1 else 1
            if (count < 0) {
                count = 0L
            }
            map[voteItemId] = count
            map
        }
    }
}
