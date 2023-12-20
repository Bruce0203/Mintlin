package mintlin.minecraft.registry

import kotlinx.io.Buffer
import kotlinx.io.InternalIoApi
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import mintlin.format.nbt.tag.Tag

@OptIn(InternalIoApi::class)
object Material {
    const val item2BlockMapFile = "item_to_block_map.dat"
    const val block2ItemMapFile = "block_to_item_map.dat"

    val item2Block = registryResource(item2BlockMapFile).getIntArrayTagList()
    val block2Item = registryResource(block2ItemMapFile).getIntArrayTagList()

    private fun Buffer.getIntArrayTagList() = Tag.IntArrayFormat.decodeFromBuffer(this)
    private fun Buffer.getCompoundTag() = Tag.CompoundFormat.decodeFromBuffer(this)

    private fun ByteArray.getStringTagList() = Tag.ListFormat.decodeFromByteArray(this)

    private fun registryResource(file: String): Buffer = SystemFileSystem.source(Path(file)).buffered().buffer
}