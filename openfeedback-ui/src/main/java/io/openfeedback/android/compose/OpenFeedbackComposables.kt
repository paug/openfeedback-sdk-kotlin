package io.openfeedback.android.compose

import android.util.Log
import androidx.compose.*
import androidx.ui.core.Alignment
import androidx.ui.core.Draw
import androidx.ui.core.Text
import androidx.ui.core.WithConstraints
import androidx.ui.foundation.Border
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.geometry.Offset
import androidx.ui.graphics.*
import androidx.ui.layout.*
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Card
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.toPx
import io.openfeedback.android.OpenFeedback
import io.openfeedback.android.OpenFeedbackModelHelper
import io.openfeedback.android.getUISessionFeedback
import io.openfeedback.android.model.*
import io.openfeedback.android.voteAndForget
import kotlin.math.absoluteValue
import kotlin.random.Random


/**
 * This is not used to display anything the UI itself but it used by click events
 * We could use ambients instead but it crashes at the moment
 */
class UIContext(val openFeedback: OpenFeedback,
              val sessionId: String,
              val colors: List<String>)

@Model
class UIContainer(var sessionFeedback: UISessionFeedback?,
                  var uiContext: UIContext?)

@Composable
fun SessionFeedbackContainer(openFeedback: OpenFeedback,
                             sessionId: String,
                             language: String
) {
    val loading = remember {
        UIContainer(null, null)
    }

    val job = remember {
        openFeedback.getUISessionFeedback(sessionId, language) { sessionFeedback, possibleColors ->
            loading.sessionFeedback = OpenFeedbackModelHelper.keepDotsPosition(loading.sessionFeedback, sessionFeedback, possibleColors)
            loading.uiContext = UIContext(openFeedback, sessionId, possibleColors)
        }
    }

    onCommit(job) {
        onDispose {
            job.cancel()
        }
    }

    if (loading.sessionFeedback == null) {
        Loading()
    } else {
        SessionFeedback(sessionFeedback = loading.sessionFeedback!!, uiContext = loading.uiContext!!)
    }
}

@Composable
fun Loading() {
    Wrap(alignment = Alignment.TopCenter) {
        CircularProgressIndicator()
    }
}

@Composable
fun SessionFeedback(sessionFeedback: UISessionFeedback, uiContext: UIContext?) {
    MaterialTheme {
        Column {
            VoteItems(voteItems = sessionFeedback.voteItem, uiContext = uiContext)
            Container(modifier = LayoutWidth.Fill + LayoutPadding(top = 5.dp, bottom = 10.dp)) {
                PoweredBy()
            }
        }
    }
}

@Composable
fun VoteItems(
        voteItems: List<UIVoteItem>,
        columnCount: Int = 2,
        uiContext: UIContext?
) {
    Container(modifier = LayoutPadding(0.dp)) {
        Row {
            0.until(columnCount).forEach { column ->
                Container(modifier = LayoutFlexible(flex = 1f)) {
                    Column {
                        voteItems.filterIndexed { index, _ ->
                            index % columnCount == column
                        }.forEach { voteItem ->
                            Ripple(bounded = true) {
                                Clickable(consumeDownOnStart = false,
                                        onClick = {
                                            if (uiContext != null) {
                                                uiContext.openFeedback.voteAndForget(sessionId = uiContext.sessionId,
                                                        voteItemId = voteItem.id,
                                                        vote = !voteItem.votedByUser)
                                            }
                                        }) {
                                    VoteCard(voteItem)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PoweredBy() {
    Row {
        Text(text = "Powered by OpenFeedback",
                style = TextStyle(
                        color = Color(0, 0, 0, 200),
                        textAlign = TextAlign.Center
                )
        )
        // Uncomment when this does not make the compiler crash
        //DrawImage(image = imageResource(R.drawable.test))
    }
}

@Composable
fun VoteCard(voteModel: UIVoteItem) {
    val b = 240
    val border = if (voteModel.votedByUser) {
        4.dp
    } else {
        0.dp
    }

    Card(shape = RoundedCornerShape(5.dp),
            border = Border(border, Color.Gray),
            color = Color(b, b, b, 255),
            modifier = LayoutPadding(5.dp)
    ) {
        Container(height = 100.dp, expanded = true) {
            Draw { canvas, parentSize ->
                val paint = Paint()
                paint.style = PaintingStyle.fill
                paint.isAntiAlias = true

                voteModel.dots.forEach { dot ->
                    paint.color = Color(
                            dot.color.substring(0, 2).toInt(16),
                            dot.color.substring(2, 4).toInt(16),
                            dot.color.substring(4, 6).toInt(16),
                            255 / 3
                    )
                    val offset = Offset(
                            parentSize.width.value * dot.x,
                            parentSize.height.value * dot.y
                    )
                    canvas.drawCircle(offset, 30.dp.value, paint)
                }
            }
            Text(
                    modifier = LayoutPadding(10.dp),
                    text = voteModel.text,
                    style = TextStyle(
                            color = Color(0, 0, 0, 200),
                            textAlign = TextAlign.Center
                    )
            )
        }
    }
}

@Preview
@Composable
fun votePreview() {
    //VoteItems(voteItems = fakeVotes, columnCount = 2, uiContext = null)

    Loading()
}

val fakeVotes = listOf(
        fakeVoteItem("Drôle/original \uD83D\uDE03", 3),
        fakeVoteItem("Trèsenrichissant enrichissant enrichissant \uD83E\uDD13", 1),
        fakeVoteItem("Super intéressant \uD83D\uDC4D", 8),
        fakeVoteItem("Très bon orateur \uD83D\uDC4F", 21),
        fakeVoteItem("Pas clair \uD83E\uDDD0", 2)
)

fun dots(count: Int, possibleColors: List<String>): List<UIDot> {
    return 0.until(count).map {
        UIDot(Random.nextFloat(),
                Random.nextFloat(),
                possibleColors.get(Random.nextInt().absoluteValue % possibleColors.size)
        )
    }
}

fun fakeVoteItem(text: String, count: Int): UIVoteItem {
    val color = listOf("7cebcd",
            "0822cb",
            "d5219d",
            "8eec1a")

    return UIVoteItem(id = Random.nextInt().toString(), text = text, dots = dots(count, color), votedByUser = count % 2 == 0)
}
