package mintlin.minecraft.packet

import kotlinx.serialization.Serializable

@Serializable
data class UnloadChunk(val chunkZ: Int, val chunkX: Int)
