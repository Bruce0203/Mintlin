@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package mintlin.format.nbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.internal.NamedValueEncoder
import kotlinx.serialization.modules.SerializersModule
import mintlin.format.nbt.tag.Compound
import mintlin.format.nbt.tag.createCompound

class NbtCompoundEncoder(
    val compound: Compound,
    override val serializersModule: SerializersModule,
) : NamedValueEncoder(), mintlin.format.NamedTagEncoder {
    override val currentNamedTag: String get() = currentTag
    override val currentNamedTagOrNull: String? get() = currentTagOrNull
    override fun encodeNamedValue(tag: String, value: Any) = encodeTaggedValue(tag, value)
    override fun composeName(parentName: String, childName: String): String = childName
    override fun encodeTaggedNull(tag: String) { if (currentTagOrNull !== null) popTag() }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        if (currentTagOrNull === null) return this
        return when (descriptor.kind) {
            StructureKind.LIST -> {
                val list = ArrayList<Any>().also { compound[currentTag] = it }
                NbtListEncoder(list, serializersModule)
            }
            else -> {
                val tag = createCompound().also { compound[currentTag] = it }
                NbtCompoundEncoder(tag, serializersModule)
            }
        }
    }

    override fun encodeTaggedValue(tag: String, value: Any) {
        compound[tag] = value
    }

    override fun encodeTaggedBoolean(tag: String, value: Boolean) {
        encodeTaggedByte(tag, if (value) 1 else 0)
    }
}

class NbtListEncoder(
    private val list: MutableList<Any>,
    override val serializersModule: SerializersModule
) : NamedValueEncoder(), mintlin.format.NamedTagEncoder {
    override val currentNamedTag: String get() = currentTag
    override val currentNamedTagOrNull: String? get() = currentTagOrNull
    override fun encodeNamedValue(tag: String, value: Any) = encodeTaggedValue(tag, value)
    override fun composeName(parentName: String, childName: String): String = childName
    override fun encodeTaggedNull(tag: String) { popTag() }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        if (currentTagOrNull === null) return this
        return when (descriptor.kind) {
            StructureKind.LIST -> {
                val list = ArrayList<Any>().also { list.add(it) }
                NbtListEncoder(list, serializersModule)
            }
            else -> {
                val tag = createCompound().also { list.add(it) }
                NbtCompoundEncoder(tag, serializersModule)
            }
        }
    }

    override fun encodeTaggedValue(tag: String, value: Any) {
        list.add(value)
    }
}