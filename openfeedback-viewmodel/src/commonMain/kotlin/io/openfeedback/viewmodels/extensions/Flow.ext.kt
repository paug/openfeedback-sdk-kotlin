package io.openfeedback.viewmodels.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Allows access to the previous emitted value
 * We use that to have stable dots coordinates
 */
internal fun <T, R : Any> Flow<T>.mapWithPreviousValue(block: (previous: R?, current: T) -> R): Flow<R> {
    var prev: R? = null
    return flow {
        this@mapWithPreviousValue.collect {
            block(prev, it).also {
                emit(it)
                prev = it
            }
        }
    }
}
