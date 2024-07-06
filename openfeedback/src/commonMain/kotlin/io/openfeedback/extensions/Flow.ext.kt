package io.openfeedback.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

/**
 * A variation of take(1) that does not cancel the flow so that the query continues running and
 * network results get written
 */
internal fun <T> Flow<T>.filterFirst(): Flow<T> {
    var first = true
    return mapNotNull {
        if (first) {
            first = false
            it
        } else {
            null
        }
    }
}
