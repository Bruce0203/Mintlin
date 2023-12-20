package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.minecraft.datastructure.IdentifiersSerializer
import mintlin.serializer.VarIntEnum
import mintlin.lang.classNameOf
import mintlin.serializer.varIntEnumSerializer

@Serializable(UpdateRecipeBook.Serializer::class)
data class UpdateRecipeBook(
    val action: Action,
    val craftingRecipeBookOpen: Boolean,
    val craftingRecipeBookFilterActive: Boolean,
    val smeltingRecipeBookOpen: Boolean,
    val smeltingRecipeBookFilterActive: Boolean,
    val blastFurnaceRecipeBookOpen: Boolean,
    val blastFurnaceRecipeBookFilterActive: Boolean,
    val smokerRecipeBookOpen: Boolean,
    val smokerRecipeBookFilterActive: Boolean,
    val recipeIds: Array<String>,
    val recipeIds2: Array<String>?,
) {
    companion object Serializer : KSerializer<UpdateRecipeBook> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<UpdateRecipeBook>())

        override fun deserialize(decoder: Decoder): UpdateRecipeBook {
            val action = Action.deserialize(decoder)
            return UpdateRecipeBook(
                action = action,
                craftingRecipeBookOpen = decoder.decodeBoolean(),
                craftingRecipeBookFilterActive = decoder.decodeBoolean(),
                smeltingRecipeBookOpen = decoder.decodeBoolean(),
                smeltingRecipeBookFilterActive = decoder.decodeBoolean(),
                blastFurnaceRecipeBookOpen = decoder.decodeBoolean(),
                blastFurnaceRecipeBookFilterActive = decoder.decodeBoolean(),
                smokerRecipeBookOpen = decoder.decodeBoolean(),
                smokerRecipeBookFilterActive = decoder.decodeBoolean(),
                recipeIds = IdentifiersSerializer.deserialize(decoder),
                recipeIds2 = if (action == Action.Init) IdentifiersSerializer.deserialize(decoder) else null
            )
        }

        override fun serialize(encoder: Encoder, value: UpdateRecipeBook) {
            Action.serialize(encoder, value.action)
            encoder.encodeBoolean(value.craftingRecipeBookOpen)
            encoder.encodeBoolean(value.craftingRecipeBookFilterActive)
            encoder.encodeBoolean(value.smeltingRecipeBookOpen)
            encoder.encodeBoolean(value.smeltingRecipeBookFilterActive)
            encoder.encodeBoolean(value.blastFurnaceRecipeBookOpen)
            encoder.encodeBoolean(value.blastFurnaceRecipeBookFilterActive)
            encoder.encodeBoolean(value.smokerRecipeBookOpen)
            encoder.encodeBoolean(value.smokerRecipeBookFilterActive)
            IdentifiersSerializer.serialize(encoder, value.recipeIds)
            if (value.action == Action.Init) IdentifiersSerializer.serialize(
                encoder, value.recipeIds2
                    ?: throw AssertionError("second recipe ids must not null if action mode is init")
            )
        }

    }

    @Serializable(Action.Serializer::class)
    enum class Action(override val value: Int) : VarIntEnum {
        Init(0), Add(1), Remove(2);

        companion object Serializer : KSerializer<Action> by varIntEnumSerializer(entries)
    }
}
