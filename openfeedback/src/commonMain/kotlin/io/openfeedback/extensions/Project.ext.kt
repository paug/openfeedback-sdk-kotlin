package io.openfeedback.extensions

import io.openfeedback.model.Project

internal fun Project.commentVoteItemId(): String? = voteItems.find { it.type == "text" }?.id
