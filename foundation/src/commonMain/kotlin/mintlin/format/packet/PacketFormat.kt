package mintlin.format.packet

import kotlinx.io.Buffer
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import mintlin.format.BufferFormat

@OptIn(ExperimentalSerializationApi::class)
object PacketFormat : BufferFormat, BinaryFormat {
    override val serializersModule: SerializersModule = EmptySerializersModule()

    override fun <T> decodeFromBuffer(deserializer: DeserializationStrategy<T>, buffer: Buffer): T {
        val decoder = PacketDecoderImp(buffer, serializersModule)
        return deserializer.deserialize(decoder)
    }

    override fun <T> encodeToBuffer(serializer: SerializationStrategy<T>, value: T, buffer: Buffer) {
        val encoder = PacketEncoderImp(serializersModule, buffer)
        serializer.serialize(encoder, value)
    }

    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        val decoder = PacketDecoderImp(bytes, serializersModule)
        return deserializer.deserialize(decoder)
    }

    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val encoder = PacketEncoderImp(serializersModule)
        serializer.serialize(encoder, value)
        return encoder.readByteArray()
    }
}
