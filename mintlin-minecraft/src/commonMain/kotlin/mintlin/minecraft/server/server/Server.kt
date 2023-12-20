package mintlin.minecraft.server.server

import Chunk
import kotlinx.serialization.decodeFromByteArray
import mintlin.cachedPacket
import mintlin.datastructure.eventbus.EventBus
import mintlin.datastructure.eventbus.EventBusImp
import mintlin.datastructure.scope.*
import mintlin.format.packet.PacketFormat
import mintlin.io.compression.zLibCompression
import mintlin.io.encryption.createRSACipher
import mintlin.io.network.Selector
import mintlin.io.network.ServerTickRateDelegate
import mintlin.lang.Init
import mintlin.logger.Logger
import mintlin.minecraft.datastructure.Point2D
import mintlin.minecraft.datastructure.Point3D
import mintlin.minecraft.datastructure.level.ChunkData
import mintlin.minecraft.datastructure.level.World
import mintlin.minecraft.server.server.factory.EntityIdFactory
import mintlin.minecraft.server.server.factory.PlayerFactory
import mintlin.minecraft.server.server.factory.TeleportIdFactory
import mintlin.minecraft.server.server.list.EntityList
import mintlin.minecraft.server.server.list.EntityListFetcher
import mintlin.minecraft.server.server.list.PlayerList
import mintlin.minecraft.server.server.list.PlayerListFetcher
import java.io.File

class ServerImp(scope: Scope) : Server, EventBus by EventBusImp(scope), Scope by scope, Init({
    scope.get<World>()
})

interface Server : EventBus, Scope {
    companion object : ScopeFactoryDSL<Server, Selector> by scopedDSL({
        singleOf(::ServerState)
        single { Logger(Logger.Level.INFO) }
        singleOf(::ServerImp) binds arrayOf(Server::class, EventBus::class)
        singleOf(::ServerTickRateDelegate)
        singleOf(::createRSACipher)
        singleOf(::MojangAPI)
        singleOf(::HttpRequestThreadPool)
        singleOf(::PlayerList)
        singleOf(::PlayerListFetcher)
        singleOf(::EntityList)
        singleOf(::EntityIdFactory)
        singleOf(::TeleportIdFactory)
        singleOf(::EntityListFetcher)
        singleOf(::BlockUpdater)
        singleOf(::PlayerFactory)
        singleOf(::ChunkLoader)
        singleOf(::MessageOfTheDayManipulatorImp) binds arrayOf(
            MessageOfTheDayAccessor::class, MessageOfTheDayManipulator::class
        )
        single {
            val file = File("worlds/world.dat")
            if (file.exists()) {
                val bytes = zLibCompression.decompress(file.readBytes())
//                val bytes = file.readBytes()
                PacketFormat.decodeFromByteArray<World>(bytes)
            } else World(-64, 320).apply {
                (-10..10).forEach { x ->
                    (-10..10).forEach { z ->
                        val pos = Point2D(x, z)
                        setChunkAt(pos, cachedPacket { Chunk(x, z, chunkData = ChunkData(getSectionSize())) })
                        (-8..<24).forEach { z ->
                            (-8..<24).forEach { x ->
                                setBlockAt(Point3D(x, 63, z), 79)
                            }
                        }
                    }
                }
            }
        } bind World::class
    })
}
