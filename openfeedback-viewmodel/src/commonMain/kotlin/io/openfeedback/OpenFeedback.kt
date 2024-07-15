package io.openfeedback

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vanniktech.locale.Locale
import com.vanniktech.locale.Locales
import io.openfeedback.m3.Comment
import io.openfeedback.m3.CommentInput
import io.openfeedback.m3.Loading
import io.openfeedback.m3.OpenFeedbackLayout
import io.openfeedback.m3.VoteCard
import io.openfeedback.viewmodels.OpenFeedbackUiState
import io.openfeedback.viewmodels.OpenFeedbackViewModel
import io.openfeedback.viewmodels.getFirebaseApp

/**
 * Stateful component that will observe remote OpenFeedback Firestore project to
 * display feedback of a session.
 *
 * @param projectId Firestore project id
 * @param sessionId Firestore session id
 * @param modifier The modifier to be applied to the component.
 * @param columnCount Number of column to display for vote items.
 * @param languageCode Language code of the user.
 * @param appName Locale openfeedback name, used to restore openfeedback configuration.
 * @param loading Component to display when the view model fetch vote items.
 * @param viewModel ViewModel instance to fetch UI models and interact with feedback form.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenFeedback(
    projectId: String,
    sessionId: String,
    modifier: Modifier = Modifier,
    columnCount: Int = 2,
    languageCode: String = Locale.from(Locales.currentLocaleString()).language.code,
    appName: String? = null,
    loading: @Composable () -> Unit = { Loading(modifier = modifier) },
    viewModel: OpenFeedbackViewModel = viewModel(
        key = sessionId,
        factory = OpenFeedbackViewModel.provideFactory(
            firebaseApp = getFirebaseApp(appName),
            projectId = projectId,
            sessionId = sessionId,
            languageCode = languageCode
        )
    )
) {
    val uiState = viewModel.uiState.collectAsState()
    when (uiState.value) {
        is OpenFeedbackUiState.Loading -> loading()
        is OpenFeedbackUiState.Success -> {
            val session = (uiState.value as OpenFeedbackUiState.Success).session
            var text by remember { mutableStateOf("") }

            OpenFeedbackLayout(
                sessionFeedback = session,
                modifier = modifier,
                columnCount = columnCount,
                comment = {
                    Comment(
                        comment = it,
                        onClick = viewModel::upVote
                    )
                },
                commentInput = {
                    CommentInput(
                        value = text,
                        onValueChange = { text = it },
                        onSubmit = { viewModel.submitComment(text) },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                voteItem = {
                    VoteCard(
                        voteModel = it,
                        onClick = viewModel::vote,
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                    )
                }
            )
        }
    }
}
