@file:Suppress("ArrayInDataClass")

package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.VarIntSizedByteArraySerializer
import mintlin.serializer.VarString20Serializer

@Serializable
data class EncryptionRequest(
    @Serializable(VarString20Serializer::class)
    val serverID: String,
    @Serializable(VarIntSizedByteArraySerializer::class)
    val publicKey: ByteArray,
    @Serializable(VarIntSizedByteArraySerializer::class)
    val verifyToken: ByteArray
)