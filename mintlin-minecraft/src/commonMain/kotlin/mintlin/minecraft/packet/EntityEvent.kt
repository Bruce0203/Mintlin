package mintlin.minecraft.packet

import kotlinx.serialization.Serializable

@Serializable
data class EntityEvent(
    override val entityId: Int,
    val status: Byte
) : IdentifiedEntity
