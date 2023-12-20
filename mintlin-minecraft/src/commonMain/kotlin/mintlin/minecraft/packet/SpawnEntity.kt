@file:Suppress("SpellCheckingInspection")

package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.AngleRotation
import mintlin.minecraft.datastructure.DoublePosition
import mintlin.minecraft.datastructure.EntityType
import mintlin.minecraft.datastructure.ShortPosition
import mintlin.serializer.UUID
import mintlin.serializer.UUIDSerializer
import mintlin.serializer.VarInt

@Serializable
data class SpawnEntity(
    override val entityId: VarInt,
    @Serializable(UUIDSerializer::class)
    val entityUuid: UUID,
    val entityType: EntityType,
    val position: DoublePosition,
    val rotation: AngleRotation,
    val headYaw: mintlin.minecraft.datastructure.Angle,
    val data: VarInt,
    val velocity: ShortPosition
) : IdentifiedEntity

