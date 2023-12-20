package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import mintlin.datastructure.toFast
import mintlin.format.packet.PacketDecoder
import mintlin.lang.classNameOf
import mintlin.minecraft.datastructure.*
import mintlin.serializer.*

private typealias SetFlag = (Int) -> Unit

@Serializable
data class Commands(
    @Serializable(NodeArraySerializer::class)
    val nodes: Array<Node>,
    val rootIndex: VarInt
) {
    override fun toString() = "${classNameOf<Commands>()}(...)"

    object NodeArraySerializer :
        KSerializer<Array<Node>> by varIntSizedArraySerializer(Node.serializer())

    @Serializable(NodeSerializer::class)
    sealed interface Node {
        val children: Array<Int>
        val iSExecutable: Boolean

        data class Root(
            override val iSExecutable: Boolean,
            override val children: Array<Int>,
            override val redirect: Int,
            override val suggestion: String?,
        ) : Node, Suggestion,
            Redirection

        sealed interface Suggestion {
            val suggestion: String?
            val hasSuggestion: Boolean get() = suggestion !== null
        }

        sealed interface Named {
            val name: String
        }

        sealed interface Redirection {
            val redirect: Int
            val hasRedirection: Boolean get() = redirect != -1
        }

        sealed interface Child : Named,
            Redirection,
            Suggestion

        data class Literal(
            override val iSExecutable: Boolean,
            override val children: Array<Int>,
            override val redirect: Int,
            override val name: String,
            override val suggestion: String?,
        ) : Node, Child


        data class Argument(
            override val iSExecutable: Boolean,
            override val children: Array<Int>,
            override val redirect: Int,
            override val name: kotlin.String,
            val property: Property,
            override val suggestion: kotlin.String?,
        ) : Node, Child {

            @Serializable
            sealed interface Property {
                fun applyFlag(flags: SetFlag) = Unit

                companion object Serializer : KSerializer<Property> {
                    override val descriptor = buildClassSerialDescriptor(classNameOf<Property>())

                    override fun deserialize(decoder: Decoder): Property {
                        val parserId = VarIntSerializer.deserialize(decoder)
                        val serializer = parserIdToSerializer[parserId]
                            ?: throw AssertionError("unknown property parser id $parserId")
                        return serializer.deserialize(decoder)
                    }

                    override fun serialize(encoder: Encoder, value: Property) {
                        val id = hashCodeToId[value::class.hashCode()]
                            ?: throw AssertionError("unknown command property class ${value::class.hashCode()}")
                        val serializer = hashCodeToSerializer[value::class.hashCode()]
                            ?: throw AssertionError("unknown command property class ${value::class.hashCode()}")
                        VarIntSerializer.serialize(encoder, id)
                        serializer.serialize(encoder, value)
                    }

                }
            }

            class PropertyDecoder(val flags: Int, decoder: PacketDecoder) : PacketDecoder by decoder

            companion object {
                @Suppress("unchecked_cast")
                private val hashCodeToSerializer = listOf(
                    hashCodeToSerializer<Bool>(),
                    hashCodeToSerializer<FloatProperty>(),
                    hashCodeToSerializer<DoubleProperty>(),
                    hashCodeToSerializer<IntegerProperty>(),
                    hashCodeToSerializer<LongProperty>(),
                    hashCodeToSerializer<String>(),
                    hashCodeToSerializer<EntityProperty>(),
                    hashCodeToSerializer<GameProfile>(),
                    hashCodeToSerializer<BlockPos>(),
                    hashCodeToSerializer<ColumnPos>(),
                    hashCodeToSerializer<Vec3>(),
                    hashCodeToSerializer<Vec2>(),
                    hashCodeToSerializer<BlockState>(),
                    hashCodeToSerializer<BlockPredicate>(),
                    hashCodeToSerializer<ItemStack>(),
                    hashCodeToSerializer<ItemPredicate>(),
                    hashCodeToSerializer<Color>(),
                    hashCodeToSerializer<Component>(),
                    hashCodeToSerializer<Message>(),
                    hashCodeToSerializer<Nbt>(),
                    hashCodeToSerializer<NbtTag>(),
                    hashCodeToSerializer<NbtPath>(),
                    hashCodeToSerializer<Objective>(),
                    hashCodeToSerializer<ObjectiveCriteria>(),
                    hashCodeToSerializer<Operation>(),
                    hashCodeToSerializer<Particle>(),
                    hashCodeToSerializer<Angle>(),
                    hashCodeToSerializer<Rotation>(),
                    hashCodeToSerializer<ScoreboardSlot>(),
                    hashCodeToSerializer<ScoreHolder>(),
                    hashCodeToSerializer<Swizzle>(),
                    hashCodeToSerializer<Team>(),
                    hashCodeToSerializer<ItemSlot>(),
                    hashCodeToSerializer<ResourceLocation>(),
                    hashCodeToSerializer<Function>(),
                    hashCodeToSerializer<EntityAnchor>(),
                    hashCodeToSerializer<IntRange>(),
                    hashCodeToSerializer<FloatRange>(),
                    hashCodeToSerializer<Dimension>(),
                    hashCodeToSerializer<GameMode>(),
                    hashCodeToSerializer<Time>(),
                    hashCodeToSerializer<ResourceOrTag>(),
                    hashCodeToSerializer<ResourceOrTagKey>(),
                    hashCodeToSerializer<Resource>(),
                    hashCodeToSerializer<ResourceKey>(),
                    hashCodeToSerializer<TemplateMirror>(),
                    hashCodeToSerializer<TemplateRotation>(),
                    hashCodeToSerializer<Heightmap>(),
                    hashCodeToSerializer<UUID>()
                ).toMap() as Map<Int, KSerializer<Property>>

                private inline fun <reified T> hashCodeToSerializer() = T::class.hashCode() to serializer<T>()

                private val parserIdToSerializer = hashCodeToSerializer.run {
                    (0..<size).zip(values).toFast()
                }

                private val hashCodeToId = hashCodeToSerializer.run {
                    keys.zip(0..<size).toFast()
                }
            }

            @Serializable
            data object Bool : Property

            @Serializable(FloatProperty.Serializer::class)
            data class FloatProperty(val range: ClosedFloatingPointRange<Float>) :
                Property {
                companion object Serializer : KSerializer<FloatProperty> {
                    override val descriptor = buildClassSerialDescriptor(classNameOf<FloatProperty>())

                    override fun deserialize(decoder: Decoder): FloatProperty {
                        val flags = decoder.decodeByte().toInt()
                        return FloatProperty(
                            (if (flags has 0x01) decoder.decodeFloat() else -Float.MAX_VALUE)..
                                    (if (flags has 0x02) decoder.decodeFloat() else Float.MAX_VALUE)
                        )
                    }

                    override fun serialize(encoder: Encoder, value: FloatProperty) {
                        var flags = 0
                        value.applyFlag { flags = flags or it }
                        encoder.encodeByte(flags.toByte())
                        val start = value.range.start
                        val end = value.range.endInclusive
                        if (start != -Float.MAX_VALUE) encoder.encodeFloat(start)
                        if (end != Float.MAX_VALUE) encoder.encodeFloat(end)
                    }
                }

                override fun applyFlag(flags: SetFlag) {
                    val start = range.start
                    val end = range.endInclusive
                    if (start != -Float.MAX_VALUE) flags(1)
                    if (end != Float.MAX_VALUE) flags(2)
                }
            }

            @Serializable(DoubleProperty.Serializer::class)
            data class DoubleProperty(val range: ClosedFloatingPointRange<Double>) :
                Property {
                companion object Serializer : KSerializer<DoubleProperty> {
                    override val descriptor = buildClassSerialDescriptor(classNameOf<DoubleProperty>())

                    override fun deserialize(decoder: Decoder): DoubleProperty {
                        val flags = decoder.decodeByte().toInt()
                        return DoubleProperty(
                            (if (flags has 0x01) decoder.decodeDouble() else -Double.MAX_VALUE)..
                                    (if (flags has 0x02) decoder.decodeDouble() else Double.MAX_VALUE)
                        )
                    }

                    override fun serialize(encoder: Encoder, value: DoubleProperty) {
                        var flags = 0
                        value.applyFlag { flags = flags or it }
                        encoder.encodeByte(flags.toByte())
                        val start = value.range.start
                        val end = value.range.endInclusive
                        if (start != -Double.MAX_VALUE) encoder.encodeDouble(start)
                        if (end != Double.MAX_VALUE) encoder.encodeDouble(end)
                    }
                }

                override fun applyFlag(flags: SetFlag) {
                    val start = range.start
                    val end = range.endInclusive
                    if (start != Double.MIN_VALUE) flags(1)
                    if (end != Double.MAX_VALUE) flags(2)
                }
            }

            @Serializable(IntegerProperty.Serializer::class)
            data class IntegerProperty(val range: ClosedRange<Int>) :
                Property {
                companion object Serializer : KSerializer<IntegerProperty> {
                    override val descriptor = buildClassSerialDescriptor(classNameOf<IntegerProperty>())

                    override fun deserialize(decoder: Decoder): IntegerProperty {
                        val flags = decoder.decodeByte().toInt()
                        return IntegerProperty(
                            (if (flags has 0x01) decoder.decodeInt() else Int.MIN_VALUE)..
                                    (if (flags has 0x02) decoder.decodeInt() else Int.MAX_VALUE)
                        )
                    }

                    override fun serialize(encoder: Encoder, value: IntegerProperty) {
                        var flags = 0
                        value.applyFlag { flags = flags or it }
                        encoder.encodeByte(flags.toByte())
                        val start = value.range.start
                        val end = value.range.endInclusive
                        if (start != Int.MIN_VALUE) encoder.encodeInt(start)
                        if (end != Int.MAX_VALUE) encoder.encodeInt(end)
                    }
                }

                override fun applyFlag(flags: SetFlag) {
                    val start = range.start
                    val end = range.endInclusive
                    if (start != Int.MIN_VALUE) flags(1)
                    if (end != Int.MAX_VALUE) flags(2)
                }
            }

            @Serializable(LongProperty.Serializer::class)
            data class LongProperty(val range: ClosedRange<Long>) :
                Property {
                companion object Serializer : KSerializer<LongProperty> {
                    override val descriptor = buildClassSerialDescriptor(classNameOf<LongProperty>())

                    override fun deserialize(decoder: Decoder): LongProperty {
                        val flags = decoder.decodeByte().toInt()
                        return LongProperty(
                            (if (flags has 0x01) decoder.decodeLong() else Long.MIN_VALUE)..
                                    (if (flags has 0x02) decoder.decodeLong() else Long.MAX_VALUE)
                        )
                    }

                    override fun serialize(encoder: Encoder, value: LongProperty) {
                        var flags = 0
                        value.applyFlag { flags = flags or it }
                        encoder.encodeByte(flags.toByte())
                        val start = value.range.start
                        val end = value.range.endInclusive
                        if (start != Long.MIN_VALUE) encoder.encodeLong(start)
                        if (end != Long.MAX_VALUE) encoder.encodeLong(end)
                    }
                }

                override fun applyFlag(flags: SetFlag) {
                    val start = range.start
                    val end = range.endInclusive
                    if (start != Long.MIN_VALUE) flags(1)
                    if (end != Long.MAX_VALUE) flags(2)
                }
            }

            @Serializable(String.Serializer::class)
            enum class String(override val value: Int) : VarIntEnum,
                Property {
                SINGLE_WORD(0),
                QUOTABLE_PHRASE(1),
                GREEDY_PHRASE(2);

                companion object Serializer :
                    KSerializer<String> by varIntEnumSerializer(
                        entries
                    )
            }

            @Serializable(EntityProperty.Serializer::class)
            enum class EntityProperty : Property {
                SINGLE_ENTITY_OR_PLAYER_ONLY, MULTIPLE_PLAYERS_ONLY, ENTITY;

                companion object Serializer : KSerializer<EntityProperty> {
                    override val descriptor = buildClassSerialDescriptor(classNameOf<EntityProperty>())

                    override fun deserialize(decoder: Decoder): EntityProperty {
                        val flags = decoder.decodeByte().toInt()
                        return when {
                            flags has 0x01 -> SINGLE_ENTITY_OR_PLAYER_ONLY
                            flags has 0x02 -> MULTIPLE_PLAYERS_ONLY
                            else -> ENTITY
                        }
                    }

                    override fun serialize(encoder: Encoder, value: EntityProperty) {
                        encoder.encodeByte(
                            when (value) {
                                SINGLE_ENTITY_OR_PLAYER_ONLY -> 1
                                MULTIPLE_PLAYERS_ONLY -> 2
                                ENTITY -> return
                            }
                        )
                    }
                }
            }

            @Serializable
            data object GameProfile : Property

            @Serializable
            data object BlockPos : Property

            @Serializable
            data object ColumnPos : Property

            @Serializable
            data object Vec3 : Property

            @Serializable
            data object Vec2 : Property

            @Serializable
            data object BlockState : Property

            @Serializable
            data object BlockPredicate : Property

            @Serializable
            data object ItemStack : Property

            @Serializable
            data object ItemPredicate : Property

            @Serializable
            data object Color : Property

            @Serializable
            data object Component : Property

            @Serializable
            data object Message : Property

            @Serializable
            data object Nbt : Property

            @Serializable
            data object NbtTag : Property

            @Serializable
            data object NbtPath : Property

            @Serializable
            data object Objective : Property

            @Serializable
            data object ObjectiveCriteria : Property

            @Serializable
            data object Operation : Property

            @Serializable
            data object Particle : Property

            @Serializable
            data object Angle : Property

            @Serializable
            data object Rotation : Property

            @Serializable
            data object ScoreboardSlot : Property

            @Serializable(ScoreHolder.Serializer::class)
            data class ScoreHolder(val isMultiAllowed: Boolean) :
                Property {
                companion object Serializer : KSerializer<ScoreHolder> {
                    override val descriptor = buildClassSerialDescriptor(classNameOf<ScoreHolder>())

                    override fun deserialize(decoder: Decoder): ScoreHolder {
                        val flags = decoder.decodeByte().toInt()
                        return ScoreHolder(flags has 0x01)
                    }

                    override fun serialize(encoder: Encoder, value: ScoreHolder) {
                        var byte = 0
                        if (value.isMultiAllowed) {
                            byte = byte or 0x01
                        }
                        encoder.encodeByte(byte.toByte())
                    }

                }
            }

            @Serializable
            data object Swizzle : Property

            @Serializable
            data object Team : Property

            @Serializable
            data object ItemSlot : Property

            @Serializable
            data object ResourceLocation : Property

            @Serializable
            data object Function : Property

            @Serializable
            data object EntityAnchor : Property

            @Serializable
            data object IntRange : Property

            @Serializable
            data object FloatRange : Property

            @Serializable
            data object Dimension : Property

            @Serializable
            data object GameMode : Property

            @Serializable
            data class Time(val min: Int) : Property

            @Serializable
            data class ResourceOrTag(
                val registry: Identifier
            ) : Property

            @Serializable
            data class ResourceOrTagKey(
                val registry: Identifier
            ) : Property

            @Serializable
            data class Resource(
                val registry: Identifier
            ) : Property

            @Serializable
            data class ResourceKey(
                val registry: Identifier
            ) : Property

            @Serializable
            data object TemplateMirror : Property

            @Serializable
            data object TemplateRotation : Property

            @Serializable
            data object Heightmap : Property

            @Serializable
            data object UUID : Property
        }
    }

    object NodeSerializer : KSerializer<Node> {
        private const val ROOT = 0x00
        private const val LITERAL = 0x01
        private const val ARGUMENT = 0x02
        private const val NODE_TYPE = 0x03
        private const val EXECUTABLE = 0x04
        private const val REDIRECT = 0x08
        private const val SUGGESTION = 0x10

        override val descriptor = buildClassSerialDescriptor(classNameOf<NodeSerializer>())

        private data object VarIntSizedVarIntArraySerializer :
            KSerializer<Array<Int>> by varIntSizedArraySerializer(VarIntSerializer)

        override fun deserialize(decoder: Decoder): Node {
            val flag = decoder.decodeByte().toInt()
            val children = VarIntSizedVarIntArraySerializer.deserialize(decoder)
            fun suggestion() =
                if (flag has SUGGESTION) IdentifierSerializer.deserialize(decoder) else null

            val redirect =
                if (flag has REDIRECT) VarIntSerializer.deserialize(decoder) else -1
            val isExecutable = flag has EXECUTABLE
            if (flag and NODE_TYPE == ROOT) return Node.Root(
                isExecutable,
                children,
                redirect,
                suggestion()
            )
            val name = VarString32767Serializer.deserialize(decoder)
            if (flag and NODE_TYPE == LITERAL)
                return Node.Literal(
                    isExecutable,
                    children,
                    redirect,
                    name,
                    suggestion()
                )
            if (flag and NODE_TYPE != ARGUMENT) throw RuntimeException()
            val propertyDecoder = Node.Argument.PropertyDecoder(
                flag,
                decoder as PacketDecoder
            )
            val property =
                Node.Argument.Property.Serializer.deserialize(
                    propertyDecoder
                )
            return Node.Argument(
                isExecutable,
                children,
                redirect,
                name,
                property,
                suggestion()
            )
        }

        override fun serialize(encoder: Encoder, value: Node) {
            when (value) {
                is Node.Root -> {
                    var flags = 0
                    if (value.hasRedirection) flags = flags or REDIRECT
                    if (value.hasSuggestion) flags = flags or SUGGESTION
                    if (value.iSExecutable) flags = flags or EXECUTABLE
                    flags = flags or 0
                    encoder.encodeByte(flags.toByte())
                    VarIntSizedVarIntArraySerializer.serialize(encoder, value.children)
                    if (value.redirect != -1) VarIntSerializer.serialize(encoder, value.redirect)
                    if (value.suggestion !== null) IdentifierSerializer.serialize(encoder, value.suggestion)
                }

                is Node.Literal -> {
                    var flags = 0
                    if (value.hasRedirection) flags = flags or REDIRECT
                    if (value.hasSuggestion) flags = flags or SUGGESTION
                    if (value.iSExecutable) flags = flags or EXECUTABLE
                    flags = flags or 1
                    encoder.encodeByte(flags.toByte())
                    VarIntSizedVarIntArraySerializer.serialize(encoder, value.children)
                    if (value.redirect != -1) VarIntSerializer.serialize(encoder, value.redirect)
                    IdentifierSerializer.serialize(encoder, value.name)
                    if (value.suggestion !== null) IdentifierSerializer.serialize(encoder, value.suggestion)
                }

                is Node.Argument -> {
                    var flags = 0
                    if (value.hasRedirection) flags = flags or REDIRECT
                    if (value.hasSuggestion) flags = flags or SUGGESTION
                    if (value.iSExecutable) flags = flags or EXECUTABLE
                    flags = flags or 2
                    value.property.applyFlag { flags = flags or it }
                    encoder.encodeByte(flags.toByte())
                    VarIntSizedVarIntArraySerializer.serialize(encoder, value.children)
                    if (value.hasRedirection) VarIntSerializer.serialize(encoder, value.redirect)
                    IdentifierSerializer.serialize(encoder, value.name)
                    Node.Argument.Property.serialize(
                        encoder,
                        value.property
                    )
                    if (value.suggestion !== null) IdentifierSerializer.serialize(encoder, value.suggestion)
                }
            }
        }
    }
}

private infix fun Int.has(flag: Int) = (this and flag) == flag
