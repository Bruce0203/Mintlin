package mintlin.minecraft.packet

import kotlinx.serialization.Serializable

@Serializable
data class UpdateTime(val worldAge: Long, val timeOfDay: Long)