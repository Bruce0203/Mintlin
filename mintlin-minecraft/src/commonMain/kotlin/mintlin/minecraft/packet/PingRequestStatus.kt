package mintlin.minecraft.packet

import kotlinx.serialization.Serializable

@Serializable
data class PingRequestStatus(val payload: Long)