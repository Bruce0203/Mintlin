package mintlin.minecraft.server.player

import mintlin.datastructure.scope.Scope
import mintlin.datastructure.scope.get
import mintlin.minecraft.server.EntityMoveEvent
import mintlin.minecraft.server.EntitySneakingEvent
import mintlin.minecraft.server.entity.*
import mintlin.minecraft.server.entity.metadata.PlayerMetadata
import mintlin.minecraft.server.entity.metadata.pose
import mintlin.minecraft.server.server.Server
import mintlin.lang.Init
import mintlin.minecraft.datastructure.Metadata
import mintlin.minecraft.datastructure.Pose
import mintlin.minecraft.network.PacketListener
import mintlin.minecraft.network.PacketWriter
import mintlin.minecraft.packet.*

class PlayerMovement(
    packetWriter: PacketWriter, packetListener: PacketListener,
    player: Player, scope: Scope, server: Server, entity: Entity,
) : Init(player.listeners {
    var pos by scope.get<EntityPositionManipulator>()
    var rot by scope.get<EntityRotationManipulator>()
    var location by scope.get<EntityLocationManipulator>()
    var isOnGround by scope.get<EntityIsOnGroundManipulator>()
    packetListener.onEvent<SetPlayerPosition> {
        val from = location
        pos = it.position
        isOnGround = it.isOnGround
        val to = location
        server.dispatch(
            EntityMoveEvent(
                entity,
                from = from,
                to = to,
                type = MovementType.SetPosition
            )
        )
//        packetWriter.send(SynchronizePlayerPosition(to, newTeleportId))
    }
    packetListener.onEvent<SetPlayerRotation> {
        val from = location
        rot = it.rotation
        isOnGround = it.isOnGround
        val to = location
        server.dispatch(
            EntityMoveEvent(
                entity,
                from = from,
                to = to,
                type = MovementType.SetRotation
            )
        )
//        packetWriter.send(SynchronizePlayerPosition(to, newTeleportId))
    }
    packetListener.onEvent<SetPlayerPositionAndRotation> {
        val from = location
        location = it.location
        isOnGround = it.isOnGround
        val to = location
        server.dispatch(
            EntityMoveEvent(
                entity,
                from = from,
                to = to,
                type = MovementType.SetPositionAndRotation
            )
        )
//        packetWriter.send(SynchronizePlayerPosition(to, newTeleportId))
    }
    packetListener.onEvent<PlayerCommand> {
        when (it.actionId) {
            PlayerCommand.Action.StartSneaking -> {
                server.dispatch(EntitySneakingEvent(entity, isSneaking = true))
            }

            PlayerCommand.Action.StopSneaking -> {
                server.dispatch(EntitySneakingEvent(entity, isSneaking = false))
            }

            PlayerCommand.Action.LeaveBed -> {}
            PlayerCommand.Action.StartSprinting -> {}
            PlayerCommand.Action.StopSprinting -> {}
            PlayerCommand.Action.StartJumpWithHorse -> {}
            PlayerCommand.Action.StopJumpWithHorse -> {}
            PlayerCommand.Action.OpenHorseInventory -> {}
            PlayerCommand.Action.StartFlyingWithElyTra -> {}
        }
    }
    server.onEvent<EntitySneakingEvent> {
        val sprintingEntity = it.entity
        if (sprintingEntity == entity) return@onEvent
        val metadata = Metadata().apply {
            PlayerMetadata(this).pose = if (it.isSneaking) Pose.Sneaking else Pose.Standing
        }
        packetWriter.send(SetEntityMetadata(sprintingEntity.id, metadata))
    }
    server.onEvent<EntityMoveEvent> {
        val movingEntity = it.entity
        if (entity == movingEntity) return@onEvent
        packetWriter.sendCachedPackets(*it.movePackets)
    }
})
