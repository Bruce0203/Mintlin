package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.VarInt

@Serializable
data class PickupItem(
    val collectedEntityId: VarInt,
    val collectorEntityId: VarInt,
    val pickupItemCount: VarInt
)