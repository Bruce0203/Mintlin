package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf
import mintlin.minecraft.datastructure.Chat
import mintlin.minecraft.datastructure.Identifier
import mintlin.minecraft.datastructure.IdentifiersSerializer
import mintlin.minecraft.datastructure.Slot
import mintlin.serializer.*

@Serializable
data class UpdateAdvancements(
    val reset: Boolean,
    @Serializable(AdvancementMappingsSerializer::class)
    val advancementMappings: Array<AdvancementMapping>,
    @Serializable(IdentifiersSerializer::class)
    val identifiers: Array<String>,
    @Serializable(ProgressMappingsSerializer::class)
    val progressMappings: Array<ProgressMapping>
) {

    object AdvancementMappingsSerializer : KSerializer<Array<AdvancementMapping>> by varIntSizedArraySerializer(
        AdvancementMapping.serializer()
    )

    @Serializable
    data class AdvancementMapping(
        val key: Identifier,
        val value: Advancement
    )

    @Serializable
    data class Advancement(
        val parentId: Identifier?,
        val advancementDisplay: AdvancementDisplay?,
        @Serializable(RequirementsSerializer::class)
        val requirements: Array<Array<String>>,
        val sendsTelemetryData: Boolean
    )

    object RequirementsSerializer
        : KSerializer<Array<Array<String>>> by varIntSizedArraySerializer(
        varIntSizedArraySerializer(
            VarString32767Serializer
        )
    )

    @Serializable
    data class AdvancementDisplay(
        val title: Chat,
        val description: Chat,
        val icon: Slot,
        val frameType: FrameType,
        val showType: ShowType,
        val x: Float,
        val y: Float
    ) {

        @Serializable(ShowType.Serializer::class)
        sealed interface ShowType {
            companion object Serializer : KSerializer<ShowType> {
                override val descriptor = buildClassSerialDescriptor(classNameOf<ShowType>())

                override fun deserialize(decoder: Decoder): ShowType {
                    val flag = decoder.decodeInt()
                    return when {
                        flag has 0x01 -> BackgroundTexture.serializer().deserialize(decoder)
                        flag has 0x02 -> Toast
                        flag has 0x04 -> Hidden
                        else -> throw AssertionError("unknown advancement display show type")
                    }
                }

                private infix fun Int.has(flag: Int) = this and flag == flag

                override fun serialize(encoder: Encoder, value: ShowType) {
                    encoder.encodeInt(
                        when (value) {
                            is BackgroundTexture -> 0x01
                            is Hidden -> 0x02
                            is Toast -> 0x04
                        }
                    )
                    when (value) {
                        is BackgroundTexture -> BackgroundTexture.serializer().serialize(encoder, value)
                        is Hidden -> {}
                        is Toast -> {}
                    }
                }

            }

            @Serializable
            data class BackgroundTexture(
                val location: Identifier
            ) : ShowType

            @Serializable
            data object Toast : ShowType

            @Serializable
            data object Hidden : ShowType
        }

        @Serializable(FrameType.Serializer::class)
        enum class FrameType(override val value: Int) : VarIntEnum {
            Task(0), Challenge(1), Goal(2);

            companion object Serializer : KSerializer<FrameType> by varIntEnumSerializer(entries)
        }
    }


    object ProgressMappingsSerializer
        : KSerializer<Array<ProgressMapping>> by varIntSizedArraySerializer(ProgressMapping.serializer())

    @Serializable
    data class ProgressMapping(
        val key: Identifier,
        val value: AdvancementProgress
    )

    @Serializable
    data class AdvancementProgress(
        @Serializable(CriteriaArraySerializer::class)
        val criterias: Array<Criteria>
    ) {
        object CriteriaArraySerializer
            : KSerializer<Array<Criteria>> by varIntSizedArraySerializer(Criteria.serializer())

        @Serializable
        data class Criteria(
            val identifier: Identifier,
            val progress: CriterionProgress
        ) {
            @Serializable
            data class CriterionProgress(
                val achievingDate: Long?
            )
        }
    }
}