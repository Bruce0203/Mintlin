package mintlin.serializer

import kotlinx.io.Buffer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias VarInt = @Serializable(VarIntSerializer::class) Int

object VarIntSerializer : KSerializer<Int> {
    override val descriptor = buildClassSerialDescriptor("VarInt")

    override fun deserialize(decoder: Decoder): Int = (decoder as ByteBufferLike).buffer.readVarInt()

    override fun serialize(encoder: Encoder, value: Int) = (encoder as ByteBufferLike).buffer.writeVarInt(value)

    fun Buffer.readVarIntOrNull(): Int? {
        var value = 0
        var position = 0
        var currentByte: Int
        var pos = 0L
        if (size == 0L) return null
        while (true) {
            if (size < pos) return null
            currentByte = get(pos++).toInt()
            value = value or (currentByte and VAR_SEGMENT_BITS shl position)
            if (currentByte and VAR_CONTINUE_BIT == 0) break
            position += 7
            if (position >= 32) {
                return null
            }
        }
        skip(pos)
        return value
    }

    fun Buffer.readVarInt(): Int {
        var value = 0
        var position = 0
        var currentByte: Int
        while (true) {
            currentByte = readByte().toInt()
            value = value or (currentByte and VAR_SEGMENT_BITS shl position)
            if (currentByte and VAR_CONTINUE_BIT == 0) break
            position += 7
            if (position >= 32) throw RuntimeException("VarInt is too big")
        }
        return value
    }

    suspend fun (suspend () -> Byte?).readVarInt(): Int? {
        var value = 0
        var position = 0
        var currentByte: Int
        while (true) {
            currentByte = this()?.toInt()?: return null
            value = value or (currentByte and VAR_SEGMENT_BITS shl position)
            if (currentByte and VAR_CONTINUE_BIT == 0) break
            position += 7
            if (position >= 32) return null
        }
        return value
    }

    fun Buffer.writeVarInt(value: Int) {
        var mutValue = value
        while (true) {
            if (mutValue and VAR_SEGMENT_BITS.inv() === 0) {
                writeByte(mutValue.toByte())
                return
            }
            writeByte((mutValue and VAR_SEGMENT_BITS or VAR_CONTINUE_BIT).toByte())
            mutValue = mutValue ushr 7
        }
    }
}

class VarIntReader(val buffer: Buffer) {
    var value = 0
    var position = 0
    var readByteCount = 0

    fun readVarInt(): Int {
        var currentByte: Int
        while (true) {
            currentByte = buffer.readByte().toInt()
            readByteCount++
            value = value or (currentByte and VAR_SEGMENT_BITS shl position)
            if (currentByte and VAR_CONTINUE_BIT == 0) break
            position += 7
            if (position >= 32) throw RuntimeException("VarInt is too big")
        }
        return value
    }
}