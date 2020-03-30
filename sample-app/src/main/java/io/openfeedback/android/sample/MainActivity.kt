package io.openfeedback.android.sample

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Space
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.disposeComposition
import androidx.ui.core.setContent
import androidx.ui.layout.Container
import androidx.ui.layout.Spacer
import io.openfeedback.android.OpenFeedback
import io.openfeedback.android.compose.Loading
import io.openfeedback.android.compose.SessionFeedbackContainer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val openFeedback = (applicationContext as MainApplication).openFeedback
        setContent {
            SessionFeedbackContainer(openFeedback, "tpPA74vW740LHiVDWHtV")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setContent { }
    }
}
