package io.openfeedback.serializers

import dev.gitlive.firebase.FirebaseDecoder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.JsonObject

class SessionVoteDeserializer : DeserializationStrategy<Map<String, Long>> {
    override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor

    override fun deserialize(decoder: Decoder): Map<String, Long> {
        val input =
            decoder as? FirebaseDecoder ?: throw SerializationException("Expected FirebaseDecoder")
        val compositeDecoder = input.beginStructure(descriptor)
        val count = compositeDecoder.decodeCollectionSize(JsonObject.serializer().descriptor)
        val mapOf = hashMapOf<String, Long>()
        for (index in 0 until count) {
            try {
                val mapIndex = index * 2
                val key = compositeDecoder.decodeStringElement(descriptor, mapIndex)
                val value = compositeDecoder.decodeLongElement(descriptor, mapIndex + 1)
                mapOf[key] = value
            } catch (ignored: SerializationException) {
            }
        }
        return mapOf
    }
}
