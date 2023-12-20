package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Metadata
import mintlin.serializer.VarInt

@Serializable
data class SetEntityMetadata(val id: VarInt, val metadata: Metadata)