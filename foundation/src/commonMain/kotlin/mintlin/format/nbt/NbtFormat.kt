package mintlin.format.nbt

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import mintlin.format.json.Json
import mintlin.format.nbt.tag.Compound
import mintlin.format.nbt.tag.TagFormat
import mintlin.format.nbt.tag.createCompound
import mintlin.format.nbt.tag.stringify

class NbtFormat(val tag: TagFormat<Compound>) : SerialFormat, BinaryFormat {
    override val serializersModule = EmptySerializersModule()

    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        val compound = tag.decodeFromByteArray(bytes)
        val nbtDecoder = NbtCompoundDecoder(compound, serializersModule)
        println(compound.stringify())
        println(Json.stringify(compound.stringify()))
        return deserializer.deserialize(nbtDecoder)
    }

    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val compound = createCompound()
        val nbtEncoder = NbtCompoundEncoder(compound, serializersModule)
        serializer.serialize(nbtEncoder, value)
        return tag.encodeToByteArray(compound)
    }

}