@file:OptIn(ExperimentalSerializationApi::class)

package mintlin.minecraft.packet

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import mintlin.format.nbt.NbtCompoundDecoder
import mintlin.format.nbt.NbtListDecoder
import mintlin.format.nbt.nbtSerializer
import mintlin.minecraft.datastructure.Chat
import mintlin.minecraft.datastructure.Style
import mintlin.lang.classNameOf

@Serializable
data class RegistryData(
    @Serializable(RegistryCodecSerializer::class)
    val registryCodec: RegistryCodec
) {
    override fun toString() = "${classNameOf<RegistryData>()}(...)"
}

data object RegistryCodecSerializer : KSerializer<RegistryCodec> by nbtSerializer(RegistryCodec.serializer())

@Serializable
data class RegistryCodec(
    @SerialName("minecraft:chat_type")
    val chatTypeRegistry: ChatTypeRegistry,
    @SerialName("minecraft:trim_pattern")
    val trimPatternRegistry: TrimPatternRegistry,
    @SerialName("minecraft:trim_material")
    val trimMaterialRegistry: TrimMaterialRegistry,
    @SerialName("minecraft:damage_type")
    val damageTypeRegistry: DamageTypeRegistry,
    @SerialName("minecraft:dimension_type")
    val dimensionTypeRegistry: DimensionTypeRegistry,
    @SerialName("minecraft:worldgen/biome")
    val biomeRegistry: BiomeRegistry
)

typealias DimensionTypeRegistry = @Serializable(DimensionTypeRegistrySerializer::class) Registry<DimensionType>

data object DimensionTypeRegistrySerializer :
    KSerializer<Registry<DimensionType>> by registrySerializer(DimensionType.serializer())

typealias DamageTypeRegistry = @Serializable(DamageTypeRegistrySerializer::class) Registry<DamageType>

data object DamageTypeRegistrySerializer :
    KSerializer<Registry<DamageType>> by registrySerializer(DamageType.serializer())

typealias TrimMaterialRegistry = @Serializable(TrimMaterialRegistrySerializer::class) Registry<TrimMaterial>

data object TrimMaterialRegistrySerializer :
    KSerializer<Registry<TrimMaterial>> by registrySerializer(TrimMaterial.serializer())

typealias TrimPatternRegistry = @Serializable(TrimPatternRegistrySerializer::class) Registry<TrimPattern>

data object TrimPatternRegistrySerializer :
    KSerializer<Registry<TrimPattern>> by registrySerializer(TrimPattern.serializer())

typealias ChatTypeRegistry = @Serializable(ChatTypeRegistrySerializer::class) Registry<ChatType>

data object ChatTypeRegistrySerializer : KSerializer<Registry<ChatType>> by registrySerializer(ChatType.serializer())

typealias BiomeRegistry = @Serializable(BiomeRegistrySerializer::class) Registry<Biome>

data object BiomeRegistrySerializer : KSerializer<Registry<Biome>> by registrySerializer(Biome.serializer())

inline fun <reified E : Any> registryOf(type: String, vararg value: Entry<E>) = Registry(type = type, value = value)

fun <E> entryOf(name: String, id: Int, element: E) = Entry(name = name, id = id, element = element)

data class Registry<E>(
    val type: String,
    val value: Array<out Entry<E>>
)

data class Entry<E>(
    val name: String,
    val id: Int,
    val element: E
)

inline fun <reified T : Any> registrySerializer(serializer: KSerializer<T>) = object : KSerializer<Registry<T>> {
    val entrySerializer = entrySerializer(serializer)

    @Suppress("unchecked_cast")
    private val arraySerializer = ArraySerializer(entrySerializer) as KSerializer<Array<out Entry<T>>>

    override val descriptor =
        buildClassSerialDescriptor("${serializer.descriptor.serialName}${classNameOf<Entry<T>>()}") {
            element<String>("type")
            element("value", arraySerializer.descriptor)
        }

    override fun deserialize(decoder: Decoder): Registry<T> {
        return decoder.decodeStructure(descriptor) {
            decodeElementIndex(descriptor)
            Registry(
                type = decodeSerializableElement(descriptor, 0, String.serializer())
                    .also { decodeElementIndex(descriptor) },
                value = run {
                    val list = ArrayList<Entry<T>>()
                    (this as Decoder).decodeStructure(arraySerializer.descriptor) {
                        while (true) {
                            val index = decodeElementIndex(arraySerializer.descriptor)
                            if (index == -1) break
                            list.add(decodeSerializableElement(arraySerializer.descriptor, index, entrySerializer))
                        }
                    }
                    list.toTypedArray()
                }
            )
        }
    }

    override fun serialize(encoder: Encoder, value: Registry<T>) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.type)
            encodeSerializableElement(descriptor, 1, arraySerializer, value.value)
        }
    }
}

@ExperimentalSerializationApi
inline fun <reified T> entrySerializer(serializer: KSerializer<T>) = object : KSerializer<Entry<T>> {
    override val descriptor =
        buildClassSerialDescriptor("${serializer.descriptor.serialName}${classNameOf<Entry<T>>()}") {
            element<String>("name")
            element<Int>("id")
            element("element", serializer.descriptor)
        }

    override fun deserialize(decoder: Decoder): Entry<T> {
        return decoder.decodeStructure(descriptor) {
            decodeElementIndex(descriptor)
            Entry(
                name = decodeStringElement(descriptor, 0)
                    .also { decodeElementIndex(descriptor) },
                id = decodeIntElement(descriptor, 1)
                    .also { decodeElementIndex(descriptor) },
                element = decodeSerializableElement(descriptor, 2, serializer)
            )
        }
    }

    override fun serialize(encoder: Encoder, value: Entry<T>) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeIntElement(descriptor, 1, value.id)
            encodeSerializableElement(descriptor, 2, serializer, value.element)
        }
    }
}

@Serializable
data class DimensionType(
    @SerialName("fixed_time")
    val fixedTime: Long? = null,
    @SerialName("has_skylight")
    val hasSkyLight: Boolean,
    @SerialName("has_ceiling")
    val hasCeiling: Boolean,
    @SerialName("ultrawarm")
    val ultraWarm: Boolean,
    val natural: Boolean,
    @SerialName("coordinate_scale")
    val coordinateScale: Double,
    @SerialName("bed_works")
    val bedWorks: Boolean,
    @SerialName("respawn_anchor_works")
    val respawnAnchorWorks: Boolean,
    @SerialName("min_y")
    val minY: Int,
    val height: Int,
    @SerialName("logical_height")
    val logicalHeight: Int,
    @SerialName("infiniburn")
    val infinityBurning: String,
    val effects: String,
    @SerialName("ambient_light")
    val ambientLight: Float,
    @SerialName("piglin_safe")
    val piglinSafe: Boolean,
    @SerialName("has_raids")
    val hasRaids: Boolean,
    @SerialName("monster_spawn_light_level")
    val monsterSpawnLightLevel: MonsterSpawnLightLevel,
    @SerialName("monster_spawn_block_light_limit")
    val monsterSpawnBlockLightLevel: Int
)

@Serializable
data class ChatType(
    val chat: Decoration,
    val narration: Decoration
)

@Serializable
data class Decoration(
    @SerialName("translation_key")
    val translationKey: String,
    val parameters: Array<String>,
    @Serializable(Style.Serializer::class)
    val style: Style? = null
)

@Serializable
data class Biome(
    @SerialName("has_precipitation")
    val hasPrecipitation: Boolean,
    val temperature: Float,
    @SerialName("temperature_modifier")
    val temperatureModifier: String? = null,
    val downfall: Float,
    val effects: Effects
) {
    @Serializable
    data class Effects(
        @SerialName("fog_color")
        val fogColor: Int,
        @SerialName("water_color")
        val waterColor: Int,
        @SerialName("water_fog_color")
        val waterFogColor: Int,
        @SerialName("sky_color")
        val skyColor: Int,
        @SerialName("foliage_color")
        val foliageColor: Int? = null,
        @SerialName("grass_color")
        val grassColor: Int? = null,
        @SerialName("grass_color_modifier")
        val grassColorModifier: String? = null,
        val particle: Particle? = null,
        @SerialName("ambient_sound")
        val ambientSound: AmbientSound? = null,
        @SerialName("mood_sound")
        val moodSound: MoodSound? = null,
        @SerialName("additions_sound")
        val additionsSound: AdditionsSound? = null,
        val music: Music? = null
    ) {
        @Serializable
        data class MoodSound(
            val sound: String,
            @SerialName("tick_delay")
            val tickDelay: Int,
            @SerialName("block_search_extent")
            val blockSearchExtent: Int,
            val offset: Double
        )

        @Serializable
        data class AdditionsSound(
            val sound: String,
            @SerialName("tick_chance")
            val tickChance: Double
        )

        @Serializable
        data class Music(
            val sound: String,
            @SerialName("min_delay")
            val minDelay: Int,
            @SerialName("max_delay")
            val maxDelay: Int,
            @SerialName("replace_current_music")
            val replaceCurrentMusic: Boolean
        )

        @Serializable
        data class Particle(
            val options: ParticleOptions,
            val probability: Float,
        )

        @Serializable
        data class ParticleOptions(
            val type: String
            //TODO : Varies
        )
    }
}

@Serializable
data class TrimPattern(
    @SerialName("asset_id")
    val assetId: String,
    @SerialName("template_item")
    val templateItem: String,
    @Serializable(Chat.Serializer::class)
    val description: Chat,
    val decal: Boolean
)

@Serializable
data class TrimMaterial(
    @SerialName("asset_name")
    val assetName: String,
    val ingredient: String,
    @SerialName("item_model_index")
    val itemModelIndex: Float,
    @SerialName("override_armor_materials")
    val overrideArmorMaterials: OverrideArmorMaterials? = null,
    @Serializable(Chat.Serializer::class)
    val description: Chat,
) {

    @Serializable(OverrideArmorMaterials.Serializer::class)
    data class OverrideArmorMaterials(val key: String, val value: String) {
        companion object Serializer : KSerializer<OverrideArmorMaterials> {
            override val descriptor = buildClassSerialDescriptor(classNameOf<OverrideArmorMaterials>()) {
                element<String>("leather")
                element<String>("chainmail")
                element<String>("iron")
                element<String>("gold")
                element<String>("diamond")
                element<String>("turtle")
                element<String>("netherite")
            }

            override fun deserialize(decoder: Decoder): OverrideArmorMaterials {
                return decoder.decodeStructure(descriptor) {
                    val index = decodeElementIndex(descriptor)
                    OverrideArmorMaterials(
                        key = descriptor.getElementName(index),
                        value = decodeStringElement(descriptor, index)
                    )
                }
            }

            override fun serialize(encoder: Encoder, value: OverrideArmorMaterials) {
                encoder.encodeStructure(descriptor) {
                    encodeStringElement(descriptor, descriptor.getElementIndex(value.key), value.value)
                }
            }

        }
    }
}

@Serializable
data class DamageType(
    @SerialName("message_id")
    val messageId: String,
    val scaling: String,
    val exhaustion: Float,
    val effects: String? = null,
    @SerialName("death_message_type")
    val deathMessageType: String? = null
)

@Serializable(MonsterSpawnLightLevelSerializer::class)
sealed interface MonsterSpawnLightLevel {
    @Serializable
    data class Compounded(
        val type: String,
        val value: Value
    ) : MonsterSpawnLightLevel {
        @Serializable
        data class Value(
            @SerialName("min_inclusive")
            val minInclusive: Int,
            @SerialName("max_inclusive")
            val maxInclusive: Int
        )
    }

    @Serializable
    data class Integral(val value: Int) : MonsterSpawnLightLevel
}

data object MonsterSpawnLightLevelSerializer : KSerializer<MonsterSpawnLightLevel> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<MonsterSpawnLightLevel>())

    override fun deserialize(decoder: Decoder): MonsterSpawnLightLevel {
        val polymorphicDecoder = decoder as mintlin.format.PolymorphicDecoder
        return when (polymorphicDecoder.decodeValue()) {
            is Int -> MonsterSpawnLightLevel.Integral(polymorphicDecoder.decodeInt())
            else -> decoder.decodeSerializableValue(MonsterSpawnLightLevel.Compounded.serializer())
        }
    }

    override fun serialize(encoder: Encoder, value: MonsterSpawnLightLevel) {
        when (value) {
            is MonsterSpawnLightLevel.Compounded ->
                MonsterSpawnLightLevel.Compounded.serializer().serialize(encoder, value)

            is MonsterSpawnLightLevel.Integral -> encoder.encodeInt(value.value)
        }
    }
}

@Serializable(AmbientSoundSerializer::class)
sealed interface AmbientSound {
    val soundId: String

    @Serializable
    data class Compounded(
        @SerialName("sound_id")
        override val soundId: String,
        val range: Float? = null,
    ) : AmbientSound

    @Serializable
    data class SingleValued(
        @SerialName("sound_id")
        override val soundId: String
    ) : AmbientSound
}

data object AmbientSoundSerializer : KSerializer<AmbientSound> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<AmbientSound>())

    override fun deserialize(decoder: Decoder): AmbientSound {
        decoder as mintlin.format.NamedTagDecoder
        decoder as mintlin.format.PolymorphicDecoder
        return when (decoder.decodeValue()) {
            is String -> AmbientSound.SingleValued(decoder.decodeString())
            else -> decoder.decodeSerializableValue(AmbientSound.Compounded.serializer())
        }
    }

    override fun serialize(encoder: Encoder, value: AmbientSound) {
        when (value) {
            is AmbientSound.SingleValued -> encoder.encodeString(value.soundId)
            is AmbientSound.Compounded -> AmbientSound.Compounded.serializer().serialize(encoder, value)
        }
    }
}