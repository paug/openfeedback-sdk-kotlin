package io.openfeedback.android

import android.util.Log
import io.openfeedback.android.model.Project
import io.openfeedback.android.model.VoteStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine

/**
 * A bunch of helper methods to help bridging the non-coroutine world
 */
fun OpenFeedback.getSessionFeedback(sessionId: String, callback: (Project, List<String>, Map<String, Long>) -> Unit):
        Job {
    return GlobalScope.launch(Dispatchers.Main) {

        val flow = combine(listOf(getProject(), getUserVotes(sessionId), getTotalVotes(sessionId))) { array ->
            array
        }

        flow.collect { array: Array<*> ->
            val project = array[0] as Project?
            if (project == null) {
                Log.e("OpenFeedback", "Cannot retrieve project, please check your projectId")
                return@collect
            }

            val userVotes = array[1] as List<String>
            val totalVotes = array[2] as Map<String, Long>

            callback(project, userVotes, totalVotes)
        }
    }
}

fun OpenFeedback.voteAndForget(sessionId: String, voteItemId: String, vote: Boolean) {
    GlobalScope.launch {
        setVote(sessionId, voteItemId, if (vote) VoteStatus.Active else VoteStatus.Deleted)
    }
}