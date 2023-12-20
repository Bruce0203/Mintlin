package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Slot
import mintlin.serializer.VarInt

@Serializable
data class SetContainerSlot(
    val windowId: Byte,
    val stateId: VarInt,
    val index: Short,
    val slot: Slot
)