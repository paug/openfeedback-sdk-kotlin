package io.openfeedback

import kotlinx.coroutines.channels.BroadcastChannel

class OptimisticVotes(
    var lastValue: Map<String, Long>?,
    val channel: BroadcastChannel<Map<String, Long>>
)
