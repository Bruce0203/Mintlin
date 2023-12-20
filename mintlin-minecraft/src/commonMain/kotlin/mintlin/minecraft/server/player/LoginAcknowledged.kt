package mintlin.minecraft.server.player

import mintlin.cachedPacket
import mintlin.datastructure.scope.Scope
import mintlin.datastructure.scope.get
import mintlin.io.network.protocol.ConnectionStateDelegate
import mintlin.lang.Init
import mintlin.minecraft.network.PacketListener
import mintlin.minecraft.network.PacketWriter
import mintlin.minecraft.packet.*
import mintlin.minecraft.packet.LoginAcknowledged
import mintlin.minecraft.registry.DamageTypes

data class LoginAcknowledged(
    private val packetListener: PacketListener,
    private val packetWriter: PacketWriter,
    private val connectionStateDelegate: ConnectionStateDelegate,
    private val player: Player,
    val scope: Scope
) : Init(player.listeners {
    val featureFlags = cachedPacket {
        FeatureFlags(featureFlags = arrayOf("minecraft:vanilla"))
    }
    val pluginMessage = cachedPacket {
        ClientBoundPluginMessage(channel = "minecraft:brand", data = "mintlin".encodeToByteArray())
    }
    val registryData = cachedPacket {
        RegistryData(
            RegistryCodec(
                ChatTypeRegistry(
                    "minecraft:chat_type", arrayOf(
                        Entry(
                            id = 0, name = "minecraft:chat", element = ChatType(
                                chat = Decoration(
                                    translationKey = "chat.type.text",
                                    parameters = arrayOf("sender", "content"), style = null
                                ),
                                narration = Decoration(
                                    translationKey = "chat.type.text.narrate",
                                    parameters = arrayOf("sender", "content"), style = null
                                )
                            )
                        )
                    )
                ),
                TrimPatternRegistry("minecraft:trim_pattern", arrayOf()),
                TrimMaterialRegistry("minecraft:trim_material", arrayOf()),
                DamageTypes.registry,
                DimensionTypeRegistry(
                    "minecraft:dimension_type", arrayOf(
                        Entry(
                            id = 0, name = "minecraft:overworld", element = DimensionType(
                                fixedTime = null,
                                hasSkyLight = true,
                                hasCeiling = false,
                                ultraWarm = false,
                                natural = true,
                                coordinateScale = 1.0,
                                bedWorks = true,
                                respawnAnchorWorks = false,
                                minY = -64,
                                height = 384,
                                logicalHeight = 384,
                                infinityBurning = "#minecraft:infiniburn_overworld",
                                effects = "minecraft:overworld",
                                ambientLight = 0f,
                                piglinSafe = false,
                                hasRaids = true,
                                monsterSpawnLightLevel = MonsterSpawnLightLevel.Compounded(
                                    type = "minecraft:uniform",
                                    value = MonsterSpawnLightLevel.Compounded.Value(minInclusive = 0, maxInclusive = 7)
                                ),
                                monsterSpawnBlockLightLevel = 0
                            )
                        )
                    )
                ),
                Registry(
                    type = "minecraft:worldgen/biome", value = arrayOf(
                        Entry(
                            name = "minecraft:plains",
                            id = 0,
                            element = Biome(
                                hasPrecipitation = false,
                                temperature = 0.5f,
                                temperatureModifier = null,
                                downfall = 0.5f,
                                effects = Biome.Effects(
                                    fogColor = 12638463,
                                    waterColor = 4159204,
                                    waterFogColor = 329011,
                                    skyColor = 8103167,
                                    foliageColor = null,
                                    grassColor = null,
                                    grassColorModifier = null,
                                    particle = null,
                                    ambientSound = null,
                                    moodSound = null,
                                    additionsSound = null,
                                    music = null
                                )
                            )
                        )
                    )
                )
            )
        )
    }
    packetListener.onEvent<LoginAcknowledged> {
        scope.get<ConfigurationState>()
        packetWriter.sendCachedPacket(pluginMessage)
        packetWriter.sendCachedPacket(featureFlags)
        packetWriter.sendCachedPacket(registryData)
        packetWriter.send(FinishConfiguration())
    }
})