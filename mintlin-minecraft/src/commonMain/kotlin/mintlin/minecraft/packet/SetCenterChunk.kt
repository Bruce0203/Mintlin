package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.VarInt

@Serializable
data class SetCenterChunk(val chunkX: VarInt, val chunkZ: VarInt)
