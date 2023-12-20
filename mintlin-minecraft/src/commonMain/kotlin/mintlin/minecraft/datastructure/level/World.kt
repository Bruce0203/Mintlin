package mintlin.minecraft.datastructure.level

import BlockEntitiesSerializer
import BlockEntity
import Chunk
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.CachedPacketHolder
import mintlin.cachedPacket
import mintlin.minecraft.datastructure.Point2D
import mintlin.minecraft.datastructure.Point3D
import mintlin.serializer.VarIntSerializer
import mintlin.lang.classNameOf
import mintlin.serializer.varIntSizedArraySerializer

interface ChunkMatrix {
    fun getChunkAt(position: Point2D): CachedPacketHolder<Chunk>?
    fun setChunkAt(position: Point2D, value: CachedPacketHolder<Chunk>?)
    fun getAllChunks(): Collection<CachedPacketHolder<Chunk>>

    fun getBlockAt(position: Point3D): Int?
    fun setBlockAt(position: Point3D, value: Int): Boolean

    fun getBiomeAt(position: Point3D): Int?
    fun setBiomeAt(position: Point3D, value: Int): Boolean
}

fun ChunkMatrix.getChunkAt(position: Point3D): CachedPacketHolder<Chunk>? = getChunkAt(position.toChunkPos())
fun ChunkMatrix.setChunkAt(position: Point3D, value: CachedPacketHolder<Chunk>) =
    setChunkAt(position.toChunkPos(), value)

private fun Point3D.toChunkPos() = Point2D(x and 0xF, z and 0xF)

@Serializable(WorldDiskSerializer::class)
class World(val minHeight: Int, val maxHeight: Int) : ChunkMatrix {
    fun getSectionSize() = (maxHeight - minHeight) / 16

    private val chunks: MutableMap<Long, CachedPacketHolder<Chunk>> = hashMapOf()

    override fun getChunkAt(position: Point2D): CachedPacketHolder<Chunk>? {
        return chunks[getChunkIndex(position)]
    }

    override fun setChunkAt(position: Point2D, value: CachedPacketHolder<Chunk>?) {
        if (value === null) chunks.remove(getChunkIndex(position))
        else chunks[getChunkIndex(position)] = value
    }

    override fun getAllChunks(): Collection<CachedPacketHolder<Chunk>> = chunks.values

    override fun getBlockAt(position: Point3D): Int? {
        val chunkSection = getChunkAt(position.toChunkPosition())?.value?.chunkData?.getChunkSectionAt(position.y)
        val pos = position.toChunkSectionPosition()
        return chunkSection?.blockStates?.get(pos)
    }

    override fun setBlockAt(position: Point3D, value: Int): Boolean {
        val chunkSection = getChunkAt(position.toChunkPosition())?.value?.chunkData?.getChunkSectionAt(position.y)
        val pos = position.toChunkSectionPosition()
        val blockStates = chunkSection?.blockStates
        return blockStates?.set(pos, value) !== null
    }

    override fun getBiomeAt(position: Point3D): Int? {
        val chunkSection = getChunkAt(position.toChunkPosition())?.value?.chunkData?.getChunkSectionAt(position.y)
        val pos = position.toChunkSectionPosition()
        return chunkSection?.biomes?.get(pos)
    }

    override fun setBiomeAt(position: Point3D, value: Int): Boolean {
        val chunkSection = getChunkAt(position.toChunkPosition())?.value?.chunkData?.getChunkSectionAt(position.y)
        val pos = position.toChunkSectionPosition()
        val blockStates = chunkSection?.biomes
        return blockStates?.set(pos, value) !== null
    }

    private fun Point3D.toChunkPosition() = Point2D(x shr 4, z shr 4)

    private fun getChunkIndex(position: Point2D) = getChunkIndex(position.x, position.y)

    private fun getChunkIndex(chunkX: Int, chunkZ: Int): Long {
        return chunkX.toLong() shl 32 or (chunkZ.toLong() and 0xffffffffL)
    }

    private fun ChunkData.getChunkSectionAt(y: Int) = chunkSections[(y - minHeight) / 16]

    private fun Point3D.toChunkSectionPosition() = Point3D(x and 0xF, y and 0xF, z and 0xF)
}

data object ChunkWithPosArraySerializer
    : KSerializer<Array<ChunkWithPos>> by varIntSizedArraySerializer(ChunkWithPos.serializer())

@Serializable
class ChunkWithPos(
    val pos: Point2D,
    @Serializable(VarIntSizedPaletteSerializer::class)
    val palettes: Array<Palette>,
    @Serializable(BlockEntitiesSerializer::class)
    val blockEntities: Array<BlockEntity>
)

data object VarIntSizedPaletteSerializer
    : KSerializer<Array<Palette>> by varIntSizedArraySerializer(PaletteDiskSerializer(PaletteType.BLOCK))

object WorldDiskSerializer : KSerializer<World> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<World>())

    override fun deserialize(decoder: Decoder): World {
        return World(
            minHeight = VarIntSerializer.deserialize(decoder),
            maxHeight = VarIntSerializer.deserialize(decoder)
        ).apply {
            ChunkWithPosArraySerializer.deserialize(decoder).forEach { chunk ->
                val x = chunk.pos.x
                val z = chunk.pos.y
                setChunkAt(Point2D(x, z), cachedPacket {
                    Chunk(x, z, chunkData = ChunkData(Array(getSectionSize()) {
                        ChunkSection(blockStates = chunk.palettes[it], biomes = Palette(PaletteType.BIOME))
                    }), blockEntities = chunk.blockEntities)
                })
            }
        }
    }

    override fun serialize(encoder: Encoder, value: World) {
        VarIntSerializer.serialize(encoder, value.minHeight)
        VarIntSerializer.serialize(encoder, value.maxHeight)
        val chunks = value.getAllChunks().map { cache ->
            val chunk = cache.value
            ChunkWithPos(Point2D(chunk.x, chunk.z), chunk.chunkData.chunkSections.map {
                it.blockStates
            }.toTypedArray(), chunk.blockEntities)
        }.toTypedArray()
        ChunkWithPosArraySerializer.serialize(encoder, chunks)
    }
}
