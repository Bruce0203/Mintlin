package mintlin.format.nbt.tag

import kotlinx.io.Buffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import mintlin.lang.classNameOf
import mintlin.lang.name
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.reflect.KClass

sealed interface Tag<T> : KSerializer<T> {
    val id: Int

    companion object RootCompoundFormat : TagFormat<Compound> by TagFormatImp(TagRootCompound)
    object CompoundFormat : TagFormat<Compound> by TagFormatImp(TagCompound)
    object IntArrayFormat : TagFormat<IntArray> by TagFormatImp(TagIntArray)
    object ListFormat : TagFormat<List<*>> by TagFormatImp(TagList)
}

interface TagFormat<T> : SerialFormat {
    val tag: Tag<T>

    fun decodeFromByteArray(byteArray: ByteArray): T

    fun decodeFromBuffer(buffer: Buffer): T

    fun encodeToBuffer(compound: T, buffer: Buffer)

    fun encodeToByteArray(compound: T): ByteArray
}

open class TagFormatImp<T>(override val tag: Tag<T>) : SerialFormat, TagFormat<T> {
    override val serializersModule = EmptySerializersModule()

    override fun decodeFromByteArray(byteArray: ByteArray): T =
        decodeFromBuffer(Buffer().apply { write(byteArray) })

    override fun decodeFromBuffer(buffer: Buffer): T =
        tag.deserialize(TagDecoder(buffer, serializersModule))

    @OptIn(ExperimentalSerializationApi::class)
    override fun encodeToBuffer(compound: T, buffer: Buffer) {
        val encoder = TagEncoder(serializersModule, buffer)
        tag.serialize(encoder, compound)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun encodeToByteArray(compound: T): ByteArray =
        TagEncoder(serializersModule).also { tag.serialize(it, compound) }.readByteArray()
}

val Tags = arrayOf(
    TagEnd, TagByte, TagShort, TagInt, TagLong, TagFloat, TagDouble,
    TagByteArray, TagString, TagList, TagCompound, TagIntArray, TagLongArray
).map { it.id to it }.toMap()

val ValueToTagMap = mapOf(
    Unit::class to TagEnd,
    Int::class to TagInt,
    Byte::class to TagByte,
    Boolean::class to TagBoolean,
    Short::class to TagShort,
    Long::class to TagLong,
    Float::class to TagFloat,
    Double::class to TagDouble,
    ByteArray::class to TagByteArray,
    String::class to TagString,
    ArrayList::class to TagList,
    HashMap::class to TagCompound,
    IntArray::class to TagIntArray,
    LongArray::class to TagLongArray
) as Map<KClass<*>, Tag<Any>>

data object TagEnd : Tag<Unit> {
    override val id get() = 0
    override val descriptor get() = buildClassSerialDescriptor(classNameOf<TagEnd>())
    override fun deserialize(decoder: Decoder) = Unit
    override fun serialize(encoder: Encoder, value: Unit) = Unit
}

data class TagByte(val value: Byte) : Tag<Byte> by this {
    companion object : Tag<Byte> {
        override val id get() = 1
        override val descriptor = buildClassSerialDescriptor(classNameOf<TagByte>())
        override fun deserialize(decoder: Decoder): Byte = decoder.decodeByte()
        override fun serialize(encoder: Encoder, value: Byte) = encoder.encodeByte(value)
    }
}

data class TagBoolean(val value: Byte) : Tag<Boolean> by this {
    companion object : Tag<Boolean> {
        override val id get() = 1
        override val descriptor = buildClassSerialDescriptor(classNameOf<TagBoolean>())
        override fun deserialize(decoder: Decoder): Boolean = decoder.decodeByte() != 0.toByte()
        override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeByte(if (value) 1 else 0)
    }
}

data class TagShort(val value: Short) : Tag<Short> by this {
    companion object : Tag<Short> {
        override val id get() = 2
        override val descriptor = buildClassSerialDescriptor(classNameOf<TagShort>())
        override fun deserialize(decoder: Decoder): Short = decoder.decodeShort()
        override fun serialize(encoder: Encoder, value: Short) = encoder.encodeShort(value)
    }
}

data class TagInt(val value: Int) : Tag<Int> by this {
    companion object : Tag<Int> {
        override val id get() = 3
        override val descriptor = buildClassSerialDescriptor(classNameOf<TagInt>())
        override fun deserialize(decoder: Decoder): Int = decoder.decodeInt()
        override fun serialize(encoder: Encoder, value: Int) = encoder.encodeInt(value)
    }
}

data class TagLong(val value: Long) : Tag<Long> by this {
    companion object : Tag<Long> {
        override val id get() = 4
        override val descriptor = buildClassSerialDescriptor(classNameOf<TagLong>())
        override fun deserialize(decoder: Decoder): Long = decoder.decodeLong()
        override fun serialize(encoder: Encoder, value: Long) = encoder.encodeLong(value)
    }
}

data class TagFloat(val value: Float) : Tag<Float> by this {
    companion object : Tag<Float> {
        override val id get() = 5
        override val descriptor = buildClassSerialDescriptor(classNameOf<TagFloat>())
        override fun deserialize(decoder: Decoder): Float = decoder.decodeFloat()
        override fun serialize(encoder: Encoder, value: Float) = encoder.encodeFloat(value)
    }
}

data class TagDouble(val value: Double) : Tag<Double> by this {
    companion object : Tag<Double> {
        override val id get() = 6
        override val descriptor = buildClassSerialDescriptor(classNameOf<TagDouble>())
        override fun deserialize(decoder: Decoder): Double = decoder.decodeDouble()
        override fun serialize(encoder: Encoder, value: Double) = encoder.encodeDouble(value)
    }
}

data class TagByteArray(val value: ByteArray) : Tag<ByteArray> by this {
    companion object : Tag<ByteArray> {
        override val id get() = 7
        override val descriptor = buildClassSerialDescriptor(classNameOf<TagByteArray>())
        override fun deserialize(decoder: Decoder) = ByteArray(decoder.decodeInt()) { decoder.decodeByte() }
        override fun serialize(encoder: Encoder, value: ByteArray) {
            encoder.encodeInt(value.size)
            value.forEach { encoder.encodeByte(it) }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TagByteArray

        return value.contentEquals(other.value)
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }
}

data class TagString(val value: String) : Tag<String> by this {
    companion object : Tag<String> {
        override val id get() = 8
        override val descriptor = buildClassSerialDescriptor(classNameOf<TagString>())
        override fun deserialize(decoder: Decoder): String = decoder.decodeString()
        override fun serialize(encoder: Encoder, value: String) = encoder.encodeString(value)
    }
}

data class TagList(val value: List<*>) : Tag<List<*>> by this {
    companion object : Tag<List<*>> {
        override val id get() = 9
        override val descriptor = buildClassSerialDescriptor(classNameOf<TagList>())

        override fun deserialize(decoder: Decoder): List<*> {
            val serializer = getTagById(decoder.decodeByte().toInt())
            return Array(decoder.decodeInt()) { decoder.decodeSerializableValue(serializer) }.toList()
        }

        override fun serialize(encoder: Encoder, value: List<*>) {
            lateinit var serializer: Tag<Any>
            value.forEachIndexed { i, v ->
                if (i == 0) {
                    serializer = getTagByValue(v!!)
                    encoder.encodeByte(serializer.id.toByte())
                    encoder.encodeInt(value.size)
                }
                serializer.serialize(encoder, v!!)
            }
            if (value.isEmpty()) {
                encoder.encodeByte(0)
                encoder.encodeInt(0)
            }
        }
    }
}

typealias Compound = @Serializable(TagRootCompound::class) HashMap<String, Any>
typealias StarProjectedCompound = @Serializable(TagRootCompound::class) HashMap<String, *>

fun createCompound() = HashMap<String, Any>()
fun Any.stringify() = if (this is List<*>) stringify() else if (this is HashMap<*, *>) stringify() else this
fun List<*>.stringify(): List<*> = map {
    when (it) {
        is HashMap<*, *> -> it.stringify()
        is List<*> -> it.stringify()
        is LongArray -> it.toList()
        is IntArray -> it.toList()
        is ByteArray -> it.toList()
        else -> it
    }
}

fun HashMap<*, *>.stringify(): HashMap<Any, Any> = toMap().mapValues {
    when (val value = it.value) {
        is HashMap<*, *> -> value.stringify()
        is List<*> -> value.stringify()
        is LongArray -> value.toList()
        is IntArray -> value.toList()
        is ByteArray -> value.toList()
        else -> value
    }
}.entries.sortedBy { it.key.toString() }.associate { it.key to it.value }.run(::HashMap)

class TagCompound : Tag<Compound> by this {
    companion object : Tag<Compound> {
        override val id get() = 10
        override val descriptor = buildClassSerialDescriptor(classNameOf<TagCompound>())
        override fun deserialize(decoder: Decoder): Compound {
            val map = createCompound()
            while (true) {
                val tag = getTagById(decoder.decodeByte().toInt())
                if (tag.id == TagEnd.id) break
                map[decoder.decodeString()] = tag.deserialize(decoder)
            }
            return map
        }

        override fun serialize(encoder: Encoder, value: Compound) {
            value.forEach { (key, any) ->
                val tag = getTagByValue(any)
                encoder.encodeByte(tag.id.toByte())
                encoder.encodeString(key)
                tag.serialize(encoder, any)
            }
            encoder.encodeByte(0)
        }
    }
}

object TagRootCompound : Tag<Compound> {
    override val id get() = 10
    override val descriptor = buildClassSerialDescriptor(classNameOf<TagRootCompound>())
    override fun deserialize(decoder: Decoder): Compound {
        return if (decoder.decodeByte().toInt() == TagEnd.id) createCompound()
        else TagCompound.deserialize(decoder)
    }

    override fun serialize(encoder: Encoder, value: Compound) {
        encoder.encodeByte(10)
        TagCompound.serialize(encoder, value)
    }
}

object SingleElementTagCompound : Tag<Compound> {
    override val id get() = 10
    override val descriptor = buildClassSerialDescriptor(classNameOf<TagRootCompound>())
    override fun deserialize(decoder: Decoder): Compound {
        decoder.decodeByte()
        decoder.decodeString()
        return TagCompound.deserialize(decoder)
    }

    override fun serialize(encoder: Encoder, value: Compound) {
        encoder.encodeByte(10)
        encoder.encodeString("")
        TagCompound.serialize(encoder, value)
    }
}

data class TagIntArray(val value: IntArray) : Tag<IntArray> by this {
    companion object : Tag<IntArray> {
        override val id get() = 11
        override val descriptor = buildClassSerialDescriptor(classNameOf<TagIntArray>())
        override fun deserialize(decoder: Decoder) = IntArray(decoder.decodeInt()) { decoder.decodeInt() }
        override fun serialize(encoder: Encoder, value: IntArray) {
            encoder.encodeInt(value.size)
            value.forEach { encoder.encodeInt(it) }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TagIntArray

        return value.contentEquals(other.value)
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }
}

data class TagLongArray(val value: LongArray) : Tag<LongArray> by this {
    companion object : Tag<LongArray> {
        override val id get() = 12
        override val descriptor = buildClassSerialDescriptor(classNameOf<TagLongArray>())
        override fun deserialize(decoder: Decoder) = LongArray(decoder.decodeInt()) { decoder.decodeLong() }
        override fun serialize(encoder: Encoder, value: LongArray) {
            encoder.encodeInt(value.size)
            value.forEach { encoder.encodeLong(it) }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TagLongArray

        return value.contentEquals(other.value)
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }
}

fun getTagById(id: Int) = Tags[id]
    ?: throw AssertionError("invalid tag id $id")

@Suppress("UNCHECKED_CAST")
private val AnyProjectedTagList = TagList as Tag<Any>
fun getTagByValue(value: Any): Tag<Any> = ValueToTagMap[value::class]
    ?: run { if (value is List<*>) AnyProjectedTagList else null }
    ?: throw AssertionError("tag serializer for ${value::class.name} not exists")
