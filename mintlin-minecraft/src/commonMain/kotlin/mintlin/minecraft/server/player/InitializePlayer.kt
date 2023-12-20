package mintlin.minecraft.server.player

import mintlin.datastructure.scope.Scope
import mintlin.datastructure.scope.get
import mintlin.minecraft.server.EntitySpawnEvent
import mintlin.minecraft.server.PlayerJoinEvent
import mintlin.minecraft.server.entity.Entity
import mintlin.minecraft.server.entity.metadata.*
import mintlin.minecraft.server.server.MessageOfTheDayAccessor
import mintlin.minecraft.server.server.Server
import mintlin.minecraft.server.server.factory.EntityIdFactory
import mintlin.io.network.protocol.ConnectionStateDelegate
import mintlin.lang.Init
import mintlin.minecraft.datastructure.*
import mintlin.minecraft.datastructure.level.World
import mintlin.minecraft.network.PacketListener
import mintlin.minecraft.network.PacketWriter
import mintlin.minecraft.packet.*
import kotlin.random.Random

data class InitializePlayer(
    val packetListener: PacketListener,
    val packetWriter: PacketWriter,
    val scope: Scope,
    val connectionStateModifier: ConnectionStateDelegate,
    val server: Server,
    val player: Player,
    val entity: Entity,
    val world: World
) : Init(player.listeners {
    server.onEvent<PlayerJoinEvent> { if (it.player == player) player.dispatch(it) }
    val newEntityId by scope.get<EntityIdFactory>()
    val messageOfTheDay by scope.get<MessageOfTheDayAccessor>()
    packetListener.onEvent<FinishConfiguration> {
        scope.get<PlayState>()
        player.type = EntityType.Player
        player.gameMode = GameMode.Adventure
        player.isOnGround = false
        player.id = newEntityId
        fun rand() = Random.nextDouble(-8.0, 8.0)
        player.pos = DoublePosition(8.5 + rand(), 65.5, 6.5 + rand())
//        player.pos = DoublePosition(8.5, 65.5, 6.5)
        player.rot = FloatRotation(0, 0)

        player.isInvulnerable = false
        player.isFlying = true
        player.isFlyingAllowed = true
        player.isInstantBreak = false
//        player.flyingSpeed = 0.05f
//        player.modifier = 0.1f

        player.heldSlot = 0

        player.viewDistance = 32
        player.simulationDistance = 32

        player.attributes[AttributeKey.FlyingSpeed].value = 0.05

        packetWriter.send(
            LoginPlay(
                entityId = player.id,
                isHardcore = false,
                dimensionNames = arrayOf("minecraft:overworld"),
                maxPlayers = 20,
                viewDistance = 32,
                simulationDistance = 32,
                reducedDebugInfo = false,
                enableRespawnScreen = true,
                doLimitedCrafting = false,
                dimensionType = "minecraft:overworld",
                dimensionName = "minecraft:overworld",
                hashedSeed = 0,
                gameMode = player.gameMode,
                isDebug = false,
                isFlat = true,
                deathLocation = null,
                portalCoolDown = 0
            )
        )
//    }
//
//    packetListener.onEvent<PlayerSession> {
//        playerSession = it
        server.dispatch(PlayerJoinEvent(player))
        packetWriter.send(
            ClientBoundPlayerAbilities(
                isInvulnerable = player.isInvulnerable,
                isFlying = player.isFlying,
                isFlyingAllowed = player.isFlyingAllowed,
                isInstantBreak = player.isInstantBreak,
                flyingSpeed = player.attributes[AttributeKey.FlyingSpeed].value.toFloat(),
                fieldOfViewModifier = 0.1f
            )
        )
        packetWriter.send(ClientBoundSetHeldItem(slot = player.heldSlot))
//        packetWriter.send(CachedPackets.UPDATE_RECIPES)
        packetWriter.send(SetDefaultPosition(location = player.pos.abs(), yawAngle = player.rot.yaw))
        packetWriter.send(
            ServerData(
                messageOfTheDay = messageOfTheDay,
                icon = null, enforceSecureChat = true
            )
        )
        packetWriter.send(
            PlayerInfoUpdate(
                players = arrayOf(
                    PlayerInfo(
                        uuid = player.uuid, actions = arrayOf(
                            PlayerInfo.AddPlayer(
                                name = player.name,
                                properties = if (player.texture === null) emptyArray() else arrayOf(
                                    PlayerInfo.AddPlayer.Property(
                                        "textures",
                                        player.texture!!,
                                        player.session.keySignature
                                    )
                                )
                            ),
                            PlayerInfo.InitializeChat(signatureData = player.session.chatSignature),
                            PlayerInfo.UpdateGameMode(gameMode = player.gameMode),
                            PlayerInfo.UpdateListed(listed = true),
                            PlayerInfo.UpdateLatency(ping = 0),
                            PlayerInfo.UpdateDisplayName(displayName = player.customName)
                        )
                    )
                )
            )
        )
        packetWriter.send(SetRenderDistance(viewDistance = player.viewDistance))
        packetWriter.send(SetSimulationDistance(simulationDistance = player.simulationDistance))
        packetWriter.send(SetCenterChunk(chunkX = 0, chunkZ = 0))
        packetWriter.send(
            UpdateAttributes(
                player.id, arrayOf(
                    Attribute(AttributeKey.MovementSpeed, 0.1, arrayOf())
                )
            )
        )
        packetWriter.send(UpdateTime(worldAge = 10, timeOfDay = 0))
        packetWriter.send(
            SynchronizePlayerPosition(
                Location(player.pos, FloatRotation(yaw = 0f, pitch = 0f)), flags = 0, teleportId = 1
            )
        )
        packetWriter.send(
            SetContainerContent(
                windowId = 0, stateId = 1, slots = Array(46) { Slot(item = null) },
                carriedItem = Slot(item = null)
            )
        )
        packetWriter.send(SetContainerSlot(windowId = 0, stateId = 2, index = 45, slot = Slot(item = null)))
//        packetWriter.send(CachedPackets.COMMANDS)
        packetWriter.send(SetEntityMetadata(id = player.id, metadata = Metadata().apply {
            with(PlayerMetadata(this)) {
                health = 20f
                isCapeEnabled = true
                isJacketEnabled = true
                isLeftSleeveEnabled = true
                isRightSleeveEnabled = true
                isLeftPantsLegEnabled = true
                isRightPantsLegEnabled = true
                isHatEnabled = true
            }
        }))
//        packetWriter.send(CachedPackets.UPDATE_ADVANCEMENTS)
        packetWriter.send(SetHealth(health = 20f, food = 20, foodSaturation = 5f))
        packetWriter.send(UpdateTime(1000L, 100L))
        server.dispatch(EntitySpawnEvent(entity))
        packetWriter.send(ClientBoundChangeDifficulty(difficulty = Difficulty.Easy, isDifficultyLocked = false))
        (-8..8).forEach { x ->
            (-8..8).forEach { z ->
                val chunk = world.getChunkAt(Point2D(x, z))?.value ?: return@forEach
                packetWriter.send(UpdateLight(chunk.x, chunk.z, chunk.light))
            }
        }
        (-8..8).forEach { x ->
            (-8..8).forEach { z ->
                val chunk = world.getChunkAt(Point2D(x, z)) ?: return@forEach
                packetWriter.sendCachedPacket(chunk)
            }
        }
        packetWriter.send(SystemChatMessage(StringComponent("안녕하세요? 서버를 전부 갈아엎고 있어요 ㅋㅋㅋㅋ"), false))
    }
})