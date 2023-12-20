@file:Suppress("ArrayInDataClass")

package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Attribute
import mintlin.minecraft.datastructure.AttributesSerializer
import mintlin.serializer.VarInt

@Serializable
data class UpdateAttributes(
    override val entityId: VarInt,
    @Serializable(AttributesSerializer::class)
    val attributes: Array<Attribute>
) : IdentifiedEntity