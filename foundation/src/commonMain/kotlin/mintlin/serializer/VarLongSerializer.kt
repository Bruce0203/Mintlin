package mintlin.serializer

import kotlinx.io.Buffer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias VarLong = @Serializable(VarLongSerializer::class) Long

object VarLongSerializer : KSerializer<Long> {
    override val descriptor = buildClassSerialDescriptor("VarLong")

    override fun deserialize(decoder: Decoder): Long = decoder.getBuffer().readVarLong()

    override fun serialize(encoder: Encoder, value: Long) = encoder.getBuffer().writeVarLong(value)

    fun Buffer.readVarLong(): Long {
        var value: Long = 0
        var position = 0
        var currentByte: Int
        while (true) {
            currentByte = readByte().toInt()
            value = value or ((currentByte and VAR_SEGMENT_BITS).toLong() shl position)
            if (currentByte and VAR_CONTINUE_BIT == 0) break
            position += 7
            if (position >= 64) throw java.lang.RuntimeException("VarLong is too big")
        }
        return value
    }

    fun Buffer.writeVarLong(value: Long) {
        var value = value
        while (true) {
            if (value and VAR_SEGMENT_BITS.toLong().inv() == 0L) {
                writeByte(value.toByte())
                return
            }
            writeByte((value and VAR_SEGMENT_BITS.toLong() or VAR_CONTINUE_BIT.toLong()).toByte())

            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value = value ushr 7
        }
    }
}