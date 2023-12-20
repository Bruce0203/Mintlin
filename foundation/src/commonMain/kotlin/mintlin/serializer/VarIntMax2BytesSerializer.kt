package mintlin.serializer

import kotlinx.io.Buffer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf

object VarIntMax2BytesSerializer : KSerializer<Int> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor(classNameOf<VarIntMax2BytesSerializer>())

    override fun deserialize(decoder: Decoder): Int {
        return decoder.getBuffer().readVarIntMax2Bytes()
    }

    override fun serialize(encoder: Encoder, value: Int) {
        encoder.getBuffer().writeVarIntMax2Bytes(value)
    }

    fun Buffer.readVarIntMax2Bytes(): Int {
        var value = 0
        var position = 0
        var currentByte: Int
        while (true) {
            if (position == 14) break
            currentByte = readByte().toInt()
            value = value or (currentByte and VAR_SEGMENT_BITS shl position)
            if (currentByte and VAR_CONTINUE_BIT == 0) break
            position += 7
            if (position >= 32) throw RuntimeException("VarInt is too big")
        }
        return value
    }

    fun Buffer.writeVarIntMax2Bytes(value: Int) {
        var mutValue = value
        var count = 0
        while (true) {
            if (count++ == 2) return
            if (mutValue and VAR_SEGMENT_BITS.inv() == 0) {
                writeByte(mutValue.toByte())
                return
            }
            writeByte((mutValue and VAR_SEGMENT_BITS or VAR_CONTINUE_BIT).toByte())
            mutValue = mutValue ushr 7
        }
    }
}
