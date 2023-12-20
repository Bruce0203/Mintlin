package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Chat

@Serializable
data class SystemChatMessage(
    val content: Chat,
    val overlay: Boolean
)