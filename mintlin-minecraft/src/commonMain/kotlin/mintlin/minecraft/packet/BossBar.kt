package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf
import mintlin.minecraft.datastructure.Chat
import mintlin.serializer.*
import kotlin.experimental.and
import kotlin.experimental.or

@Serializable
data class BossBar(
    @Serializable(UUIDSerializer::class)
    val uuid: UUID,
    val action: Action
) {
    interface ActionFlags {
        var flag: Byte
    }

    var ActionFlags.darkenSky: Boolean
        get() = flag and 0x01 == 0x01.toByte()
        set(value) {
            flag = flag or 0x01
        }

    var ActionFlags.isDragonBar: Boolean
        get() = flag and 0x02 == 0x02.toByte()
        set(value) {
            flag = flag or 0x02
        }

    var ActionFlags.isCreateFrog: Boolean
        get() = flag and 0x01 == 0x04.toByte()
        set(value) {
            flag = flag or 0x04
        }

    @Serializable(Action.Serializer::class)
    sealed interface Action {
        companion object Serializer : KSerializer<Action> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<Action>())

            override fun deserialize(decoder: Decoder): Action {
                return when (decoder.decodeInt()) {
                    0 -> Add.serializer().deserialize(decoder)
                    1 -> Remove
                    2 -> UpdateHealth.serializer().deserialize(decoder)
                    3 -> UpdateTitle.serializer().deserialize(decoder)
                    4 -> UpdateStyle.serializer().deserialize(decoder)
                    5 -> UpdateFlags.serializer().deserialize(decoder)
                    else -> throw RuntimeException()
                }
            }

            override fun serialize(encoder: Encoder, value: Action) {
                encoder.encodeInt(
                    when (value) {
                        is Add -> 0
                        is Remove -> 1
                        is UpdateFlags -> 2
                        is UpdateHealth -> 3
                        is UpdateStyle -> 4
                        is UpdateTitle -> 5
                    }
                )
                when (value) {
                    is Add -> Add.serializer()
                        .serialize(encoder, value)

                    is Remove -> Remove.serializer()
                        .serialize(encoder, value)

                    is UpdateFlags -> UpdateFlags.serializer()
                        .serialize(encoder, value)

                    is UpdateHealth -> UpdateHealth.serializer()
                        .serialize(encoder, value)

                    is UpdateStyle -> UpdateStyle.serializer()
                        .serialize(encoder, value)

                    is UpdateTitle -> UpdateTitle.serializer()
                        .serialize(encoder, value)
                }
            }

        }
    }

    @Serializable
    data class Add(
        val title: Chat,
        val health: Float,
        val color: Color,
        val division: Division,
        override var flag: Byte = 0
    ) : Action, ActionFlags

    @Serializable(Color.Serializer::class)
    enum class Color(override val value: Int) : VarIntEnum {
        Pink(0), Blue(1), Red(2), Green(3), Yellow(4), Purple(5), White(6);

        companion object Serializer :
            KSerializer<Color> by varIntEnumSerializer(entries)
    }

    @Serializable(Division.Serializer::class)
    enum class Division(override val value: Int) : VarIntEnum {
        NoDivision(0), NOTCH_6(1), NOTCH_10(2), NOTCH_12(3), NOTCH_20(4);

        companion object Serializer :
            KSerializer<Division> by varIntEnumSerializer(entries)
    }

    @Serializable
    data object Remove : Action

    @Serializable
    data class UpdateHealth(val health: Float) : Action

    @Serializable
    data class UpdateTitle(val title: Chat) : Action

    @Serializable
    data class UpdateStyle(
        val color: Color,
        val division: Division
    ) :
        Action

    @Serializable
    data class UpdateFlags(override var flag: Byte) : Action,
        ActionFlags

}