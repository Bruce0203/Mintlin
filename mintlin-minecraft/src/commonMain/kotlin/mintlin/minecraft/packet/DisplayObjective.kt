package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.VarInt
import mintlin.serializer.VarString32767

@Serializable
data class DisplayObjective(
    val position: VarInt,
    val scoreName: VarString32767
)