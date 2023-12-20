@file:Suppress("ArrayInDataClass")

package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.datastructure.FastMap
import mintlin.datastructure.toFast
import mintlin.lang.classNameOf
import mintlin.minecraft.datastructure.IdentifierSerializer
import mintlin.minecraft.datastructure.Slot
import mintlin.minecraft.datastructure.SlotsSerializer
import mintlin.serializer.*

typealias CraftingSpecialArmorDye = RecipeData.CategoryData
typealias CraftingSpecialBookCloning = RecipeData.CategoryData
typealias CraftingSpecialMapCloning = RecipeData.CategoryData
typealias CraftingSpecialMapExtending = RecipeData.CategoryData
typealias CraftingSpecialFireworkRocket = RecipeData.CategoryData
typealias CraftingSpecialFireworkStar = RecipeData.CategoryData
typealias CraftingSpecialFireworkStarFade = RecipeData.CategoryData
typealias CraftingSpecialRepairItem = RecipeData.CategoryData
typealias CraftingSpecialTippedArrow = RecipeData.CategoryData
typealias CraftingSpecialBannerDuplicate = RecipeData.CategoryData
typealias CraftingSpecialShieldDecoration = RecipeData.CategoryData
typealias CraftingSpecialShulkerBoxColoring = RecipeData.CategoryData
typealias CraftingSpecialSuspiciousTew = RecipeData.CategoryData
typealias CraftingSpecialDecoratedPot = RecipeData.CategoryData
typealias Smelting = RecipeData.FurnaceData
typealias Blasting = RecipeData.FurnaceData
typealias Smoking = RecipeData.FurnaceData
typealias CampfireCooking = RecipeData.FurnaceData

@Serializable
data class UpdateRecipes(
    @Serializable(RecipesSerializer::class)
    val recipes: Array<Recipe>
) {
    override fun toString() = "${classNameOf<UpdateRecipes>()}(...)"

    object RecipesSerializer : KSerializer<Array<Recipe>> by varIntSizedArraySerializer(Recipe.serializer())

    @Serializable(Recipe.Serializer::class)
    data class Recipe(
        val type: String,
        val id: String,
        val data: RecipeData
    ) {
        companion object Serializer : KSerializer<Recipe> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<Recipe>())

            @Suppress("unchecked_cast")
            private val typeToSerializer = mapOf(
                "minecraft:crafting_shapeless" to RecipeData.CraftingShapeless.serializer(),
                "minecraft:crafting_shaped" to RecipeData.CraftingShaped.serializer(),
                "minecraft:crafting_special_armordye" to CraftingSpecialArmorDye.serializer(),
                "minecraft:crafting_special_bookcloning" to CraftingSpecialBookCloning.serializer(),
                "minecraft:crafting_special_mapcloning" to CraftingSpecialMapCloning.serializer(),
                "minecraft:crafting_special_mapextending" to CraftingSpecialMapExtending.serializer(),
                "minecraft:crafting_special_firework_rocket" to CraftingSpecialFireworkRocket.serializer(),
                "minecraft:crafting_special_firework_star" to CraftingSpecialFireworkStar.serializer(),
                "minecraft:crafting_special_firework_star_fade" to CraftingSpecialFireworkStarFade.serializer(),
                "minecraft:crafting_special_repairitem" to CraftingSpecialRepairItem.serializer(),
                "minecraft:crafting_special_tippedarrow" to CraftingSpecialTippedArrow.serializer(),
                "minecraft:crafting_special_bannerduplicate" to CraftingSpecialBannerDuplicate.serializer(),
                "minecraft:crafting_special_shielddecoration" to CraftingSpecialShieldDecoration.serializer(),
                "minecraft:crafting_special_shulkerboxcoloring" to CraftingSpecialShulkerBoxColoring.serializer(),
                "minecraft:crafting_special_suspiciousstew" to CraftingSpecialSuspiciousTew.serializer(),
                "minecraft:crafting_decorated_pot" to CraftingSpecialDecoratedPot.serializer(),
                "minecraft:smelting" to Smelting.serializer(),
                "minecraft:blasting" to Blasting.serializer(),
                "minecraft:smoking" to Smoking.serializer(),
                "minecraft:campfire_cooking" to CampfireCooking.serializer(),
                "minecraft:stonecutting" to RecipeData.StoneCutting.serializer(),
                "minecraft:smithing_transform" to RecipeData.SmithingTransform.serializer(),
                "minecraft:smithing_trim" to RecipeData.SmithingTrim.serializer(),
            ).toFast() as FastMap<String, KSerializer<RecipeData>>

            override fun deserialize(decoder: Decoder): Recipe {
                val type = IdentifierSerializer.deserialize(decoder)
                val id = IdentifierSerializer.deserialize(decoder)
                val serializer = typeToSerializer[type]
                    ?: throw AssertionError("invalid recipe type $type")
                val data = serializer.deserialize(decoder)
                return Recipe(type = type, id = id, data = data)
            }

            override fun serialize(encoder: Encoder, value: Recipe) {
                IdentifierSerializer.serialize(encoder, value.type)
                IdentifierSerializer.serialize(encoder, value.id)
                val serializer = typeToSerializer[value.type]
                    ?: throw AssertionError("invalid recipe type ${value.type}")
                serializer.serialize(encoder, value.data)
            }
        }
    }
}

@Serializable
sealed interface RecipeData {
    @Serializable
    data class CraftingShapeless(
        @Serializable(VarString32767Serializer::class)
        val group: String,
        val category: Category,
        @Serializable(IngredientsSerializer::class)
        val ingredients: Array<Ingredient>,
        val result: Slot,
    ) : RecipeData

    @Serializable(CraftingShaped.Serializer::class)
    data class CraftingShaped(
        val width: VarInt,
        val height: VarInt,
        @Serializable(VarString32767Serializer::class)
        val group: String,
        @Serializable(Category.Serializer::class)
        val category: Category,
        @Serializable(IngredientsSerializer::class)
        val ingredients: Array<Ingredient>,
        val result: Slot,
        val showNotification: Boolean
    ) : RecipeData {
        companion object Serializer : KSerializer<CraftingShaped> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<CraftingShaped>())

            override fun deserialize(decoder: Decoder): CraftingShaped {
                val width = VarIntSerializer.deserialize(decoder)
                val height = VarIntSerializer.deserialize(decoder)
                val group = VarString32767Serializer.deserialize(decoder)
                val category = Category.deserialize(decoder)
                val ingredients = Array(width * height) { Ingredient.serializer().deserialize(decoder) }
                val result = Slot.serializer().deserialize(decoder)
                val showNotification = decoder.decodeBoolean()
                return CraftingShaped(
                    width = width, height = height,
                    group = group, category = category, ingredients = ingredients,
                    result = result, showNotification = showNotification
                )
            }

            override fun serialize(encoder: Encoder, value: CraftingShaped) {
                VarIntSerializer.serialize(encoder, value.width)
                VarIntSerializer.serialize(encoder, value.height)
                VarString32767Serializer.serialize(encoder, value.group)
                Category.serialize(encoder, value.category)
                value.ingredients.forEach { Ingredient.serializer().serialize(encoder, it) }
                Slot.serializer().serialize(encoder, value.result)
                encoder.encodeBoolean(value.showNotification)
            }
        }
    }

    @Serializable
    data class CategoryData(
        @Serializable(Category.Serializer::class)
        val category: Category
    ) : RecipeData

    @Serializable
    data class FurnaceData(
        @Serializable(VarString32767Serializer::class)
        val group: String,
        @Serializable(FurnaceCategory.Serializer::class)
        val category: FurnaceCategory,
        val ingredient: Ingredient,
        val result: Slot,
        val experience: Float,
        val cookingTime: VarInt
    ) : RecipeData

    @Serializable(FurnaceCategory.Serializer::class)
    enum class FurnaceCategory(override val value: Int) : VarIntEnum {
        Food(0), Blocks(1), Misc(2);

        companion object Serializer : KSerializer<FurnaceCategory> by varIntEnumSerializer(entries)
    }

    @Serializable
    data class StoneCutting(
        @Serializable(VarString32767Serializer::class)
        val group: String,
        val ingredient: Ingredient,
        val result: Slot
    ) : RecipeData

    @Serializable
    data class SmithingTransform(
        val template: Ingredient,
        val base: Ingredient,
        val addition: Ingredient,
        val result: Slot
    ) : RecipeData

    @Serializable
    class SmithingTrim(
        val template: Ingredient,
        val base: Ingredient,
        val addition: Ingredient
    ) : RecipeData

    @Serializable(Category.Serializer::class)
    enum class Category(override val value: Int) : VarIntEnum {
        Building(0), RedStone(1), Equipment(2), Misc(3);

        companion object Serializer : KSerializer<Category> by varIntEnumSerializer(entries)
    }

    @Serializable
    data class Ingredient(
        @Serializable(SlotsSerializer::class)
        val slots: Array<Slot>
    )

    object IngredientsSerializer : KSerializer<Array<Ingredient>> by varIntSizedArraySerializer(
        Ingredient.serializer()
    )
}
