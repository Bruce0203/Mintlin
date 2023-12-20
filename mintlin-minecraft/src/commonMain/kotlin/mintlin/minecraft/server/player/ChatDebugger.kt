package mintlin.minecraft.server.player

import kotlinx.serialization.encodeToByteArray
import mintlin.minecraft.server.entity.Entity
import mintlin.minecraft.server.server.Server
import mintlin.minecraft.server.server.factory.PlayerFactory
import mintlin.format.packet.PacketFormat
import mintlin.io.compression.zLibCompression
import mintlin.lang.Init
import mintlin.minecraft.datastructure.Point2D
import mintlin.minecraft.datastructure.StringComponent
import mintlin.minecraft.datastructure.level.World
import mintlin.minecraft.network.PacketListener
import mintlin.minecraft.network.PacketWriter
import mintlin.minecraft.packet.ChatMessage
import mintlin.minecraft.packet.SystemChatMessage
import java.io.File

class ChatDebugger(
    private val packetWriter: PacketWriter,
    packetListener: PacketListener,
    private val server: Server,
    private val player: Player,
    private val entity: Entity,
    playerFactory: PlayerFactory,
    world: World
) : Init(player.listeners {
    packetListener.onEvent<ChatMessage> {
        if (it.message != "respawn!") {
            if (it.message == "optimize!") {
                world.getAllChunks().toTypedArray().map { it.value }.filter {
                    it.chunkData.chunkSections.all { it.blockStates.count == 0 }
                }.forEach { world.setChunkAt(Point2D(it.x, it.z), null) }
                packetWriter.send(SystemChatMessage(StringComponent("optimized!Done"), false))
            }
            if (it.message == "save!") {
                val file = File("worlds/world.dat")
                if (!file.exists()) {
                    file.parentFile.mkdirs()
                    file.createNewFile()
                }
                file.writeBytes(zLibCompression.compress(PacketFormat.encodeToByteArray(world)))
                packetWriter.send(SystemChatMessage(StringComponent("saved!Done"), false))
            }
        }
//            return@onEvent
//        packetWriter.send(ConfigurationAcknowledged())
//        this.cancelEvent()
//        var connectionState by scope.get<ConnectionStateManipulator>()
//        connectionState = ConnectionState.Configuration
//        server.dispatch(PlayerLeaveEvent(player))
//        val connection = scope.get<Connection>()
//        entity.scope.close()
//        player.scope.close()
//        val newPlayer = playerFactory.createPlayer(connection)
//        newPlayer.scope.get<LoginState>()
//        newPlayer.name = player.name
//        newPlayer.customName = player.customName
//        var playerUUID by newPlayer.scope.get<PlayerUUIDManipulator>()
//        playerUUID = player.uuid
//        packetListener.dispatch(LoginAcknowledged())
    }
})