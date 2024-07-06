package io.openfeedback.extensions

import io.openfeedback.model.CommentsMap

/**
 * Define 1 up vote for every comments specified in [votedCommentIds] parameter. Then,
 * ensures that we are using the greater up vote value between the [CommentsMap] and the
 * up vote defined by the parameter.
 *
 * @param votedCommentIds List of comment ids.
 * @return New [CommentsMap] instance with new up vote values.
 */
internal fun CommentsMap.coerceAggregations(votedCommentIds: Set<String>): CommentsMap {
    return CommentsMap(all.mapValues {
        val minValue = if (votedCommentIds.contains(it.key)) {
            1L
        } else {
            0L
        }
        it.value.copy(plus = it.value.plus.coerceAtLeast(minValue))
    })
}
