package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Position

@Serializable
data class WorldEvent(
    val event: Int,
    val location: Position,
    val data: Int,
    val disableRelativeVolume: Boolean
)
