package io.openfeedback.android.model

import androidx.compose.Model
import io.openfeedback.android.compose.dots
import java.util.*
import kotlin.math.absoluteValue

/**
 * @param x: the x coordinate between 0f and 1f
 * @param y: the y coordinate between 0f and 1f
 * @param color: the color as "rrggbb"
 */
@Model
class UIDot(val x: Float,
                 val y: Float,
                 val color: String)

@Model
class UIVoteItem(
        val id: String,
        val text: String,
        var dots: List<UIDot>,
        var votedByUser: Boolean
)

@Model
class UIComment(
        val message: String,
        val createdAt: Date
)

@Model
class UISessionFeedback(
        val comments: List<UIComment>,
        val voteItem: List<UIVoteItem>
)


