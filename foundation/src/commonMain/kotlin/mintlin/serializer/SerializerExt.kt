package mintlin.serializer

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

const val VAR_SEGMENT_BITS = 0x7F
const val VAR_CONTINUE_BIT = 0x80

interface ByteBufferLike {
    val buffer: Buffer
    val available get() = buffer.size
    fun decodeByteArray() = buffer.readByteArray()
}

interface CloneableDecoder : Cloneable, Decoder {
    public override fun clone(): CloneableDecoder
}

fun Decoder.getBuffer() = (this as ByteBufferLike).buffer
fun Encoder.getBuffer() = (this as ByteBufferLike).buffer
