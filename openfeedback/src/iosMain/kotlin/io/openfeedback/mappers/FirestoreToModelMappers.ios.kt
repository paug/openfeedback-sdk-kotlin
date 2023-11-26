package io.openfeedback.mappers

import cocoapods.FirebaseFirestore.FIRTimestamp
import io.openfeedback.model.Comment
import kotlinx.datetime.Instant

actual fun Map<String, *>.convertToModel(
    id: String,
    voteItemId: String
): Comment {
    val createdAt = this["createdAt"] as FIRTimestamp
    val updatedAt = this["updatedAt"] as FIRTimestamp
    return Comment(
        id = id,
        voteItemId = voteItemId,
        text = this["text"] as String,
        plus = this["plus"] as Long,
        createdAt = Instant.fromEpochSeconds(createdAt.seconds, createdAt.nanoseconds),
        updatedAt = Instant.fromEpochSeconds(updatedAt.seconds, updatedAt.nanoseconds)
    )
}
