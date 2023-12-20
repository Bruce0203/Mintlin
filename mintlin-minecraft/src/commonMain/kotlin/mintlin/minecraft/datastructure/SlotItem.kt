package mintlin.minecraft.datastructure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mintlin.format.nbt.nbtSerializer
import mintlin.serializer.VarInt
import mintlin.serializer.varIntSizedArraySerializer

@Serializable
data class Slot(
    val item: SlotItem? = null
)

object SlotSerializer : KSerializer<Slot> by Slot.serializer()
object SlotsSerializer : KSerializer<Array<Slot>> by varIntSizedArraySerializer(Slot.serializer())

@Serializable
data class SlotItem(
    val itemId: VarInt,
    val amount: Byte,
    @Serializable(ItemNbtSerializer::class)
    val meta: ItemNbt
)

@Serializable
data class ItemNbt(
    @SerialName("Damage")
    val damage: Int? = null
)

object ItemNbtSerializer : KSerializer<ItemNbt> by nbtSerializer(ItemNbt.serializer())

