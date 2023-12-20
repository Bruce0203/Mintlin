package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.VarInt

@Serializable
data class SetRenderDistance(val viewDistance: VarInt)