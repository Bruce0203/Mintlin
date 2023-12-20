package mintlin.minecraft.datastructure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.datastructure.FastIdentityMap
import mintlin.datastructure.FastMap
import mintlin.format.nbt.tag.TagRootCompound
import mintlin.lang.classNameOf
import mintlin.lang.fastCastTo
import mintlin.serializer.*
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias MetadataValue = Metadata.Value

fun metaDataOf(vararg entries: Pair<Int, MetadataValue>) = Metadata().also { map ->
    entries.forEach {
        map.map[it.first] = it.second
    }
}

@Serializable(Metadata.Serializer::class)
open class Metadata private constructor(val map: FastMap<Int, MetadataValue>) {
    constructor() : this(FastIdentityMap())

    data class Value(val type: MetadataType, val value: Any?)

    operator fun get(id: Int) = map[id]?.value

    operator fun set(id: Int, type: MetadataType, value: Any) {
        map[id] = Value(type, value)
    }

    fun remove(id: Int) = map.remove(id)

    override fun toString(): String {
        return "EntityMetadata(${
            map.map {
                "Entry(index=${it.key}, value=${it.value.value}(${it.value.type}))"
            }
        })"
    }

    object Serializer : KSerializer<Metadata> {
        private val END_INDEX = 0xFF.toUByte()
        override val descriptor = buildClassSerialDescriptor(classNameOf<Metadata>())
        override fun deserialize(decoder: Decoder): Metadata {
            val map = FastIdentityMap<Int, MetadataValue>()
            while (true) {
                val index = decoder.decodeByte().toInt()
                if (index.toUByte() == END_INDEX) break
                val type = MetadataType.deserialize(decoder)
                val value = type.serializer.deserialize(decoder)
                map[index] = MetadataValue(type = type, value = value)
            }
            return Metadata(map)
        }

        override fun serialize(encoder: Encoder, value: Metadata) {
            value.map.forEach { entry ->
                val key = entry.key
                val value = entry.value
                encoder.encodeByte(key.toByte())
                MetadataType.serialize(encoder, value.type)
                value.type.serializer.serialize(encoder, value.value)
            }
            encoder.encodeByte(END_INDEX.toByte())
        }
    }
}

object NotYetImplementedSerializer : KSerializer<Nothing> {
    override fun deserialize(decoder: Decoder): Nothing = throw NotImplementedError()

    override val descriptor get() = throw NotImplementedError()

    override fun serialize(encoder: Encoder, value: Nothing): Unit = throw NotImplementedError()
}

@Serializable(MetadataType.Serializer::class)
enum class MetadataType(
    override val value: Int,
    serializer: KSerializer<*>
) : VarIntEnum {
    Byte(0, ByteSerializer),
    VarInt(1, VarIntSerializer),
    VarLong(2, VarLongSerializer),
    Float(3, FloatSerializer),
    String(4, VarString32767Serializer),
    Chat(5, mintlin.minecraft.datastructure.Chat),
    OptChat(6, JsonChatSerializer.nullable),
    Slot(7, SlotSerializer),
    Boolean(8, BooleanSerializer),
    Rotation(9, FloatPosition.serializer()),
    Position(10, PositionSerializer),
    OptPosition(11, mintlin.minecraft.datastructure.Position.serializer().nullable),
    Direction(12, mintlin.minecraft.datastructure.Direction.serializer()),
    OptUUID(13, UUIDSerializer.nullable),
    BlockId(14, VarIntSerializer),
    OptBlockId(15, VarIntSerializer.nullable),
    NBT(16, TagRootCompound),
    Particle(17, NotYetImplementedSerializer),
    VillagerData(18, NotYetImplementedSerializer),
    OptVarInt(19, NotYetImplementedSerializer),
    Pose(20, PoseSerializer),
    CatVariant(21, NotYetImplementedSerializer),
    FrogVariant(22, NotYetImplementedSerializer),
    OptGlobalPos(23, NotYetImplementedSerializer),
    PaintingVariant(24, NotYetImplementedSerializer),
    SnifferState(25, NotYetImplementedSerializer),
    Vector3(26, NotYetImplementedSerializer),
    Quaternion(27, NotYetImplementedSerializer);

    @Suppress("UNCHECKED_CAST")
    val serializer: KSerializer<Any?> = serializer as KSerializer<Any?>

    companion object Serializer : KSerializer<MetadataType> by varIntEnumSerializer(entries)
}

typealias PoseSerializer = Pose.Serializer

@Serializable(Pose.Serializer::class)
enum class Pose(override val value: Int) : VarIntEnum {
    Standing(0), FallFlying(1), Sleeping(2), Swimming(3), SpinAttack(4), Sneaking(5), LongJumping(6), Dying(7),
    Croaking(8), UsingTongue(9), Sitting(10), Roaring(11), Sniffing(12), Emerging(13), Digging(14);

    companion object Serializer : KSerializer<Pose> by varIntEnumSerializer(entries)
}

@Serializable(Direction.Serializer::class)
enum class Direction(override val value: Int) : VarIntEnum {
    Down(0), Up(1), North(2), South(3), West(4), East(5);

    companion object Serializer : KSerializer<Direction> by varIntEnumSerializer(entries)
}

class MetadataProperty<T : MetadataHolder, V : Any>(
    val id: Int, val type: MetadataType, val defaultValue: () -> V
) : ReadWriteProperty<T, V> {
    override fun getValue(thisRef: T, property: KProperty<*>): V {
        return thisRef.metadata[id].fastCastTo() ?: defaultValue()
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        thisRef.metadata[id, type] = value
    }
}

class NullableMetadataProperty<T : MetadataHolder, V>(val id: Int, val type: MetadataType) : ReadWriteProperty<T, V?> {
    override fun getValue(thisRef: T, property: KProperty<*>): V? = thisRef.metadata[id].fastCastTo()

    override fun setValue(thisRef: T, property: KProperty<*>, value: V?) {
        if (value === null) thisRef.metadata.remove(id) else thisRef.metadata[id, type] = value
    }
}

class FlagDelegate<T : MetadataHolder, D : ReadWriteProperty<T, Byte>>(
    private val flag: Byte, private val delegate: D
) : ReadWriteProperty<T, Boolean> {
    override fun getValue(thisRef: T, property: KProperty<*>): Boolean = delegate.getValue(thisRef, property) has flag

    override fun setValue(thisRef: T, property: KProperty<*>, value: Boolean) {
        val get = delegate.getValue(thisRef, property)
        delegate.setValue(
            thisRef, property,
            if (value) get set flag else get remove flag
        )
    }

    private infix fun Byte.has(flag: Byte) = this and flag == flag
    private infix fun Byte.set(flag: Byte) = this or flag
    private infix fun Byte.remove(flag: Byte) = this and flag.inv()
}

interface MetadataHolder {
    val metadata: Metadata
}



