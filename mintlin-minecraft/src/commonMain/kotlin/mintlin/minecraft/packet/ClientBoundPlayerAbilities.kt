package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf
import kotlin.experimental.and

@Serializable(ClientBoundPlayerAbilities.Serializer::class)
data class ClientBoundPlayerAbilities(
    val isInvulnerable: Boolean,
    val isFlying: Boolean,
    val isFlyingAllowed: Boolean,
    val isInstantBreak: Boolean,
    val flyingSpeed: Float,
    val fieldOfViewModifier: Float
) {
    companion object Serializer : KSerializer<ClientBoundPlayerAbilities> {
        override val descriptor =
            buildClassSerialDescriptor(classNameOf<ClientBoundPlayerAbilities>())

        override fun deserialize(decoder: Decoder): ClientBoundPlayerAbilities {
            val flag = decoder.decodeByte()
            return ClientBoundPlayerAbilities(
                isInvulnerable = flag has 0x01,
                isFlying = flag has 0x02,
                isFlyingAllowed = flag has 0x04,
                isInstantBreak = flag has 0x08,
                flyingSpeed = decoder.decodeFloat(),
                fieldOfViewModifier = decoder.decodeFloat()
            )
        }

        private infix fun Byte.has(flag: Byte) = this and flag == flag

        override fun serialize(encoder: Encoder, value: ClientBoundPlayerAbilities) {
            var flag = 0x00
            if (value.isInvulnerable) flag = flag or 0x01
            if (value.isFlying) flag = flag or 0x02
            if (value.isFlyingAllowed) flag = flag or 0x04
            if (value.isInstantBreak) flag = flag or 0x08
            encoder.encodeByte(flag.toByte())
            encoder.encodeFloat(value.flyingSpeed)
            encoder.encodeFloat(value.fieldOfViewModifier)
        }

    }
}

