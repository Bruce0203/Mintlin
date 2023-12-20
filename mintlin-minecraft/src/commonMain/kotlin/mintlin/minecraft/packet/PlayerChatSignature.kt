@file:Suppress("ArrayInDataClass")

package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.UUID
import mintlin.serializer.UUIDSerializer
import mintlin.serializer.VarIntSizedBytesMax4096
import mintlin.serializer.VarIntSizedBytesMax512

@Serializable
data class PlayerChatSignature(
    @Serializable(UUIDSerializer::class)
    val chatSessionId: UUID,
    val publicKeyExpiryTime: Long,
    @Serializable(VarIntSizedBytesMax512::class)
    val encodedPublicKey: ByteArray,
    @Serializable(VarIntSizedBytesMax4096::class)
    val publicKeySignature: ByteArray,
)