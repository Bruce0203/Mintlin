package mintlin.format

import kotlinx.io.Buffer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer

interface BufferFormat : SerialFormat {
    fun <T> decodeFromBuffer(deserializer: DeserializationStrategy<T>, buffer: Buffer): T

    fun <T> encodeToBuffer(serializer: SerializationStrategy<T>, value: T, buffer: Buffer)
}

inline fun <reified T> BufferFormat.decodeToBuffer(buffer: Buffer): T =
    decodeFromBuffer(serializer<T>(), buffer)

inline fun <reified T> BufferFormat.encodeToBuffer(value: T, buffer: Buffer) =
    encodeToBuffer(serializer<T>(), value, buffer)
