package mintlin.minecraft.server

import kotlinx.datetime.Clock
import mintlin.datastructure.scope.get
import mintlin.datastructure.scope.invoke
import mintlin.io.network.SelectorTicker
import mintlin.io.network.Tick
import mintlin.io.network.createSocketServer
import mintlin.io.network.protocol.Bound
import mintlin.logger.Logger
import mintlin.minecraft.server.handshake.HandShakeFetcher
import mintlin.minecraft.server.server.Server
import mintlin.minecraft.network.PacketChannel
import mintlin.minecraft.network.PacketReader
import mintlin.minecraft.registry.MinecraftProtocol
import mintlin.minecraft.registry.Registry
import mintlin.minecraft.registry.RegistryEntry
import mintlin.minecraft.server.server.ServerState

fun main() {
    val registry = Registry(RegistryEntry(MinecraftProtocol.v1_20_2, Bound.Server))
    val startTime = Clock.System.now()
    val server = Server(arrayOf(registry))
    server.get<ServerState>()
    val selector = createSocketServer(port = 25565, listener = { socketChannel ->
        val packetChannel = PacketChannel.invoke(socketChannel, server)
        HandShakeFetcher(packetChannel)
        socketChannel.setSocketReadListener(packetChannel.get<PacketReader>())
        socketChannel.setSocketCloseListener(packetChannel)
    }).let { SelectorTicker(it) }
    val duration = Clock.System.now() - startTime
    server.get<Logger>().info { "Done ($duration)! Server Initiated" }
    selector.run(onTick = { server.dispatch(Tick) })
}