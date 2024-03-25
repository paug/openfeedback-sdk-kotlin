package io.openfeedback.mappers

import com.google.firebase.Timestamp
import io.openfeedback.model.Comment
import kotlinx.datetime.Instant

actual fun Map<String, *>.convertToModel(
    id: String,
    voteItemId: String
): Comment {
    val createdAt = this["createdAt"] as Timestamp
    val updatedAt = this["updatedAt"] as Timestamp
    return Comment(
        id = id,
        voteItemId = voteItemId,
        text = this["text"] as String,
        plus = this["plus"] as Long,
        createdAt = Instant.fromEpochSeconds(createdAt.seconds, createdAt.nanoseconds),
        updatedAt = Instant.fromEpochSeconds(updatedAt.seconds, updatedAt.nanoseconds)
    )
}
