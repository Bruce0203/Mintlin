package mintlin.minecraft.server

import Chunk
import mintlin.cachedPacket
import mintlin.minecraft.server.entity.Entity
import mintlin.minecraft.server.player.Player
import mintlin.lazyCachedPacket
import mintlin.minecraft.datastructure.*
import mintlin.minecraft.datastructure.level.Light
import mintlin.minecraft.packet.*

class PlayerJoinEvent(val player: Player)

class PlayerLeaveEvent(val player: Player)

class EntitySpawnEvent(val entity: Entity)

class EntityRemoveEvent(val entity: Entity)

class EntityMoveEvent(
    val entity: Entity,
    val from: Location,
    val to: Location,
    val type: MovementType
) {
    val velocity: DoublePosition get() = (to.position - from.position) * 4096

    val movePackets by lazy {
        when (type) {
            MovementType.SetPosition ->
                arrayOf(cachedPacket { UpdateEntityPosition(entity.id, velocity.toShort(), entity.isOnGround) })

            MovementType.SetRotation -> {
                val rotation = to.rotation.toAngle()
                arrayOf(
                    cachedPacket { UpdateEntityRotation(entity.id, rotation, entity.isOnGround) },
                    cachedPacket { SetHeadRotation(entity.id, headYaw = rotation.yaw) }
                )
            }

            MovementType.SetPositionAndRotation -> {
                val rotation = to.rotation.toAngle()
                val shortVelocity = velocity.toShort()
                arrayOf(
                    cachedPacket {
                        UpdateEntityPositionAndRotation(
                            entity.id,
                            shortVelocity,
                            rotation,
                            entity.isOnGround
                        )
                    },
                    cachedPacket { SetHeadRotation(entity.id, headYaw = rotation.yaw) }
                )
            }
        }
    }

}

class EntitySneakingEvent(val entity: Entity, val isSneaking: Boolean)

class EntitySwingArmEvent(val entity: Entity, val hand: Hand)

class EntityInteractEvent(
    val entity: Entity,
    val target: Entity,
    val interaction: Interaction,
    val isSneaking: Boolean
)

class BlockPlaceEvent(val entity: Entity, val position: Position, val block: Int)

class BlockBreakEvent(val entity: Entity, val position: Position)

class PlayerChatEvent(
    val player: Player, val message: String, val packet: PlayerChatMessage
) {
    val playerChatMessagePacket = lazyCachedPacket { packet }
}

class EntityVelocityEvent(val entity: Entity, val strength: Float, val velocity: Vector)

class EntityAttackEvent(
    val entity: Entity,
    val target: Entity,
    val isSneaking: Boolean
)

class LightUpdateEvent(val chunk: Chunk, val light: Light) {
    val lightUpdatePacket = lazyCachedPacket { UpdateLight(chunk.x, chunk.z, light) }
}