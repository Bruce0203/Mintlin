package mintlin.minecraft.server.player

import mintlin.datastructure.eventbus.Priority
import mintlin.minecraft.server.EntityRemoveEvent
import mintlin.minecraft.server.EntitySpawnEvent
import mintlin.minecraft.server.PlayerJoinEvent
import mintlin.minecraft.server.PlayerLeaveEvent
import mintlin.minecraft.server.entity.Entity
import mintlin.minecraft.server.server.Server
import mintlin.minecraft.server.server.list.EntityList
import mintlin.lang.Init
import mintlin.minecraft.datastructure.*
import mintlin.minecraft.network.PacketWriter
import mintlin.minecraft.packet.*
import mintlin.minecraft.server.server.list.PlayerList

class EntitySpawner(
    val server: Server,
    val packetWriter: PacketWriter,
    private val thisEntity: Entity,
    private val player: Player,
    private val entities: EntityList,
    private val players: PlayerList,
) : Init(player.listeners {
    server.onEvent<PlayerLeaveEvent>(Priority.LOW) {
        if (it.player == player) {
            server.dispatch(EntityRemoveEvent(thisEntity))
        }
    }
    server.onEvent<PlayerJoinEvent>(Priority.HIGHEST) {
        packetWriter.send(PlayerInfoUpdate((players.mapNotNull { otherPlayer ->
            if (otherPlayer == player) return@mapNotNull null
            PlayerInfo(uuid = otherPlayer.uuid, arrayOf(
                PlayerInfo.AddPlayer("???", arrayOf()),
                PlayerInfo.InitializeChat(signatureData = otherPlayer.session.chatSignature),
                PlayerInfo.UpdateGameMode(gameMode = otherPlayer.gameMode),
                PlayerInfo.UpdateListed(listed = otherPlayer.isListed),
                PlayerInfo.UpdateLatency(ping = otherPlayer.latency),
                PlayerInfo.UpdateDisplayName(displayName = StringComponent("???"))))
        }.toTypedArray())))
        entities.forEach { entity ->
            entity.dispatch(EntitySpawnEvent(thisEntity))
            if (entity != thisEntity) {
                thisEntity.dispatch(EntitySpawnEvent(entity))
            }
        }
    }
    server.onEvent<EntityRemoveEvent> {
        val entity = it.entity
        if (entity == thisEntity) return@onEvent
        packetWriter.send(RemoveEntities(arrayOf(entity.id)))
        packetWriter.send(PlayerInfoRemove(players = arrayOf(entity.uuid)))
    }
    server.onEvent<EntitySpawnEvent> {
        thisEntity.dispatch(it)
    }
    server.onEvent<PlayerJoinEvent> {
        if (it.player == player) return@onEvent
        packetWriter.send(PlayerInfoUpdate(arrayOf(PlayerInfo(uuid = it.player.uuid, arrayOf(
            PlayerInfo.AddPlayer(it.player.name, arrayOf()),
            PlayerInfo.InitializeChat(signatureData = it.player.session.chatSignature),
            PlayerInfo.UpdateGameMode(gameMode = it.player.gameMode),
            PlayerInfo.UpdateListed(listed = it.player.isListed),
            PlayerInfo.UpdateLatency(ping = it.player.latency),
            PlayerInfo.UpdateDisplayName(displayName = it.player.customName)
        )))))
    }
    thisEntity.onEvent<EntitySpawnEvent> {
        val entity = it.entity
        if (entity == thisEntity) return@onEvent
        val angle = entity.rot.toAngle()
        packetWriter.send(
            SpawnEntity(
                entity.id, entity.uuid, entity.type,
                entity.pos, angle, 0.angle, data = 0,
                velocity = ShortPosition(0, 0, 0)
            )
        )
        packetWriter.send(SetHeadRotation(entity.id, angle.yaw))
    }
})