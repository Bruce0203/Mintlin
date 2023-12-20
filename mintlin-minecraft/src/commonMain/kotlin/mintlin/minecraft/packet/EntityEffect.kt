package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.VarInt

@Serializable
data class EntityEffect(
    override val entityId: VarInt,
    val effectId: VarInt,
    val amplifier: Byte,
    @Serializable
    val duration: Int,
    val flags: Byte
) : IdentifiedEntity