@file:Suppress("ArrayInDataClass")

package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.VarIntSizedByteArraySerializer

@Serializable
data class EncryptionResponse(
    @Serializable(VarIntSizedByteArraySerializer::class)
    val sharedSecret: ByteArray,
    @Serializable(VarIntSizedByteArraySerializer::class)
    val verifyToken: ByteArray
)