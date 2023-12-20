package mintlin.minecraft.datastructure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.serializer.FixedSizeByteArraySerializer
import mintlin.serializer.VarIntSizedLongArraySerializer
import mintlin.lang.classNameOf
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun bitSetOf(vararg data: Long) = BitSet(data)

@Serializable
data class BitSet(
    @Serializable(VarIntSizedLongArraySerializer::class)
    val data: LongArray
) : Collection<Boolean> {
    constructor() : this(0)
    constructor(size: Int) : this(LongArray((size + 63) / 64))

    @Transient
    override val size: Int = data.size//fixme as bit size not long array size

    private fun part(index: Int) = index ushr 6
    private fun bit(index: Int) = index and 0x3f

    operator fun get(index: Int): Boolean = ((data[part(index)] ushr (bit(index))) and 1L) != 0L
    operator fun set(index: Int, value: Boolean) {
        val i = part(index)
        val b = bit(index)
        if (value) {
            data[i] = data[i] or (1L shl b)
        } else {
            data[i] = data[i] and (1L shl b).inv()
        }
    }

    fun set(index: Int) = set(index, true)
    fun unset(index: Int) = set(index, false)

    fun clear() = data.fill(0L)

    override fun contains(element: Boolean): Boolean = indices.any { this[it] == element }
    override fun containsAll(elements: Collection<Boolean>): Boolean = when {
        elements.contains(true) && !this.contains(true) -> false
        elements.contains(false) && !this.contains(false) -> false
        else -> true
    }

    override fun isEmpty(): Boolean = isEmpty()
    override fun iterator(): Iterator<Boolean> = indices.map { this[it] }.iterator()

    override fun hashCode(): Int = data.contentHashCode() + size
    override fun equals(other: Any?): Boolean =
        (other is BitSet) && this.size == other.size && this.data.contentEquals(other.data)
}

open class FixedBitSetSerializer(size: Int) : KSerializer<BitSet> {
    val size = (size + 7) / 8
    override val descriptor = buildClassSerialDescriptor(classNameOf<BitSet>())
    private val byteArraySerializer = FixedSizeByteArraySerializer(this.size)

    override fun deserialize(decoder: Decoder): BitSet {
        return byteArrayToBitSet(byteArraySerializer.deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: BitSet) {
        byteArraySerializer.serialize(encoder, value.toByteArray().copyOf(size))
    }

    fun byteArrayToBitSet(byteArray: ByteArray): BitSet {
        val bitSet = BitSet(byteArray.size * 8)
        for (i in byteArray.indices) {
            for (j in 0 until 8) {
                if ((byteArray[i].toInt() and (1 shl (7 - j))) != 0) {
                    bitSet.set(i * 8 + j)
                }
            }
        }
        return bitSet
    }

    fun BitSet.toByteArray(): ByteArray {
        val n: Int = size
        if (n == 0) return ByteArray(0)
        var len = 8 * (n - 1)
        run {
            var x: Long = data[n - 1]
            while (x != 0L) {
                len++
                x = x ushr 8
            }
        }
        val bytes = ByteArray(len)
        val bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until n - 1) bb.putLong(data[i])
        var x: Long = data[n - 1]
        while (x != 0L) {
            bb.put((x and 0xffL).toByte())
            x = x ushr 8
        }
        return bytes
    }

    fun bitSetToLongArray(bitSet: BitSet): LongArray {
        val longArraySize = (bitSet.size + 63) / 64
        val longArray = LongArray(longArraySize)

        for (i in bitSet.indices) {
            if (bitSet[i]) {
                val longIndex = i / 64
                val bitIndex = i % 64
                longArray[longIndex] = longArray[longIndex] or (1L shl (63 - bitIndex))
            }
        }

        return longArray
    }
}
