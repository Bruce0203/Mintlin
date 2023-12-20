package mintlin.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf
import mintlin.serializer.VarIntSerializer.readVarInt
import mintlin.serializer.VarIntSerializer.writeVarInt

inline fun <reified T : Any> varIntSizedArraySerializer(kSerializer: KSerializer<T>) = object : KSerializer<Array<T>> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("VarIntSizedArray${classNameOf<T>()}")

    override fun deserialize(decoder: Decoder): Array<T> {
        val size = (decoder as ByteBufferLike).buffer.readVarInt()
        val elementDecoder = (decoder as CloneableDecoder).clone()
        return Array(size) { kSerializer.deserialize(elementDecoder.clone()) }
    }

    override fun serialize(encoder: Encoder, value: Array<T>) {
        (encoder as ByteBufferLike).buffer.writeVarInt(value.size)
        value.forEach { kSerializer.serialize(encoder, it) }
    }
}

inline fun <reified T : Any> fixedSizeArraySerializer(size: Int, kSerializer: KSerializer<T>) =
    object : KSerializer<Array<T>> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("VarIntSizedArray${classNameOf<T>()}>")

        override fun deserialize(decoder: Decoder): Array<T> {
            val elementDecoder = (decoder as CloneableDecoder).clone()
            return Array(size) { kSerializer.deserialize(elementDecoder.clone()) }
        }

        override fun serialize(encoder: Encoder, value: Array<T>) {
            if (size != value.size) throw AssertionError("array size ${value.size} is not fixed size $value")
            value.forEach { kSerializer.serialize(encoder, it) }
        }
    }