package mintlin.minecraft.packet

import kotlinx.serialization.Serializable

@Serializable
data class PingResponse(val payload: Long)
