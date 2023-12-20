package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.VarInt

@Serializable
data class ChunkBatchFinished(val batchSize: VarInt)