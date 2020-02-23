package io.openfeedback.android.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.ui.core.setContent
import io.openfeedback.android.OpenFeedback
import io.openfeedback.android.compose.SessionFeedback
import io.openfeedback.android.compose.SessionFeedbackContainer
import io.openfeedback.android.model.UIVoteItem

class SessionFeedbackView @JvmOverloads constructor(context: Context,
                                                    attrs: AttributeSet? = null,
                                                    defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    fun configure(
            openFeedback: OpenFeedback,
            sessionId: String) {
        setContent {
            SessionFeedbackContainer(openFeedback, sessionId)
        }
    }
}