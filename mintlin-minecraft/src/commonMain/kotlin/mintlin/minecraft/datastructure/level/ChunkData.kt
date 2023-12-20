@file:Suppress("ArrayInDataClass")

package mintlin.minecraft.datastructure.level

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.format.packet.PacketDecoder
import mintlin.format.packet.PacketDecoderImp
import mintlin.format.packet.PacketEncoderImp
import mintlin.lang.classNameOf
import mintlin.serializer.ShortSerializer
import mintlin.serializer.VarIntSizedByteArraySerializer

@Serializable(ChunkData.Serializer::class)
data class ChunkData(val chunkSections: Array<ChunkSection>) {
    constructor(sectionSize: Int) : this(Array(sectionSize) {
        ChunkSection(blockStates = Palette(PaletteType.BLOCK), biomes = Palette(PaletteType.BIOME))
    })

    companion object Serializer : KSerializer<ChunkData> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<ChunkData>())

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): ChunkData {
            val bytes = VarIntSizedByteArraySerializer.deserialize(decoder)
            val packetDecoder = PacketDecoderImp(bytes, decoder.serializersModule)
            return ChunkData(ChunkSectionsSerializer.deserialize(packetDecoder))
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: ChunkData) {
            val packetEncoder = PacketEncoderImp(encoder.serializersModule)
            ChunkSectionsSerializer.serialize(packetEncoder, value.chunkSections)
            val bytes = packetEncoder.readByteArray()
            VarIntSizedByteArraySerializer.serialize(encoder, bytes)
        }
    }
}

@Serializable(ChunkSection.Serializer::class)
data class ChunkSection(val blockStates: Palette, val biomes: Palette) {
    companion object Serializer : KSerializer<ChunkSection> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<ChunkSection>())
        data object BlockStatesPaletteSerializer : KSerializer<Palette> by PaletteSerializer(PaletteType.BLOCK)
        data object BiomesPaletteSerializer : KSerializer<Palette> by PaletteSerializer(PaletteType.BIOME)

        override fun deserialize(decoder: Decoder): ChunkSection {
            ShortSerializer.deserialize(decoder)
            return ChunkSection(blockStates = BlockStatesPaletteSerializer.deserialize(decoder),
                biomes = BiomesPaletteSerializer.deserialize(decoder))
        }

        override fun serialize(encoder: Encoder, value: ChunkSection) {
            encoder.encodeShort(value.blockStates.count.toShort())
            BlockStatesPaletteSerializer.serialize(encoder, value.blockStates)
            BiomesPaletteSerializer.serialize(encoder, value.biomes)
        }
    }
}

object ChunkSectionsSerializer : KSerializer<Array<ChunkSection>> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<ChunkSectionsSerializer>())

    override fun deserialize(decoder: Decoder): Array<ChunkSection> {
        val list = ArrayList<ChunkSection>()
        val packetDecoder = decoder as PacketDecoder
        while (packetDecoder.available != 0L) {
            list.add(ChunkSection.serializer().deserialize(decoder))
        }
        return list.toTypedArray()
    }

    override fun serialize(encoder: Encoder, value: Array<ChunkSection>) {
        value.forEach { ChunkSection.serializer().serialize(encoder, it) }
    }
}
