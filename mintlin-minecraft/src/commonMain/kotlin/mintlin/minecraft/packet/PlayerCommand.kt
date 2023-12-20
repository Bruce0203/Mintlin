package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.VarInt
import mintlin.serializer.VarIntEnum
import mintlin.serializer.varIntEnumSerializer

@Serializable
data class PlayerCommand(
    override val entityId: VarInt,
    val actionId: Action,
    val jumpBoost: VarInt = 0
) : IdentifiedEntity {
    @Serializable(Action.Serializer::class)
    enum class Action(override val value: Int) : VarIntEnum {
        StartSneaking(0), StopSneaking(1),
        LeaveBed(2), StartSprinting(3), StopSprinting(4),
        StartJumpWithHorse(5), StopJumpWithHorse(6),
        OpenHorseInventory(7), StartFlyingWithElyTra(8);

        companion object Serializer : KSerializer<Action> by varIntEnumSerializer(entries)
    }
}