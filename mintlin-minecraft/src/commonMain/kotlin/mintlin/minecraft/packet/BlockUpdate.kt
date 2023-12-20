package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Position
import mintlin.serializer.VarInt

@Serializable
data class BlockUpdate(
    val location: Position,
    val blockId: VarInt
)