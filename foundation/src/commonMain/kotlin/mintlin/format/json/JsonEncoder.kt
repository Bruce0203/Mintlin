@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package mintlin.format.json

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.internal.NamedValueEncoder
import kotlinx.serialization.modules.SerializersModule
import mintlin.datastructure.FastArrayList
import kotlin.collections.set

class JsonCompoundEncoder(
    val compound: MutableMap<String, Any>,
    override val serializersModule: SerializersModule,
) : NamedValueEncoder(), mintlin.format.NamedTagEncoder {
    constructor(serializersModule: SerializersModule) : this(LinkedHashMap(), serializersModule)
    override val currentNamedTag: String get() = currentTag
    override val currentNamedTagOrNull: String? get() = currentTagOrNull
    override fun encodeNamedValue(tag: String, value: Any) = encodeTaggedValue(tag, value)
    override fun encodeTaggedNull(tag: String) { if (currentTagOrNull !== null) popTag() }
    override fun composeName(parentName: String, childName: String): String = childName

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        if (currentTagOrNull === null) return this
        return when (descriptor.kind) {
            StructureKind.LIST -> {
                val list = FastArrayList<Any>().also { compound[currentTag] = it }
                JsonListEncoder(list, serializersModule)
            }
            else -> {
                val tag = LinkedHashMap<String, Any>().also { compound[currentTag] = it }
                JsonCompoundEncoder(tag, serializersModule)
            }
        }
    }

    override fun encodeTaggedValue(tag: String, value: Any) {
        compound[tag] = value
    }

}

class JsonListEncoder(
    private val list: FastArrayList<Any>,
    override val serializersModule: SerializersModule
) : NamedValueEncoder(), mintlin.format.NamedTagEncoder {
    override val currentNamedTag: String get() = currentTag
    override val currentNamedTagOrNull: String? get() = currentTagOrNull
    override fun encodeNamedValue(tag: String, value: Any) = encodeTaggedValue(tag, value)
    override fun encodeTaggedNull(tag: String) { if (currentTagOrNull !== null) popTag() }
    override fun composeName(parentName: String, childName: String): String = childName

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        if (currentTagOrNull === null) return this
        return when (descriptor.kind) {
            StructureKind.LIST -> {
                val list = FastArrayList<Any>().also { list.add(it) }
                JsonListEncoder(list, serializersModule)
            }
            else -> {
                val tag = LinkedHashMap<String, Any>().also { list.add(it) }
                JsonCompoundEncoder(tag, serializersModule)
            }
        }
    }

    override fun encodeTaggedValue(tag: String, value: Any) {
        list.add(value)
    }
}