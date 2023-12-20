package mintlin.minecraft.client

import mintlin.datastructure.eventbus.EventBusImp
import mintlin.datastructure.eventbus.Priority
import mintlin.datastructure.scope.ScopeImp
import mintlin.datastructure.scope.get
import mintlin.datastructure.scope.invoke
import mintlin.io.network.*
import mintlin.io.network.protocol.*
import mintlin.logger.Logger
import mintlin.minecraft.datastructure.DoublePosition
import mintlin.minecraft.datastructure.MainHand
import mintlin.minecraft.datastructure.bitSetOf
import mintlin.minecraft.network.CompressionState
import mintlin.minecraft.network.PacketChannel
import mintlin.minecraft.network.PacketListener
import mintlin.minecraft.network.PacketWriter
import mintlin.minecraft.packet.*
import mintlin.minecraft.registry.MinecraftProtocol
import mintlin.minecraft.registry.Registry
import mintlin.minecraft.registry.RegistryEntry
import mintlin.serializer.UUID
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.properties.Delegates
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

fun main() {
    val host = "localhost"
//    val host = "ang-ang-ang.kro.kr"
    val port = 25565
    val threads = 4
    val threadPool = Executors.newFixedThreadPool(threads)
    repeat(threads) {
        threadPool.execute {
            runClient(host, port, 125, delay = 150L)
        }
        Thread.sleep(300L)
        println("Done #$it")
    }
    println("Done all!")
}

fun runClient(host: String, port: Int, amount: Int, delay: Long) {
    val registry = Registry(RegistryEntry(MinecraftProtocol.v1_20_2, Bound.Client), Logger(Logger.Level.INFO))
    val tickListener = EventBusImp(ScopeImp())
    lateinit var selector: ClientSelector
    selector = clientSelectorFactory.createClientSelector(listener = { socketChannel ->
        val packetChannel = PacketChannel(socketChannel, registry)
        socketChannel.setSocketCloseListener(packetChannel)
        socketChannel.setSocketReadListener(packetChannel.get())
        val packetWriter = packetChannel.get<PacketWriter>()
        val packetListener = packetChannel.get<PacketListener>()
        val protocolEntry by packetChannel.get<ProtocolEntryDelegate>()
        var connectionState by packetChannel.get<ConnectionStateDelegate>()
        var threshold by packetChannel.get<CompressionState>()
        var entityId by Delegates.notNull<Int>()
        var pos: DoublePosition? = null
        packetListener.listeners {
            packetListener.onEvent<FinishConfiguration> {
                packetWriter.send(FinishConfiguration())
                connectionState = ConnectionState.Play
                packetWriter.send(
                    ClientInformation(
                        "ko_kr",
                        12,
                        ChatMode.Enabled,
                        true,
                        127,
                        MainHand.Right,
                        true,
                        true
                    )
                )
            }
            packetListener.onEvent<LoginSuccess> {
                packetWriter.send(LoginAcknowledged())
                connectionState = ConnectionState.Configuration
            }
            packetListener.onEvent<SetCompression> { threshold = it.threshold }
            packetListener.onEvent<LoginPlay> { entityId = it.entityId }
            packetListener.onEvent<SynchronizePlayerPosition> {
                packetWriter.send(ConfirmTeleportation(it.teleportId))
            }
            packetListener.onEvent<SynchronizePlayerPosition>(Priority.HIGH) {
                closeListener()
                pos = it.location.position
                packetWriter.send(SetPlayerPositionAndRotation(it.location, isOnGround = true))
                packetWriter.send(PlayerCommand(entityId = entityId, actionId = PlayerCommand.Action.StartSneaking))
            }
//            packetListener.onEvent<KeepAlive> { packetWriter.send(KeepAlive(it.keepAliveId)) }
            packetListener.onEvent<SynchronizePlayerPosition> {

                packetWriter.send(
                    ChatMessage(
                        "asdf", 1702111903883,
                        salt = -3906506481784680069, signature = null,
                        messageCount = 0, acknowledged = bitSetOf()
                    )
                )
//            return@onEvent
                pos = it.location.position
                closeListener()
//            println("-".repeat(100))
            }
            var i = 0
            tickListener.onEvent<Tick> {
                return@onEvent
                if (pos === null) return@onEvent
//                if (++delay < 20*10) return@onEvent
                var x = pos!!.x
                if (++i == 10) i = 0
                if (i < 5) x += 0.3 else x -= 0.3
                packetWriter.send(SetPlayerPosition(DoublePosition(x, pos!!.y, pos!!.z), true))
            }
        }
        packetWriter.send(HandShake(
            protocolVersion = protocolEntry.id,
            serverAddress = host,
            serverPort = port,
            nextState = NextState.Login
        ))
        connectionState = ConnectionState.Login
        packetWriter.send(LoginStart(name = "test_${abs(Random.nextInt()/10)}", playerUUID = UUID.randomUUID()))
    })
    val selectorTicker = SelectorTicker(selector)
    var tickRate by selectorTicker.get<ServerTickRateDelegate>()
    tickRate = 50.milliseconds
    tickListener.listeners {
        var delay = 0
        tickListener.onEvent<Tick> {
            if (++delay != 20) return@onEvent
            delay = 0
            selector.createClient(host, port)
        }
    }
    selectorTicker.run {
        tickListener.dispatch(Tick)
    }
}