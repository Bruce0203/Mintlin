package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Face
import mintlin.minecraft.datastructure.Position
import mintlin.serializer.VarInt
import mintlin.serializer.VarIntEnum
import mintlin.serializer.varIntEnumSerializer

@Serializable
data class PlayerAction(
    val status: Status,
    val location: Position,
    val face: Face,
    override val sequenceId: VarInt
) : SequenceIdentifier {

    @Serializable(Status.Serializer::class)
    enum class Status(override val value: Int) : VarIntEnum {
        StartedDigging(0), CancelledDigging(1), FinishedDigging(2),
        DropItemStack(3), DropItem(4), ShootArrowOrFinishEating(5), SwapItemInHand(6);

        companion object Serializer : KSerializer<Status> by varIntEnumSerializer(entries)
    }
}

