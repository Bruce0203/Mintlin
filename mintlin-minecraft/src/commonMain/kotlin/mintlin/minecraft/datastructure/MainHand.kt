package mintlin.minecraft.datastructure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.VarIntEnum
import mintlin.serializer.varIntEnumSerializer

@Serializable
enum class MainHand(override val value: Int) : VarIntEnum {
    Left(0), Right(1);

    companion object Serializer : KSerializer<MainHand> by varIntEnumSerializer(entries)
}