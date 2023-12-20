package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import mintlin.datastructure.toFast
import mintlin.minecraft.datastructure.GameMode
import mintlin.lang.classNameOf

@Serializable
data class GameEvent(val event: Event) {
    @Serializable(Event.Serializer::class)
    sealed interface Event {
        companion object Serializer : KSerializer<Event> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<Event>())

            @Suppress("unchecked_cast")
            private val hashCodeToSerializers = listOf(
                hashCodeToSerializer<NoRespawnBlockAvailable>(),
                hashCodeToSerializer<BeginRaining>(),
                hashCodeToSerializer<EndRaining>(),
                hashCodeToSerializer<ChangeGameMode>(),
                hashCodeToSerializer<WinGame>(),
                hashCodeToSerializer<DemoEvent>(),
                hashCodeToSerializer<ArrowHitPlayer>(),
                hashCodeToSerializer<RainLevelChange>(),
                hashCodeToSerializer<ThunderLevelChange>(),
                hashCodeToSerializer<PlayPufferFishStingSound>(),
                hashCodeToSerializer<PlayElderDragonMobAppearance>(),
                hashCodeToSerializer<EnableRespawnScreen>(),
                hashCodeToSerializer<LimitedCrafting>()
            ).toMap() as Map<Int, KSerializer<Event>>

            private val idToSerializers = hashCodeToSerializers.run {
                (0..<size).zip(values).toFast()
            }

            private val hashCodeToId = hashCodeToSerializers.run {
                keys.zip(0..<size).toFast()
            }

            private inline fun <reified T> hashCodeToSerializer() = T::class.hashCode() to serializer<T>()

            override fun deserialize(decoder: Decoder): Event {
                val event = decoder.decodeByte().toInt()
                val serializer = idToSerializers[event]
                    ?: throw AssertionError("unknown game event id $event")
                return serializer.deserialize(decoder)
            }

            override fun serialize(encoder: Encoder, value: Event) {
                val id = hashCodeToId[value::class.hashCode()]
                    ?: throw RuntimeException("unknown event class ${value::class}")
                encoder.encodeByte(id.toByte())
                val serializer = hashCodeToSerializers[value::class.hashCode()]
                    ?: throw RuntimeException("unknown event class ${value::class}")
                serializer.serialize(encoder, value)
            }
        }
    }

    @Serializable
    data class NoRespawnBlockAvailable(private val value: Float) : Event

    @Serializable
    data class BeginRaining(private val value: Float) : Event

    @Serializable
    data class EndRaining(private val value: Float) : Event

    @Serializable(ChangeGameMode.Serializer::class)
    class ChangeGameMode(val gameMode: GameMode) : Event {
        companion object Serializer : KSerializer<ChangeGameMode> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<ChangeGameMode>())

            override fun deserialize(decoder: Decoder) = ChangeGameMode(
                gameMode = when (val value = decoder.decodeFloat()) {
                    0f -> GameMode.Survival
                    1f -> GameMode.Creative
                    2f -> GameMode.Adventure
                    3f -> GameMode.Spectator
                    else -> throw AssertionError("unknown change game mode event value $value")
                }
            )

            override fun serialize(encoder: Encoder, value: ChangeGameMode) {
                encoder.encodeFloat(
                    when (value.gameMode) {
                        GameMode.Survival -> 0f
                        GameMode.Creative -> 1f
                        GameMode.Adventure -> 2f
                        GameMode.Spectator -> 3f
                        else -> throw RuntimeException()
                    }
                )
            }
        }
    }

    @Serializable(WinGame.Serializer::class)
    class WinGame(val hasCredits: Boolean) : Event {
        companion object Serializer : KSerializer<WinGame> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<WinGame>())

            override fun deserialize(decoder: Decoder): WinGame {
                val value = decoder.decodeFloat()
                return WinGame(hasCredits = value == 1f)
            }

            override fun serialize(encoder: Encoder, value: WinGame) {
                encoder.encodeFloat(if (value.hasCredits) 1f else 0f)
            }
        }
    }

    @Serializable(DemoEvent.Serializer::class)
    enum class DemoEvent(private val value: Float) : Event {
        ShowWelcome(0f),
        TellMovements(101f), TellJump(102f), TellInventory(103f),
        TellTheDemoIsOverAndPrintAMessageAboutHowToTakeAScreenShot(104f);

        companion object Serializer : KSerializer<DemoEvent> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<DemoEvent>())

            override fun deserialize(decoder: Decoder): DemoEvent {
                val value = decoder.decodeFloat()
                return DemoEvent.entries.firstOrNull { value == it.value }
                    ?: throw AssertionError("unknown game event value $value")
            }

            override fun serialize(encoder: Encoder, value: DemoEvent) = encoder.encodeFloat(value.value)
        }
    }

    @Serializable
    data class ArrowHitPlayer(private val value: Float) : Event

    @Serializable(RainLevelChange.Serializer::class)
    class RainLevelChange(val value: Float) : Event {
        init {
            assertValueRangeIsInZeroToOne(value)
        }

        companion object Serializer : KSerializer<RainLevelChange> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<RainLevelChange>())

            override fun deserialize(decoder: Decoder): RainLevelChange {
                val value = decoder.decodeFloat()
                return RainLevelChange(value)
            }

            override fun serialize(encoder: Encoder, value: RainLevelChange) {
                encoder.encodeFloat(value.value)
            }
        }
    }

    @Serializable(ThunderLevelChange.Serializer::class)
    class ThunderLevelChange(val value: Float) : Event {
        init {
            assertValueRangeIsInZeroToOne(value)
        }

        companion object Serializer : KSerializer<ThunderLevelChange> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<ThunderLevelChange>())

            override fun deserialize(decoder: Decoder): ThunderLevelChange {
                val value = decoder.decodeFloat()
                return ThunderLevelChange(value)
            }

            override fun serialize(encoder: Encoder, value: ThunderLevelChange) {
                encoder.encodeFloat(value.value)
            }
        }
    }

    @Serializable
    data class PlayPufferFishStingSound(private val value: Float) : Event

    @Serializable
    data class PlayElderDragonMobAppearance(private val value: Float) : Event

    @Serializable(EnableRespawnScreen.Serializer::class)
    data class EnableRespawnScreen(val hasRespawnScreen: Boolean) : Event {
        companion object Serializer : KSerializer<EnableRespawnScreen> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<EnableRespawnScreen>())

            override fun deserialize(decoder: Decoder): EnableRespawnScreen {
                val value = decoder.decodeFloat()
                val hasRespawnScreen = value == 1f
                return EnableRespawnScreen(hasRespawnScreen = hasRespawnScreen)
            }

            override fun serialize(encoder: Encoder, value: EnableRespawnScreen) {
                encoder.encodeFloat(if (value.hasRespawnScreen) 1f else 0f)
            }
        }
    }

    @Serializable(LimitedCrafting.Serializer::class)
    data class LimitedCrafting(val isEnabled: Boolean) : Event {
        companion object Serializer : KSerializer<LimitedCrafting> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<LimitedCrafting>())

            override fun deserialize(decoder: Decoder): LimitedCrafting {
                val value = decoder.decodeFloat()
                val isEnabled = value == 1f
                return LimitedCrafting(isEnabled = isEnabled)
            }

            override fun serialize(encoder: Encoder, value: LimitedCrafting) {
                encoder.encodeFloat(if (value.isEnabled) 1f else 0f)
            }
        }
    }
}

private fun assertValueRangeIsInZeroToOne(value: Float) = assert((0f..1f).contains(value)) {
    "rain level change game event value out of range is 0 to 1 but $value"
}