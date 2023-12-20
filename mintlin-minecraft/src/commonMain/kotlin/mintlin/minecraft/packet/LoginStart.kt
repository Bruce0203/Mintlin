package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.UUID
import mintlin.serializer.UUIDSerializer
import mintlin.serializer.VarString16Serializer

@Serializable
data class LoginStart(
    @Serializable(VarString16Serializer::class)
    val name: String,
    @Serializable(UUIDSerializer::class)
    val playerUUID: UUID,
)