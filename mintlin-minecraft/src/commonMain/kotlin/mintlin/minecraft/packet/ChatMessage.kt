package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.BitSet
import mintlin.minecraft.datastructure.FixedBitSetSerializer
import mintlin.serializer.FixedSizeByteArraySerializer
import mintlin.serializer.VarInt
import mintlin.serializer.VarString256

@Serializable
data class ChatMessage(
    val message: VarString256,
    val timestamp: Long,
    val salt: Long,
    @Serializable(SignatureSerializer::class)
    val signature: ByteArray?,
    val messageCount: VarInt,
    @Serializable(Fixed20BitSet::class)
    val acknowledged: BitSet
)

object Fixed20BitSet : KSerializer<BitSet> by FixedBitSetSerializer(size = 20)

object SignatureSerializer : KSerializer<ByteArray> by FixedSizeByteArraySerializer(256)