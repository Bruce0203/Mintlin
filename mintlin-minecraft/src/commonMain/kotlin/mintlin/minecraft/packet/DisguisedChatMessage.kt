package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Chat
import mintlin.serializer.VarInt

@Serializable
data class DisguisedChatMessage(
    val message: Chat,
    val chatType: VarInt,
    val sender: Chat,
    val targetName: Chat?
)