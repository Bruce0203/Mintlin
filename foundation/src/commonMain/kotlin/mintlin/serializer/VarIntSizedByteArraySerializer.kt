package mintlin.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf
import mintlin.serializer.VarIntSerializer.readVarInt
import mintlin.serializer.VarIntSerializer.writeVarInt

object VarIntSizedBytesMax512 : VarIntSizedBytesMax(512)
object VarIntSizedBytesMax4096 : VarIntSizedBytesMax(4096)

open class VarIntSizedBytesMax(private val max: Int) : KSerializer<ByteArray> {
    override val descriptor = buildClassSerialDescriptor("${classNameOf<VarIntSizedBytesMax>()}$max")

    override fun deserialize(decoder: Decoder): ByteArray {
        val size = decoder.getBuffer().readVarInt()
        testLength(size)
        return ByteArray(size) { decoder.decodeByte() }
    }

    override fun serialize(encoder: Encoder, value: ByteArray) {
        testLength(value.size)
        encoder.getBuffer().writeVarInt(value.size)
        value.forEach { encoder.encodeByte(it) }
    }

    private fun testLength(size: Int) {
        if (max < size) throw AssertionError("VarInt sized bytes max length $max but size was $size")
    }
}