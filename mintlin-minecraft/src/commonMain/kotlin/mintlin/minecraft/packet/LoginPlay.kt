package mintlin.minecraft.packet

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.*
import mintlin.serializer.VarInt

@Serializable
data class LoginPlay @OptIn(ExperimentalSerializationApi::class) constructor(
    override val entityId: Int,
    val isHardcore: Boolean,
    @Suppress("ArrayInDataClass")
    @Serializable(IdentifiersSerializer::class)
    val dimensionNames: Array<String>,
    val maxPlayers: VarInt,
    val viewDistance: VarInt,
    val simulationDistance: VarInt,
    val reducedDebugInfo: Boolean,
    val enableRespawnScreen: Boolean,
    val doLimitedCrafting: Boolean,
    val dimensionType: Identifier,
    val dimensionName: Identifier,
    val hashedSeed: Long,
    val gameMode: GameMode,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    val previousGameMode: GameModeOrUndefined = GameModeOrUndefined.Undefined,
    val isDebug: Boolean,
    val isFlat: Boolean,
    val deathLocation: DeathLocation? = null,
    val portalCoolDown: VarInt
) : IdentifiedEntity {

    @Serializable
    data class DeathLocation(
        val deathDimensionName: Identifier,
        val deathLocation: Position
    )

}
