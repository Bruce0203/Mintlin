package mintlin.minecraft.datastructure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.VarIntEnum
import mintlin.serializer.varIntEnumSerializer

@Serializable(Hand.Serializer::class)
enum class Hand(override val value: Int) : VarIntEnum {
    MainHand(0), OffHand(1);

    companion object Serializer : KSerializer<Hand> by varIntEnumSerializer(entries)
}
