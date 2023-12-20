package mintlin.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor(classNameOf<UUID>())

    override fun deserialize(decoder: Decoder): UUID {
        return UUID(ByteArray(16) { decoder.decodeByte() }.asUByteArrayInt())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        (encoder as ByteBufferLike).buffer.write(value.data.bytes)
    }
}
