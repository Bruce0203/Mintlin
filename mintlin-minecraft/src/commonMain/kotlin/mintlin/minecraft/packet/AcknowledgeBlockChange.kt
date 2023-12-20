package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.VarInt

@Serializable
data class AcknowledgeBlockChange(override val sequenceId: VarInt) : SequenceIdentifier
