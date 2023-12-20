@file:Suppress("ArrayInDataClass")

package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Chat
import mintlin.minecraft.datastructure.JsonChatSerializer
import mintlin.serializer.VarIntSizedByteArraySerializer

@Serializable
data class ServerData(
    @Serializable(JsonChatSerializer::class)
    val messageOfTheDay: Chat,
    @Serializable(VarIntSizedByteArraySerializer::class)
    val icon: ByteArray? = null,
    val enforceSecureChat: Boolean
)