package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.VarIntEnum
import mintlin.serializer.varIntEnumSerializer

@Serializable
data class ClientStatus(val action: Action)

@Serializable(Action.Serializer::class)
enum class Action(override val value: Int) : VarIntEnum {
    PerformRespawn(0), RequestStats(1);

    companion object Serializer : KSerializer<Action> by varIntEnumSerializer(entries)
}