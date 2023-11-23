package io.openfeedback.mappers

import io.openfeedback.model.Comment

expect fun Map<String, *>.convertToModel(id: String, voteItemId: String): Comment
