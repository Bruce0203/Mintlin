package mintlin.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf

object ShortSerializer : KSerializer<Int> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<ShortSerializer>())

    override fun deserialize(decoder: Decoder): Int = decoder.decodeShort().toInt()

    override fun serialize(encoder: Encoder, value: Int) = encoder.encodeShort(value.toShort())
}

object ByteToIntSerializer : KSerializer<Int> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<ByteToIntSerializer>())

    override fun deserialize(decoder: Decoder): Int = decoder.decodeByte().toInt()

    override fun serialize(encoder: Encoder, value: Int) = encoder.encodeByte(value.toByte())
}

object ByteSerializer : KSerializer<Byte> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<ByteSerializer>())

    override fun deserialize(decoder: Decoder): Byte = decoder.decodeByte()

    override fun serialize(encoder: Encoder, value: Byte) = encoder.encodeByte(value)
}

object FloatSerializer : KSerializer<Float> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<FloatSerializer>())

    override fun deserialize(decoder: Decoder): Float = decoder.decodeFloat()

    override fun serialize(encoder: Encoder, value: Float) = encoder.encodeFloat(value)
}

object NullableFloatSerializer : KSerializer<Float?> by FloatSerializer.nullable

object BooleanSerializer : KSerializer<Boolean> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<BooleanSerializer>())

    override fun deserialize(decoder: Decoder): Boolean = decoder.decodeBoolean()

    override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeBoolean(value)
}

object LongSerializer : KSerializer<Long> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<LongSerializer>())

    override fun deserialize(decoder: Decoder): Long = decoder.decodeLong()

    override fun serialize(encoder: Encoder, value: Long) = encoder.encodeLong(value)
}

object VarIntSizedLongArraySerializer : KSerializer<LongArray> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<VarIntSizedLongArraySerializer>())

    private val longArraySerializer = varIntSizedArraySerializer(LongSerializer)

    override fun deserialize(decoder: Decoder): LongArray {
        return longArraySerializer.deserialize(decoder).toLongArray()
    }

    override fun serialize(encoder: Encoder, value: LongArray) {
        longArraySerializer.serialize(encoder, value.toTypedArray())
    }
}

object VarIntSizedByteArraySerializer : KSerializer<ByteArray> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<VarIntSizedByteArraySerializer>())

    private val byteArraySerializer = varIntSizedArraySerializer(ByteSerializer)

    override fun deserialize(decoder: Decoder): ByteArray {
        return byteArraySerializer.deserialize(decoder).toByteArray()
    }

    override fun serialize(encoder: Encoder, value: ByteArray) {
        byteArraySerializer.serialize(encoder, value.toTypedArray())
    }
}

object VarIntSizedVarIntArraySerializer : KSerializer<IntArray> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<VarIntSizedVarIntArraySerializer>())

    private val intArraySerializer = varIntSizedArraySerializer(VarIntSerializer)

    override fun deserialize(decoder: Decoder): IntArray {
        return intArraySerializer.deserialize(decoder).toIntArray()
    }

    override fun serialize(encoder: Encoder, value: IntArray) {
        intArraySerializer.serialize(encoder, value.toTypedArray())
    }
}

class FixedSizeByteArraySerializer(val size: Int) : KSerializer<ByteArray> {
    override val descriptor = buildClassSerialDescriptor("${size}${classNameOf<FixedSizeByteArraySerializer>()}")

    override fun deserialize(decoder: Decoder): ByteArray {
        return ByteArray(size) { decoder.decodeByte() }
    }

    override fun serialize(encoder: Encoder, value: ByteArray) {
        value.forEach { encoder.encodeByte(it) }
    }
}

object VarIntSizedIntArrayListSerializer : KSerializer<MutableList<Int>> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<VarIntSizedIntArrayListSerializer>())

    override fun deserialize(decoder: Decoder): MutableList<Int> {
        return VarIntSizedVarIntArraySerializer.deserialize(decoder).toMutableList()
    }

    override fun serialize(encoder: Encoder, value: MutableList<Int>) {
        VarIntSizedVarIntArraySerializer.serialize(encoder, value.toIntArray())
    }
}

class FixedSizedLongArraySerializer(val size: Int) : KSerializer<LongArray> {
    override val descriptor = buildClassSerialDescriptor("${size}${classNameOf<FixedSizedLongArraySerializer>()}")

    override fun deserialize(decoder: Decoder): LongArray {
        return LongArray(size) { decoder.decodeLong() }
    }

    override fun serialize(encoder: Encoder, value: LongArray) {
        value.forEach { encoder.encodeLong(it) }
    }
}
