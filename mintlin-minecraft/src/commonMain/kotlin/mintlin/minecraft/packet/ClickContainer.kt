package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Slot
import mintlin.serializer.VarInt
import mintlin.serializer.VarIntEnum
import mintlin.serializer.varIntEnumSerializer
import mintlin.serializer.varIntSizedArraySerializer

@Serializable
data class ClickContainer(
    val windowId: Byte,
    val stateId: VarInt,
    val slot: Short,
    val button: Byte,
    val mode: Mode,
    @Serializable(NewSlotsSerializer::class)
    val newSlots: Array<NewSlot>,
    val carriedItem: Slot
)

@Serializable
data class NewSlot(val number: Short, val data: Slot)
object NewSlotsSerializer :
    KSerializer<Array<NewSlot>> by varIntSizedArraySerializer(NewSlot.serializer())

@Serializable(Mode.Serializer::class)
enum class Mode(override val value: Int) : VarIntEnum {
    Clicking(0), Shifting(1), Swapping(2), MiddleClicking(3),
    Dropping(4), Dragging(5), DoubleClicking(6);

    companion object Serializer : KSerializer<Mode> by varIntEnumSerializer(entries)
}