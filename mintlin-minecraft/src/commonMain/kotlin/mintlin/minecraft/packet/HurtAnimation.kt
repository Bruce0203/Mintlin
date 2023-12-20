package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.VarInt

@Serializable
data class HurtAnimation(
    override val entityId: VarInt,
    val yaw: Float
) : IdentifiedEntity