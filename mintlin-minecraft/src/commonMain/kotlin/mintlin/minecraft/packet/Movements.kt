package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.*
import mintlin.serializer.VarInt
import kotlin.experimental.and
import kotlin.experimental.or

interface Standing {
    val isOnGround: Boolean
}

enum class MovementType { SetPosition, SetRotation, SetPositionAndRotation }

@Serializable
data class SetDefaultPosition(
    val location: Position,
    val yawAngle: Float
)

@Serializable
data class SetPlayerPosition(
    val position: DoublePosition,
    override val isOnGround: Boolean
) : Standing

@Serializable
data class SetPlayerPositionAndRotation(
    val location: Location,
    override val isOnGround: Boolean
) : Standing

@Serializable
data class SetPlayerRotation(
    val rotation: FloatRotation,
    override val isOnGround: Boolean
) : Standing

@Serializable
data class SetPlayerOnGround(
    override val isOnGround: Boolean
) : Standing

@Serializable
data class SynchronizePlayerPosition(
    val location: Location,
    val flags: Byte,
    val teleportId: VarInt
) {
    constructor(location: Location, teleportId: VarInt) : this(location, 0, teleportId)

    val x get() = flags has X
    val y get() = flags has Y
    val z get() = flags has Z
    val pitch get() = flags has PITCH
    val yaw get() = flags has YAW

    fun x() = copy(flags = flags or X)
    fun y() = copy(flags = flags or Y)
    fun z() = copy(flags = flags or Z)
    fun pitch() = copy(flags = flags or PITCH)
    fun yaw() = copy(flags = flags or YAW)

    fun xyz() = copy(flags = ((flags or X) or Y) or Z)
    fun rot() = copy(flags = (flags or PITCH) or YAW)
    fun xyzAndRot() = copy(flags = ((((flags or X) or Y) or Z) or PITCH) or YAW)

    companion object {
        private const val X: Byte = 0x01
        private const val Y: Byte = 0x02
        private const val Z: Byte = 0x04
        private const val PITCH: Byte = 0x08
        private const val YAW: Byte = 0x10

        private infix fun Byte.has(flag: Byte) = this and flag == flag

        operator fun invoke(location: Location, teleportId: VarInt) =
            SynchronizePlayerPosition(location, 0, teleportId)
    }
}

@Serializable
data class UpdateEntityPositionAndRotation(
    override val entityId: VarInt,
    val delta: ShortPosition,
    val rotation: AngleRotation,
    override val isOnGround: Boolean
) : Standing, IdentifiedEntity

@Serializable
data class UpdateEntityRotation(
    override val entityId: VarInt,
    val rotation: AngleRotation,
    override val isOnGround: Boolean
) : Standing, IdentifiedEntity

@Serializable
data class UpdateEntityPosition(
    override val entityId: VarInt,
    val delta: ShortPosition,
    override val isOnGround: Boolean
) : Standing, IdentifiedEntity

@Serializable
data class SetHeadRotation(
    override val entityId: VarInt,
    val headYaw: Angle
) : IdentifiedEntity

@Serializable
data class TeleportEntity(
    override val entityId: VarInt,
    val position: DoublePosition,
    val rotation: AngleRotation,
    override val isOnGround: Boolean,
) : Standing, IdentifiedEntity

@Serializable
data class SetEntityVelocity(
    override val entityId: VarInt,
    val velocity: ShortPosition,
) : IdentifiedEntity