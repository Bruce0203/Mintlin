package mintlin.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf
import mintlin.serializer.VarIntSerializer.readVarInt
import mintlin.serializer.VarIntSerializer.writeVarInt
import mintlin.serializer.VarString128Serializer.readVarString
import mintlin.serializer.VarString128Serializer.writeVarString

typealias VarIntEnum = VarEnum<Int>
typealias VarStringEnum = VarEnum<String>
typealias ByteEnum = VarEnum<Int>

interface VarEnum<T> {
    val value: T
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : VarIntEnum> varIntEnumSerializer(entries: List<T>) = object : KSerializer<T> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("${classNameOf<VarIntEnum>()}${classNameOf<T>()}")

    override fun deserialize(decoder: Decoder) = decoder.getBuffer().readVarInt()
        .let { value ->
            entries.firstOrNull { it.value == value }
                ?: throw AssertionError("unknown ${descriptor.serialName} ordinal $value")
        }

    override fun serialize(encoder: Encoder, value: T) = encoder.getBuffer().writeVarInt(value.value)
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : ByteEnum> varByteEnumSerializer(entries: List<T>) = object : KSerializer<T> {
    override val descriptor = buildClassSerialDescriptor("${classNameOf<ByteEnum>()}${classNameOf<T>()}")

    override fun deserialize(decoder: Decoder) =
        decoder.decodeByte().toInt().let { value ->
            entries.firstOrNull { it.value == value }
                ?: throw AssertionError("unknown ${descriptor.serialName} ordinal $value")
        }

    override fun serialize(encoder: Encoder, value: T) = encoder.encodeByte(value.value.toByte())
}

inline fun <reified T : VarStringEnum> varStringEnumSerializer(
    length: Int, entries: List<T>
) = object : KSerializer<T> {
    override val descriptor = buildClassSerialDescriptor("ByteEnum${classNameOf<T>()}")

    override fun deserialize(decoder: Decoder) =
        decoder.getBuffer().readVarString(length).let { value -> entries.first { it.value == value } }

    override fun serialize(encoder: Encoder, value: T) = encoder.getBuffer().writeVarString(value.value)
}
