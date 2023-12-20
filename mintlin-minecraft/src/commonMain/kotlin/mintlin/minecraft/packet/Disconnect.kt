@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package mintlin.minecraft.packet

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.minecraft.datastructure.Chat
import mintlin.minecraft.datastructure.JsonChatSerializer
import mintlin.minecraft.datastructure.StringComponent
import mintlin.lang.classNameOf

@Serializable
data class Disconnect(val reason: Reason) {
    @Serializable(Reason.Serializer::class)
    sealed interface Reason {
        companion object Serializer : KSerializer<Reason> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<Reason>())

            override fun deserialize(decoder: Decoder): Reason {
                return TextReason(JsonChatSerializer.deserialize(decoder))
            }

            override fun serialize(encoder: Encoder, value: Reason) {
                when (value) {
                    is TextReason -> JsonChatSerializer.serialize(encoder, value.chat)
                    is ConnectionBroke -> JsonChatSerializer.serialize(
                        encoder,
                        StringComponent(ConnectionBroke.toString())
                    )
                }
            }
        }
    }

    data object ConnectionBroke : Reason

    data class TextReason(val chat: Chat) : Reason
}