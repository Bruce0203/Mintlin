package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.BitSet
import mintlin.minecraft.datastructure.FixedBitSetSerializer
import mintlin.serializer.*

@Serializable
data class ChatCommand(
    @Serializable(VarString256Serializer::class)
    val command: String,
    val timestamp: Long,
    val salt: Long,
    @Serializable(SignaturesSerializer::class)
    val signatures: Array<Signature>,
    val messageCount: VarInt,
    @Serializable(AcknowledgedSerializer::class)
    val acknowledged: BitSet
) {
    data object AcknowledgedSerializer : KSerializer<BitSet> by FixedBitSetSerializer(size = 20)

    @Serializable
    data class Signature(
        @Serializable(VarString16Serializer::class)
        val argumentName: String,
        @Serializable(SignatureDataSerializer::class)
        val signature: ByteArray
    )

    data object SignatureDataSerializer : KSerializer<ByteArray> by FixedSizeByteArraySerializer(size = 256)
    data object SignaturesSerializer :
        KSerializer<Array<Signature>> by varIntSizedArraySerializer(
            Signature.serializer()
        )
}