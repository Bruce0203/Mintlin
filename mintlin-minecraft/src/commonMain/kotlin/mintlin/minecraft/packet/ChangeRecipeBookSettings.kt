package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.VarIntEnum
import mintlin.serializer.varIntEnumSerializer

@Serializable
data class ChangeRecipeBookSettings(
    val bookId: BookID,
    val bookOpen: Boolean,
    val filterActive: Boolean
)

@Serializable(BookID.Serializer::class)
enum class BookID(override val value: Int) : VarIntEnum {
    Crafting(0), Furnace(1), BlastFurnace(2), Smoker(4);

    companion object Serializer : KSerializer<BookID> by varIntEnumSerializer(entries)
}