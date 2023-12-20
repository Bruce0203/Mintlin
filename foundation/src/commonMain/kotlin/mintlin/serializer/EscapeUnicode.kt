package mintlin.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonUnquotedLiteral
import mintlin.format.json.JsonCompoundEncoder
import mintlin.lang.classNameOf

fun String.toUnicodeEscape(): String {
    val sb = StringBuilder()
    forEach { c ->
        if (c.code >= 128) {
            val unicode = c.code.toUInt().toString(16)
            sb.append("\\u")
            for (i in 0 until 4 - unicode.length) {
                sb.append('0')
            }
            sb.append(unicode)
        } else {
            sb.append(c)
        }
    }
    return sb.toString()
}

object UnescapedUnicodeStringSerializer : KSerializer<String> {

    override val descriptor = buildClassSerialDescriptor(classNameOf<UnescapedUnicodeStringSerializer>())

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: String) = when (encoder) {
        is JsonEncoder -> encoder.encodeJsonElement(JsonUnquotedLiteral("\"$value\""))
        is JsonCompoundEncoder -> encoder.encodeString("\"$value\"")
        else -> encoder.encodeString(value)
    }

    override fun deserialize(decoder: Decoder): String = when (decoder) {
        is JsonDecoder -> decoder.decodeJsonElement().toString().run { substring(1, length - 1) }
        else -> decoder.decodeString()
    }
}
