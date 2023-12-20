package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Position
import mintlin.serializer.VarInt

@Serializable
data class SetBlockDestroyStage(
    override val entityId: VarInt,
    val location: Position,
    val destroyStage: Byte
) : IdentifiedEntity
