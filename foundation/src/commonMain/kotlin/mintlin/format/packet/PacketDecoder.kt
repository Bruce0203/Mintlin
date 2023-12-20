package mintlin.format.packet

import kotlinx.io.Buffer
import kotlinx.io.readDouble
import kotlinx.io.readFloat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule
import mintlin.datastructure.FastArrayList
import mintlin.serializer.ByteBufferLike
import mintlin.serializer.CloneableDecoder

interface PacketDecoder : Decoder, CloneableDecoder, ByteBufferLike

@ExperimentalSerializationApi
open class PacketDecoderImp(
    override val buffer: Buffer,
    override val serializersModule: SerializersModule
) : AbstractDecoder(), CompositeDecoder, PacketDecoder {
    constructor(byteArray: ByteArray, serializersModule: SerializersModule)
            : this(Buffer().apply { write(byteArray) }, serializersModule)

    private var elementIndexes = FastArrayList<Int>()

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        elementIndexes.add(-1)
        return super.beginStructure(descriptor)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        elementIndexes.removeLast()
        super.endStructure(descriptor)
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        val elementIndex = elementIndexes.replaceLast { it + 1 }
        if (elementIndex == descriptor.elementsCount) return CompositeDecoder.DECODE_DONE
        return elementIndex
    }

    override fun decodeBoolean() = decodeByte().toInt() != 0

    override fun decodeByte() = buffer.readByte()
    override fun decodeInt(): Int = buffer.readInt()
    override fun decodeShort(): Short = buffer.readShort()

    override fun decodeFloat() = buffer.readFloat()

    override fun decodeDouble() = buffer.readDouble()

    override fun decodeLong() = buffer.readLong()

    override fun decodeNotNullMark() = decodeBoolean()

    override fun clone(): PacketDecoder = PacketDecoderImp(
        serializersModule = serializersModule,
        buffer = buffer
    )

}

