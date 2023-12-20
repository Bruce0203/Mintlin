package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.RemainedBytesSerializer
import mintlin.serializer.VarInt

@Serializable
data class LoginPluginResponse(
    val messageID: VarInt,
    val successful: Boolean,
    @Serializable(RemainedBytesSerializer::class)
    val data: ByteArray
)