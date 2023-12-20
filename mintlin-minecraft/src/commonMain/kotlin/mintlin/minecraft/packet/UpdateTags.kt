package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Identifier
import mintlin.serializer.VarIntSerializer
import mintlin.lang.classNameOf
import mintlin.serializer.varIntSizedArraySerializer

@Serializable
data class UpdateTags(
    @Serializable(TagsArraySerializer::class)
    val tag: Array<Tags>
) {
    override fun toString() = "${classNameOf<UpdateTags>()}(...)"

    object TagsArraySerializer : KSerializer<Array<Tags>> by varIntSizedArraySerializer(
        Tags.serializer()
    )

    @Serializable
    data class Tags(
        val type: Identifier,
        @Serializable(TagArraySerializer::class)
        val tags: Array<Tag>
    ) {
        object TagArraySerializer : KSerializer<Array<Tag>> by varIntSizedArraySerializer(
            Tag.serializer()
        )

        @Serializable
        data class Tag(
            val name: Identifier,
            @Serializable(EntriesSerializer::class)
            val entries: Array<Int>
        ) {
            object EntriesSerializer : KSerializer<Array<Int>> by varIntSizedArraySerializer(
                VarIntSerializer
            )
        }
    }
}