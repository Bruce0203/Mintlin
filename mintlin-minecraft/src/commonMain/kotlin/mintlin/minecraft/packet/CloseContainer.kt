package mintlin.minecraft.packet

import kotlinx.serialization.Serializable

@Serializable
data class CloseContainer(val windowId: Byte)
