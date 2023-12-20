package mintlin.minecraft.server.player

import mintlin.minecraft.server.server.Server
import mintlin.io.network.Tick
import mintlin.lang.Init
import mintlin.minecraft.network.PacketWriter

class Broadcaster(
    player: Player,
    packetWriter: PacketWriter,
    server: Server
) : Init(player.listeners {
    server.onEvent<Tick> {
//        packetWriter.send(SystemChatMessage(StringComponent("Hello"), false))
    }
})
