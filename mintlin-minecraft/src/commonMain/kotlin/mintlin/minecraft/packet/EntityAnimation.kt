package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.ByteEnum
import mintlin.serializer.VarInt
import mintlin.serializer.varByteEnumSerializer

@Serializable
data class EntityAnimation(
    override val entityId: VarInt,
    val animation: Animation
) : IdentifiedEntity

@Serializable(Animation.Serializer::class)
enum class Animation(override val value: Int) : ByteEnum {
    SwingMainArm(0),
    LeaveBed(2),
    SwingOffHand(3),
    CriticalEffect(4),
    MagicCriticalEffect(5);

    companion object Serializer : KSerializer<Animation> by varByteEnumSerializer(entries)
}