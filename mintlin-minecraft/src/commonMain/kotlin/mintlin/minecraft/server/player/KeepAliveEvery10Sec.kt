package mintlin.minecraft.server.player

import mintlin.minecraft.server.server.Server
import mintlin.io.network.Tick
import mintlin.lang.Init
import mintlin.minecraft.network.PacketWriter
import mintlin.minecraft.packet.KeepAlive
import mintlin.minecraft.server.PlayerLeaveEvent
import kotlin.random.Random

class KeepAliveEvery10Sec(packetWriter: PacketWriter, player: Player, server: Server) : Init(player.listeners {
    var i = 0
    server.onEvent<Tick> {
        if (i++ != 20) return@onEvent
        else i = 0
        try {
            packetWriter.send(KeepAlive(Random.nextLong()))
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
})
