package mintlin.format.nbt.tag

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.io.writeDouble
import kotlinx.io.writeFloat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.SerializersModule

@ExperimentalSerializationApi
class TagEncoder(
    override val serializersModule: SerializersModule,
    private val buffer: Buffer = Buffer(),
) : AbstractEncoder() {
    fun readByteArray() = buffer.readByteArray()
    override fun encodeNull() = Unit

    override fun encodeBoolean(value: Boolean) {
        buffer.writeByte(if (value) 1 else 0)
    }

    override fun encodeFloat(value: Float) {
        buffer.writeFloat(value)
    }

    override fun encodeDouble(value: Double) {
        buffer.writeDouble(value)
    }

    override fun encodeLong(value: Long) {
        buffer.write(longToByteArray(value))
    }

    override fun encodeByte(value: Byte) {
        buffer.writeByte(value)
    }

    override fun encodeShort(value: Short) {
        buffer.writeShort(value)
    }

    override fun encodeString(value: String) {
        buffer.writeShort(value.length.toShort())
        buffer.write(value.encodeToByteArray())
    }

    override fun encodeInt(value: Int) {
        buffer.writeInt(value)
    }

    fun encodeIntArray(value: IntArray) {
        buffer.writeInt(value.size)
        value.forEach { buffer.writeInt(it) }
    }

    fun encodeLongArray(value: LongArray) {
        buffer.writeInt(value.size)
        value.forEach { buffer.write(longToByteArray(it)) }
    }

    fun encodeByteArray(data: ByteArray) {
        buffer.writeInt(data.size)
        buffer.write(data)
    }
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
