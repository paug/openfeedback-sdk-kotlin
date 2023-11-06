package io.openfeedback.android.mappers

import com.google.firebase.Timestamp
import io.openfeedback.android.model.Comment

internal fun Map<String, *>.convertToModel(
    id: String,
    voteItemId: String
): Comment = Comment(
    id = id,
    voteItemId = voteItemId,
    text = this["text"] as String,
    plus = this["plus"] as Long,
    createdAt = this["createdAt"] as Timestamp,
    updatedAt = this["updatedAt"] as Timestamp
)