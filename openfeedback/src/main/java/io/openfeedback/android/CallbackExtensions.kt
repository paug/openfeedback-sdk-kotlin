package io.openfeedback.android

import android.util.Log
import io.openfeedback.android.model.Project
import io.openfeedback.android.model.VoteStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

/**
 * A bunch of helper methods to help bridging the non-coroutine world
 */
fun OpenFeedback.getSessionFeedback(sessionId: String, callback: (Project, List<String>, Map<String, Long>) -> Unit): Job {
    return GlobalScope.launch(Dispatchers.Main) {

        combine(listOf(getProject(), getUserVotes(sessionId), getTotalVotes(sessionId))) { array ->
            val project = array[0].snapshot as Project?
            if (project == null) {
                Log.e("OpenFeedback", "Cannot retrieve project, please check your projectId")
                return@combine
            }

            val userVotes = array[1] as List<String>
            val totalVotes = array[2] as Map<String, Long>

            callback(project, userVotes, totalVotes)

            if (array.count { it.fromCache } == 0) {
                //cancel()
            }
        }
    }
}

fun OpenFeedback.voteAndForget(sessionId: String, voteItemId: String, vote: Boolean) {
    GlobalScope.launch {
        setVote(sessionId, voteItemId, if (vote) VoteStatus.Active else VoteStatus.Deleted)
    }
}