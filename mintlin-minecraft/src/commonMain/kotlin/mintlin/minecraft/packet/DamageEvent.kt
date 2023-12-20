package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.VarInt

@Serializable
data class DamageEvent(
    override val entityId: VarInt,
    val sourceTypeId: VarInt,
    val sourceCauseId: VarInt,
    val sourceDirectId: VarInt,
    val hasSourceRotation: SourcePosition?,
) : IdentifiedEntity

@Serializable
data class SourcePosition(
    val x: Double,
    val y: Double,
    val z: Double
)
