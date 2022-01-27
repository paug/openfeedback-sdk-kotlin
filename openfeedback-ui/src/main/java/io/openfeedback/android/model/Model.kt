package io.openfeedback.android.model

import java.util.*

/**
 * @param x: the x coordinate between 0f and 1f
 * @param y: the y coordinate between 0f and 1f
 * @param color: the color as "rrggbb"
 */
class UIDot(
    val x: Float,
    val y: Float,
    val color: String
)

class UIVoteItem(
    val id: String,
    val text: String,
    val dots: List<UIDot>,
    val votedByUser: Boolean
)

class UIComment(
    val message: String,
    val createdAt: Date
)

class UISessionFeedback(
    val comments: List<UIComment>,
    val voteItem: List<UIVoteItem>
)
