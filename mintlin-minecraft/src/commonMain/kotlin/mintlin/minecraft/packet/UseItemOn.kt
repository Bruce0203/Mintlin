package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Face
import mintlin.minecraft.datastructure.FloatPosition
import mintlin.minecraft.datastructure.Hand
import mintlin.minecraft.datastructure.Position
import mintlin.serializer.VarInt

@Serializable
data class UseItemOn(
    val hand: Hand,
    val location: Position,
    @Serializable(Face.VarIntSerializer::class)
    val face: Face,
    val cursorPosition: FloatPosition,
    val insideBlock: Boolean,
    override val sequenceId: VarInt
) : SequenceIdentifier