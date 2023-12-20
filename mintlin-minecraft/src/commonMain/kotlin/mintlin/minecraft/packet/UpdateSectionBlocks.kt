package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.serializer.VarLongSerializer
import mintlin.lang.classNameOf
import mintlin.serializer.varIntSizedArraySerializer

@Serializable
data class UpdateSectionBlocks(
    val chunkSectionPosition: ChunkSectionPosition,
    @Serializable(BlocksSerializer::class)
    val blocks: Array<Block>
)

@Serializable(ChunkSectionPosition.Serializer::class)
data class ChunkSectionPosition(
    val x: Long,
    val y: Long,
    val z: Long
) {
    companion object Serializer : KSerializer<ChunkSectionPosition> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<ChunkSectionPosition>())

        override fun deserialize(decoder: Decoder): ChunkSectionPosition {
            val long = decoder.decodeLong()
            return ChunkSectionPosition(
                x = (long shr 42),
                y = ((long shl 44) shr 44),
                z = ((long shl 22) shr 42),
            )
        }

        override fun serialize(encoder: Encoder, value: ChunkSectionPosition) {
            encoder.encodeLong((((value.x and 0x3FFFFF) shl 42) or (value.y and 0xFFFFF) or ((value.z and 0x3FFFFF) shl 20)))
        }
    }
}

object BlocksSerializer : KSerializer<Array<Block>> by varIntSizedArraySerializer(Block.serializer())

@Serializable(Block.Serializer::class)
data class Block(
    val stateId: Int,
    val localX: Int,
    val localY: Int,
    val localZ: Int,
) {
    companion object Serializer : KSerializer<Block> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<Block>())

        override fun deserialize(decoder: Decoder): Block {
            val long = VarLongSerializer.deserialize(decoder)
            return Block(
                stateId = (long shr 12).toInt(),
                localY = (long and 0xF).toInt(),
                localX = ((long shr 8) and 0xF).toInt(),
                localZ = ((long shr 4) and 0xF).toInt(),
            )
        }

        override fun serialize(encoder: Encoder, value: Block) {
            VarLongSerializer.serialize(
                encoder,
                ((value.stateId.toLong() shl 12) or (((value.localX shl 8) or (value.localZ shl 4)) or value.localY).toLong())
            )
        }
    }
}