package com.fitmate.app.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.roundToInt

/**
 * A serializer that accepts both integer and floating-point JSON numbers
 * and rounds them to the nearest Int. This handles AI APIs that sometimes
 * return decimal values like 14.3 for protein grams.
 */
object RoundingIntSerializer : KSerializer<Int> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("RoundingInt", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Int) {
        encoder.encodeInt(value)
    }

    override fun deserialize(decoder: Decoder): Int {
        return if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement()
            element.jsonPrimitive.content.toDoubleOrNull()?.roundToInt() ?: 0
        } else {
            decoder.decodeInt()
        }
    }
}

/**
 * Nullable variant of RoundingIntSerializer.
 */
object NullableRoundingIntSerializer : KSerializer<Int?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("NullableRoundingInt", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Int?) {
        if (value != null) encoder.encodeInt(value) else encoder.encodeNull()
    }

    override fun deserialize(decoder: Decoder): Int? {
        return if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement()
            if (element is JsonNull) null
            else element.jsonPrimitive.content.toDoubleOrNull()?.roundToInt()
        } else {
            decoder.decodeInt()
        }
    }
}
