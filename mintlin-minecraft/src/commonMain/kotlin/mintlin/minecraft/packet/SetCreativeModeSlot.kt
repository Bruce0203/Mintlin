package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Slot
import mintlin.minecraft.datastructure.SlotSerializer
import mintlin.serializer.ShortSerializer

@Serializable
data class SetCreativeModeSlot(
    @Serializable(ShortSerializer::class)
    val slot: Int,
    @Serializable(SlotSerializer::class)
    val clickedItem: Slot
)