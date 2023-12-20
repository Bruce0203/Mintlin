package mintlin.format.nbt.tag

import kotlinx.io.Buffer
import kotlinx.io.readDouble
import kotlinx.io.readFloat
import kotlinx.io.readString
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.capturedKClass
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class TagDecoder(
    val buffer: Buffer,
    override val serializersModule: SerializersModule
) : AbstractDecoder() {
    constructor(byteArray: ByteArray, serializersModule: SerializersModule)
            : this(Buffer().apply { write(byteArray) }, serializersModule)

    private val descriptorToIndex = HashMap<Int, Int>()
    private operator fun get(descriptor: SerialDescriptor) =
        descriptorToIndex.getOrPut(descriptor.capturedKClass.hashCode()) { 0 }

    private operator fun set(descriptor: SerialDescriptor, value: Int) =
        descriptorToIndex.set(descriptor.capturedKClass.hashCode(), value)

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        val elementIndex = this[descriptor]
        if (elementIndex == descriptor.elementsCount) return CompositeDecoder.DECODE_DONE
        this[descriptor] = elementIndex + 1
        return elementIndex
    }

    override fun decodeInt() = buffer.readInt()
    override fun decodeLong() = buffer.readLong()
    override fun decodeFloat() = buffer.readFloat()
    override fun decodeByte() = buffer.readByte()
    override fun decodeDouble() = buffer.readDouble()
    override fun decodeShort() = buffer.readShort()
    override fun decodeString() = buffer.readString(buffer.readShort().toLong())
    fun decodeByteArray() = ByteArray(buffer.readInt()) { buffer.readByte() }
    fun decodeIntArray() = IntArray(buffer.readInt()) { buffer.readInt() }
    fun decodeLongArray() = LongArray(buffer.readInt()) { buffer.readLong() }
}