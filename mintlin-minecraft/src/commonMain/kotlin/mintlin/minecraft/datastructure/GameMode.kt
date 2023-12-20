package mintlin.minecraft.datastructure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.ByteEnum
import mintlin.serializer.varByteEnumSerializer

@Serializable(GameMode.Serializer::class)
enum class GameMode(override val value: Int) : ByteEnum {
    Survival(0), Creative(1),
    Adventure(2), Spectator(3);

    companion object Serializer : KSerializer<GameMode> by varByteEnumSerializer(entries)

    fun toGameModeOrUndefined() = when (this) {
        Survival -> GameModeOrUndefined.Survival
        Creative -> GameModeOrUndefined.Creative
        Adventure -> GameModeOrUndefined.Adventure
        Spectator -> GameModeOrUndefined.Spectator
    }
}

@Serializable(GameModeOrUndefined.Serializer::class)
enum class GameModeOrUndefined(override val value: Int) : ByteEnum {
    Survival(0), Creative(1),
    Adventure(2), Spectator(3),
    Undefined(-1);

    companion object Serializer : KSerializer<GameModeOrUndefined> by varByteEnumSerializer(entries)

    fun toGameMode() = when (this) {
        Survival -> GameMode.Survival
        Creative -> GameMode.Creative
        Adventure -> GameMode.Adventure
        Spectator -> GameMode.Spectator
        Undefined -> throw RuntimeException()
    }
}
