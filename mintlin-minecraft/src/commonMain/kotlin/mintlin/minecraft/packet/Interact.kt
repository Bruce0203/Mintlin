package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.minecraft.datastructure.FloatPosition
import mintlin.minecraft.datastructure.Hand
import mintlin.serializer.VarInt
import mintlin.serializer.VarIntSerializer
import mintlin.lang.classNameOf

@Serializable
data class Interact(
    override val entityId: VarInt,
    val interact: Interaction,
    val isSneaking: Boolean
) : IdentifiedEntity

@Serializable(Interaction.Serializer::class)
sealed interface Interaction {
    sealed interface Handed {
        val hand: Hand
    }

    @Serializable
    data class Interact(override val hand: Hand) : Interaction, Handed

    @Serializable
    data class InteractAt(val target: FloatPosition, override val hand: Hand) : Interaction, Handed

    @Serializable
    data object Attack : Interaction
    companion object Serializer : KSerializer<Interaction> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<Interaction>())

        override fun deserialize(decoder: Decoder): Interaction =
            when (VarIntSerializer.deserialize(decoder)) {
                0 -> Interact.serializer().deserialize(decoder)
                1 -> Attack.serializer().deserialize(decoder)
                2 -> InteractAt.serializer().deserialize(decoder)
                else -> throw RuntimeException()
            }

        override fun serialize(encoder: Encoder, value: Interaction) {
            when (value) {
                is Interact -> {
                    VarIntSerializer.serialize(encoder, 0)
                    Interact.serializer().serialize(encoder, value)
                }

                is Attack -> {
                    VarIntSerializer.serialize(encoder, 1)
                    Attack.serializer().serialize(encoder, value)
                }

                is InteractAt -> {
                    VarIntSerializer.serialize(encoder, 2)
                    InteractAt.serializer().serialize(encoder, value)
                }
            }
        }
    }
}