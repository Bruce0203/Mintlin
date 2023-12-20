package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.ByteToIntSerializer
import mintlin.serializer.ShortSerializer

@Serializable
data class ClientBoundSetHeldItem(
    @Serializable(ByteToIntSerializer::class)
    val slot: Int
)

@Serializable
data class ServerBoundSetHeldItem(
    @Serializable(ShortSerializer::class)
    val slot: Int
)
