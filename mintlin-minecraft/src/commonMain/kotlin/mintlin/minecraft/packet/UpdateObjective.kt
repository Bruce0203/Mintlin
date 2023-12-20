package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.minecraft.datastructure.Chat
import mintlin.serializer.VarIntEnum
import mintlin.serializer.VarString32767Serializer
import mintlin.lang.classNameOf
import mintlin.serializer.varIntEnumSerializer

@Serializable
data class UpdateObjective(
    @Serializable(VarString32767Serializer::class)
    val name: String,
    val mode: Byte,
) {
    @Serializable(ScoreboardType.Serializer::class)
    enum class ScoreboardType(override val value: Int) : VarIntEnum {
        Integer(0), Hearts(1);

        companion object Serializer : KSerializer<ScoreboardType> by varIntEnumSerializer(entries)
    }

    @Serializable
    sealed interface Mode {
        @Serializable
        data class Create(
            val displayedText: Chat?,
            val type: ScoreboardType,
        ) : Mode

        @Serializable
        data class Update(
            val displayedText: Chat?,
            val type: ScoreboardType,
        ) : Mode

        @Serializable
        data object Remove : Mode

        companion object Serializer : KSerializer<Mode> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<Mode>())

            override fun deserialize(decoder: Decoder) = when (val mode = decoder.decodeByte().toInt()) {
                0 -> Create.serializer().deserialize(decoder)
                1 -> Remove
                2 -> Update.serializer().deserialize(decoder)
                else -> throw AssertionError("unknown update objective mode $mode")
            }

            override fun serialize(encoder: Encoder, value: Mode) {
                encoder.encodeByte(
                    when (value) {
                        is Create -> 0
                        is Remove -> 1
                        is Update -> 1
                    }
                )
                when (value) {
                    is Create -> Create.serializer().serialize(encoder, value)
                    is Remove -> Remove.serializer().serialize(encoder, value)
                    is Update -> Update.serializer().serialize(encoder, value)
                }
            }

        }
    }
}
