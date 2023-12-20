package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.VarInt

@Serializable
data class SetExperience(
    val experienceBar: Float,
    val level: VarInt,
    val totalExperience: VarInt
)