package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.level.Light
import mintlin.serializer.VarInt

@Serializable
data class UpdateLight(
    val chunkX: VarInt,
    val chunkZ: VarInt,
    val light: Light
)