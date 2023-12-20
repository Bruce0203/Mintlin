package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Slot
import mintlin.minecraft.datastructure.SlotsSerializer
import mintlin.serializer.VarInt

@Serializable
data class SetContainerContent(
    val windowId: Byte,
    val stateId: VarInt,
    @Serializable(SlotsSerializer::class)
    val slots: Array<Slot>,
    val carriedItem: Slot
)