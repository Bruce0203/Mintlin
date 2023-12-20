package mintlin.format.json

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.EmptySerializersModule
import mintlin.lang.fastCastTo

object JsonFormat : StringFormat {
    override val serializersModule = EmptySerializersModule()
    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        val decoder = JsonCompoundDecoder(Json.parseFast(string).fastCastTo(), serializersModule)
        return deserializer.deserialize(decoder)
    }

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        val encoder = JsonCompoundEncoder(serializersModule)
        serializer.serialize(encoder, value)
        return Json.stringify(encoder.compound)
    }
}