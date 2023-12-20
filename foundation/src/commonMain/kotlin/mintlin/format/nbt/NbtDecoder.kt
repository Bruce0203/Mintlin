@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package mintlin.format.nbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.internal.NamedValueDecoder
import kotlinx.serialization.modules.SerializersModule
import mintlin.format.nbt.tag.StarProjectedCompound
import mintlin.lang.fastCastTo

class NbtCompoundDecoder(
    val compound: StarProjectedCompound,
    override val serializersModule: SerializersModule
) : NamedValueDecoder(), mintlin.format.PolymorphicDecoder, mintlin.format.NamedTagDecoder {
    var elementIndex: Int = -1
    private lateinit var elementName: String
    override val currentNamedTag: String get() = currentTag
    override val currentNamedTagOrNull: String? get() = currentTagOrNull

    override fun decodeValue() = compound[elementName]!!

    override fun decodeTaggedValue(tag: String): Any {
        return compound[tag]!!
    }

    override fun decodeTaggedBoolean(tag: String): Boolean {
        return super.decodeTaggedByte(tag) != 0.toByte()
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        do {
            if (++elementIndex >= descriptor.elementsCount) return -1
            elementName = descriptor.getTag(elementIndex)
        } while (elementName !in compound)
        return elementIndex
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return when (descriptor.kind) {
            StructureKind.LIST -> NbtListDecoder(decodeValue().fastCastTo(), serializersModule)
            else -> {
                if (currentTagOrNull === null) this
                else NbtCompoundDecoder(decodeValue().fastCastTo(), serializersModule)
            }
        }
    }
}


class NbtListDecoder(
    private val list: List<Any>,
    override val serializersModule: SerializersModule
) : NamedValueDecoder(), mintlin.format.PolymorphicDecoder, mintlin.format.NamedTagDecoder {
    override val currentNamedTag: String get() = currentTag
    override val currentNamedTagOrNull: String? get() = currentTagOrNull
    private var elementIndex: Int = -1

    override fun decodeValue(): Any {
        return list[elementIndex]
    }

    override fun decodeTaggedValue(tag: String): Any {
        return list[elementIndex]
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return when (descriptor.kind) {
            StructureKind.LIST -> NbtListDecoder(decodeValue().fastCastTo(), serializersModule)
            else -> {
                if (currentTagOrNull === null) this
                else NbtCompoundDecoder(decodeValue().fastCastTo(), serializersModule)
            }
        }
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = list.size

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return if (++elementIndex == decodeCollectionSize(descriptor)) CompositeDecoder.DECODE_DONE
        else elementIndex
    }
}

