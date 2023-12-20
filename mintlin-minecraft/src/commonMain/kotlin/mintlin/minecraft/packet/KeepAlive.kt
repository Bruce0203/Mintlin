package mintlin.minecraft.packet

import kotlinx.serialization.Serializable

@Serializable
data class KeepAlive(val keepAliveId: Long)
