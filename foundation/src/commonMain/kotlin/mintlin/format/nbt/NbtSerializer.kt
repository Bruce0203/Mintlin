package mintlin.format.nbt


import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.format.nbt.tag.Compound
import mintlin.format.nbt.tag.Tag
import mintlin.format.nbt.tag.TagFormat
import mintlin.format.nbt.tag.createCompound
import mintlin.serializer.ByteBufferLike

inline fun <reified T : Any> nbtSerializer(
    kSerializer: KSerializer<T>, tag: TagFormat<Compound> = Tag.RootCompoundFormat
) = object : KSerializer<T> {
    override val descriptor = kSerializer.descriptor

    override fun deserialize(decoder: Decoder): T {
        val compound = tag.decodeFromBuffer((decoder as ByteBufferLike).buffer)
        val nbtDecoder = NbtCompoundDecoder(compound, decoder.serializersModule)
        return kSerializer.deserialize(nbtDecoder)
    }

    override fun serialize(encoder: Encoder, value: T) {
        val compound = createCompound()
        val nbtEncoder = NbtCompoundEncoder(compound, encoder.serializersModule)
        kSerializer.serialize(nbtEncoder, value)
        tag.encodeToBuffer(compound, (encoder as ByteBufferLike).buffer)
    }
}
