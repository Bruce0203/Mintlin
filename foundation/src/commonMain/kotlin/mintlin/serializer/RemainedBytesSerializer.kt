package mintlin.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf

object RemainedBytesSerializer : RemainedBytesMaxLen(Int.MAX_VALUE)
object RemainedBytesMax1048576 : RemainedBytesMaxLen(1048576)
object RemainedBytesMax32767 : RemainedBytesMaxLen(32767)

open class RemainedBytesMaxLen(val length: Int) : KSerializer<ByteArray> {
    override val descriptor = buildClassSerialDescriptor("${classNameOf<RemainedBytesMaxLen>()}$length")

    override fun deserialize(decoder: Decoder): ByteArray {
        val packetDecoder = (decoder as ByteBufferLike)
        testLength(packetDecoder.available.toInt())
        return packetDecoder.decodeByteArray()
    }

    override fun serialize(encoder: Encoder, value: ByteArray) {
        testLength(value.size)
        value.forEach { encoder.encodeByte(it) }
    }

    private fun testLength(length: Int) {
        if (this.length < length)
            throw AssertionError("length of this array must with max length of $length")
    }
}