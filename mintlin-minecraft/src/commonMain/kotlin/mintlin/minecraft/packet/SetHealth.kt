package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.VarInt

@Serializable
data class SetHealth(
    val health: Float,
    val food: VarInt,
    val foodSaturation: Float
)