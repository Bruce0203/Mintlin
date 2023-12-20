package mintlin.format.packet

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.io.writeDouble
import kotlinx.io.writeFloat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import mintlin.serializer.ByteBufferLike

interface PacketEncoder : Encoder, ByteBufferLike

@ExperimentalSerializationApi
class PacketEncoderImp(
    override val serializersModule: SerializersModule,
    override val buffer: Buffer = Buffer()
) : AbstractEncoder(), PacketEncoder {

    fun readByteArray() = buffer.readByteArray()

    override fun encodeBoolean(value: Boolean) {
        buffer.writeByte(if (value) 1 else 0)
    }

    override fun encodeDouble(value: Double) {
        buffer.writeDouble(value)
    }

    override fun encodeFloat(value: Float) {
        buffer.writeFloat(value)
    }

    override fun encodeByte(value: Byte) {
        buffer.writeByte(value)
    }

    override fun encodeInt(value: Int) {
        buffer.writeInt(value)
    }

    override fun encodeShort(value: Short) {
        buffer.writeShort(value)
    }

    override fun encodeNull() {
        buffer.writeByte(0)
    }

    override fun encodeNotNullMark() {
        buffer.writeByte(1)
    }

    override fun encodeLong(value: Long) = buffer.write(longToByteArray(value))
}

private fun longToByteArray(value: Long): ByteArray {
    return byteArrayOf(
        (value shr 56).toByte(),
        (value shr 48).toByte(),
        (value shr 40).toByte(),
        (value shr 32).toByte(),
        (value shr 24).toByte(),
        (value shr 16).toByte(),
        (value shr 8).toByte(),
        value.toByte()
    )
}
