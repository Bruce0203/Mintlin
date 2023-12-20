@file:Suppress("ArrayInDataClass")

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.format.nbt.nbtSerializer
import mintlin.format.nbt.tag.Compound
import mintlin.format.nbt.tag.Tag
import mintlin.lang.fastCastTo
import mintlin.minecraft.datastructure.level.ChunkData
import mintlin.minecraft.datastructure.level.Light
import mintlin.serializer.ByteBufferLike
import mintlin.serializer.VarIntSerializer
import mintlin.lang.classNameOf
import mintlin.serializer.varIntSizedArraySerializer

@Serializable
data class Chunk(
    val x: Int,
    val z: Int,
    @Serializable(HeightMapsSerializer::class)
    val heightmaps: HeightMaps = HeightMaps(),
    val chunkData: ChunkData,
    @Serializable(BlockEntitiesSerializer::class)
    val blockEntities: Array<BlockEntity> = emptyArray(),
    val light: Light = Light(
        skyLights = Array(chunkData.chunkSections.size) { ByteArray(0) },
        blockLights = Array(chunkData.chunkSections.size) { ByteArray(0) },
    )
) {
    override fun toString() = "${classNameOf<Chunk>()}(x=$x, z=$z, ...)"
}

@Serializable
@SerialName("")
data class HeightMaps(
    @SerialName("MOTION_BLOCKING")
    @Serializable(LongArraySerializer::class)
    val motionBlocking: LongArray = LongArray(37) { 0 },
    @SerialName("WORLD_SURFACE")
    @Serializable(LongArraySerializer::class)
    val worldSurface: LongArray = LongArray(37) { 0 }
) {
    data object LongArraySerializer : KSerializer<LongArray> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<LongArraySerializer>())
        override fun deserialize(decoder: Decoder): LongArray {
            return (decoder as mintlin.format.PolymorphicDecoder).decodeValue().fastCastTo()
        }

        override fun serialize(encoder: Encoder, value: LongArray) {
            val tagEncoder = encoder as mintlin.format.NamedTagEncoder
            tagEncoder.encodeNamedValue(tagEncoder.currentNamedTag, value)
        }
    }
}

object HeightMapsSerializer : KSerializer<HeightMaps> by nbtSerializer(HeightMaps.serializer())

@Serializable(BlockEntity.Serializer::class)
data class BlockEntity(
    val x: Int, val y: Int, val z: Int,
    val type: Int, val data: Compound
) {
    companion object Serializer : KSerializer<BlockEntity> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<BlockEntity>())

        override fun deserialize(decoder: Decoder): BlockEntity {
            val packedXZ = decoder.decodeByte().toInt()
            return BlockEntity(
                x = packedXZ shr 4, z = packedXZ and 15,
                y = decoder.decodeShort().toInt(),
                type = VarIntSerializer.deserialize(decoder),
                data = Tag.decodeFromBuffer((decoder as ByteBufferLike).buffer)
            )
        }

        override fun serialize(encoder: Encoder, value: BlockEntity) {
            encoder.encodeByte((((value.x and 15) shl 4) or (value.z and 15)).toByte())
            encoder.encodeShort(value.y.toShort())
            VarIntSerializer.serialize(encoder, value.type)
            Tag.encodeToBuffer(value.data, (encoder as ByteBufferLike).buffer)
        }
    }
}

object BlockEntitiesSerializer : KSerializer<Array<BlockEntity>> by varIntSizedArraySerializer(BlockEntity.serializer())