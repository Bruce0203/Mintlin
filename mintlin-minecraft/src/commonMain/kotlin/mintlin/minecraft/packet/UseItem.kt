package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Hand
import mintlin.serializer.VarInt

@Serializable
data class UseItem(val hand: Hand, override val sequenceId: VarInt) : SequenceIdentifier