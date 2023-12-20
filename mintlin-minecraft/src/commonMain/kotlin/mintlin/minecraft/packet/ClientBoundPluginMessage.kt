package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Identifier
import mintlin.serializer.RemainedBytesMax32767
import mintlin.serializer.VarIntSizedByteArraySerializer

@Serializable
data class ClientBoundPluginMessage(
    val channel: Identifier,
    @Serializable(VarIntSizedByteArraySerializer::class)
    val data: ByteArray
)

@Serializable
data class ServerBoundPluginMessage(
    val channel: Identifier,
    @Serializable(RemainedBytesMax32767::class)
    val data: ByteArray
)