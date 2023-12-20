package mintlin.minecraft.server.player

import mintlin.datastructure.eventbus.Priority
import mintlin.minecraft.server.PlayerLeaveEvent
import mintlin.minecraft.server.server.Server
import mintlin.lang.Init
import mintlin.minecraft.network.ConnectionSocketClosedEvent
import mintlin.minecraft.network.PacketChannel
import mintlin.minecraft.network.PacketListener
import mintlin.minecraft.server.entity.Entity

class Disconnection(
    private val packetChannel: PacketChannel,
    val packetListener: PacketListener,
    val server: Server,
    val player: Player,
    val entity: Entity
) : Init(packetChannel.listeners {
    packetChannel.onEvent<ConnectionSocketClosedEvent> {
        server.dispatch(PlayerLeaveEvent(player))
    }
    packetChannel.onEvent<ConnectionSocketClosedEvent>(Priority.MONITOR) {
        player.closeScope()
        entity.closeScope()
    }
})