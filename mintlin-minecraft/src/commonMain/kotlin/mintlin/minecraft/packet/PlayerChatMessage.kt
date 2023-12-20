package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf
import mintlin.minecraft.datastructure.BitSet
import mintlin.minecraft.datastructure.Chat
import mintlin.serializer.*

@Serializable
data class PlayerChatMessage(
    @Serializable(UUIDSerializer::class)
    val sender: UUID,
    val index: VarInt,
    @Serializable(MessageSignatureSerializer::class)
    val messageSignature: ByteArray? = null,
    @Serializable(VarString256Serializer::class)
    val message: String,
    val timestamp: Long,
    val salt: Long,
    @Serializable(PreviousMessagesSerializer::class)
    val previousMessages: Array<PreviousMessage>,
    val unsignedContent: Chat? = null,
    val filterType: FilterType,
    val chatType: VarInt,
    val senderName: Chat,
    val targetName: Chat? = null
) {

    @Serializable(FilterType.Serializer::class)
    sealed interface FilterType {
        companion object Serializer : KSerializer<FilterType> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<FilterType>())

            override fun deserialize(decoder: Decoder) =
                when (val value = VarIntSerializer.deserialize(decoder)) {
                    0 -> PassThrough; 1 -> FullyFiltered
                    2 -> PartiallyFiltered.serializer().deserialize(decoder)
                    else -> throw AssertionError("invalid filter type ordinal $value")
                }

            override fun serialize(encoder: Encoder, value: FilterType) {
                VarIntSerializer.serialize(
                    encoder, when (value) {
                        PassThrough -> 0
                        FullyFiltered -> 1
                        is PartiallyFiltered -> 2
                    }
                )
                if (value is PartiallyFiltered) {
                    PartiallyFiltered.serializer().serialize(encoder, value)
                }
            }
        }

        @Serializable
        data object PassThrough : FilterType

        @Serializable
        data object FullyFiltered : FilterType

        @Serializable
        data class PartiallyFiltered(val bits: BitSet) : FilterType
    }


    object PreviousMessagesSerializer :
        KSerializer<Array<PreviousMessage>> by varIntSizedArraySerializer(PreviousMessage.serializer())

    @Serializable(PreviousMessage.Serializer::class)
    data class PreviousMessage(
        val messageId: VarInt,
        val signature: ByteArray? = null
    ) {
        companion object Serializer : KSerializer<PreviousMessage> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<PreviousMessage>())

            override fun deserialize(decoder: Decoder): PreviousMessage {
                val messageId = VarIntSerializer.deserialize(decoder)
                return PreviousMessage(
                    messageId = messageId,
                    signature = if (messageId == 0) MessageSignatureSerializer.deserialize(decoder) else null
                )

            }

            override fun serialize(encoder: Encoder, value: PreviousMessage) {
                TODO("Not yet implemented")
            }

        }
    }

    object MessageSignatureSerializer : KSerializer<ByteArray> by FixedSizeByteArraySerializer(256)
}