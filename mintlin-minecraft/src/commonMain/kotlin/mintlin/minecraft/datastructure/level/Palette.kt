@file:Suppress("ArrayInDataClass")

package mintlin.minecraft.datastructure.level

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf
import mintlin.minecraft.datastructure.Point3D
import mintlin.serializer.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.min

fun interface EntrySupplier {
    fun get(position: Point3D): Int
}

fun interface EntryConsumer {
    fun accept(position: Point3D, value: Int)
}

fun interface EntryFunction {
    fun apply(position: Point3D, value: Int): Int
}

fun interface IntUnaryOperator {
    fun applyAsInt(operand: Int): Int
}

interface PaletteTypeDelegate {
    val type: PaletteType
    val dimension: Int get() = type.dimension
    val maxBitsPerEntry: Int get() = type.maxBitsPerEntry
    val maxSize: Int get() = type.maxSize
    val defaultBitsPerEntry: Int get() = type.defaultBitsPerEntry
}

enum class PaletteType(
    override val dimension: Int, override val maxBitsPerEntry: Int, override val defaultBitsPerEntry: Int
) : PaletteTypeDelegate {
    BLOCK(dimension = 16, maxBitsPerEntry = 8, defaultBitsPerEntry = 8),
    BIOME(dimension = 4, maxBitsPerEntry = 3, defaultBitsPerEntry = 0);
    override val type = this
    override val maxSize: Int = dimension * dimension * dimension
    val globalPalette: Palette = GlobalPalette(this)
}

interface Palette : PaletteTypeDelegate {
    override val type: PaletteType

    val bitsPerEntry: Int
    val count: Int

    fun clone(): Palette
    fun set(position: Point3D, value: Int)
    fun get(position: Point3D): Int
    fun getAll(consumer: EntryConsumer)
    fun getAllPresent(consumer: EntryConsumer)
    fun fill(value: Int)
    fun setAll(supplier: EntrySupplier)
    fun replace(position: Point3D, operator: IntUnaryOperator)

    fun replaceAll(function: EntryFunction)

    companion object {
        operator fun invoke(type: PaletteType): Palette = AdaptivePalette(FilledPalette(type, 0))
    }
}

data class AdaptivePalette(var palette: Palette) : Palette, Cloneable {

    override val type: PaletteType get() = palette.type
    override val bitsPerEntry: Int get() = palette.bitsPerEntry
    override val count: Int get() = palette.count
    override fun get(position: Point3D): Int = palette.get(position)
    override fun getAll(consumer: EntryConsumer) = palette.getAll(consumer)
    override fun getAllPresent(consumer: EntryConsumer) = palette.getAllPresent(consumer)

    override fun clone(): Palette {
        val adaptivePalette = super.clone() as AdaptivePalette
        adaptivePalette.palette = palette.clone()
        return adaptivePalette
    }

    override fun fill(value: Int) {
        palette = FilledPalette(type, value)
    }

    override fun set(position: Point3D, value: Int) {
        getOrChangeToFlexible().set(position, value)
    }

    override fun setAll(supplier: EntrySupplier) {
        palette = FlexiblePalette(type)
        palette.setAll(supplier)
    }

    override fun replace(position: Point3D, operator: IntUnaryOperator) {
        getOrChangeToFlexible().replace(position, operator)
    }

    override fun replaceAll(function: EntryFunction) {
        getOrChangeToFlexible().replaceAll(function)
    }

    private fun getOrChangeToFlexible(): Palette = if (palette is FlexiblePalette) palette else {
        val flexiblePalette = FlexiblePalette(type)
        palette = flexiblePalette
        flexiblePalette
    }
}

private data class FilledPalette(override val type: PaletteType, var value: Int) : Palette {
    override val bitsPerEntry: Int get() = 0
    override val count: Int get() = if (value != 0) maxSize else 0

    override fun clone(): Palette = this

    override fun set(position: Point3D, value: Int) = throw UnsupportedOperationException()

    override fun get(position: Point3D): Int = value
    override fun getAll(consumer: EntryConsumer) {
        val value = value
        val dimension = dimension
        for (y in 0 until dimension)
            for (z in 0 until dimension)
                for (x in 0 until dimension)
                    consumer.accept(Point3D(x, y, z), value)
    }

    override fun getAllPresent(consumer: EntryConsumer) {
        if (value != 0) getAll(consumer)
    }

    override fun fill(value: Int) { this.value = value }

    override fun setAll(supplier: EntrySupplier) = throw UnsupportedOperationException()

    override fun replace(position: Point3D, operator: IntUnaryOperator) = throw UnsupportedOperationException()

    override fun replaceAll(function: EntryFunction): Unit = throw UnsupportedOperationException()
}

class GlobalPalette(override val type: PaletteType) : Palette {
    override val bitsPerEntry: Int get() = throw UnsupportedOperationException()
    override val count: Int get() = throw UnsupportedOperationException()
    override fun clone(): Palette = throw UnsupportedOperationException()
    override fun set(position: Point3D, value: Int): Unit = throw UnsupportedOperationException()
    override fun get(position: Point3D): Int = throw UnsupportedOperationException()
    override fun getAll(consumer: EntryConsumer): Unit = throw UnsupportedOperationException()
    override fun getAllPresent(consumer: EntryConsumer): Unit = throw UnsupportedOperationException()
    override fun fill(value: Int): Unit = throw UnsupportedOperationException()
    override fun setAll(supplier: EntrySupplier): Unit = throw UnsupportedOperationException()
    override fun replace(position: Point3D, operator: IntUnaryOperator): Unit = throw UnsupportedOperationException()
    override fun replaceAll(function: EntryFunction): Unit = throw UnsupportedOperationException()
}

private fun getDataArraySize(bitsPerEntry: Int, maxSize: Int): Int {
    val valuesPerLong = 64 / bitsPerEntry
    return (maxSize + valuesPerLong - 1) / valuesPerLong
}

data class FlexiblePalette(
    override val type: PaletteType,
    override var bitsPerEntry: Int = type.defaultBitsPerEntry,
    var paletteToValueList: ArrayList<Int> = ArrayList<Int>(1).also { it.add(0) },
    var values: LongArray = LongArray(getDataArraySize(bitsPerEntry, type.maxSize))
) : Palette, Cloneable {
    override var count: Int = 0

    var valueToPaletteMap = if (paletteToValueList.size == 1) HashMap<Int, Int>(1).also { it[0] = 0 }
    else { HashMap(paletteToValueList.mapIndexed { i, value -> value to i }.toMap()) }

    override fun clone(): Palette {
        val palette = super.clone() as FlexiblePalette
        palette.values = values.clone()
        palette.paletteToValueList = ArrayList(this.paletteToValueList)
        palette.valueToPaletteMap = HashMap(valueToPaletteMap)
        palette.count = count
        return palette
    }

    override fun get(position: Point3D): Int {
        val bitsPerEntry = bitsPerEntry
        val sectionIndex: Int = getSectionIndex(dimension, position)
        val valuesPerLong = 64 / bitsPerEntry
        val index = sectionIndex / valuesPerLong
        val bitIndex = (sectionIndex - index * valuesPerLong) * bitsPerEntry
        val value = (values[index] shr bitIndex).toInt() and (1 shl bitsPerEntry) - 1
        return if (hasPalette()) paletteToValueList[value] else value
    }

    override fun getAll(consumer: EntryConsumer) {
        retrieveAll(consumer, consumeEmpty = true)
    }

    override fun getAllPresent(consumer: EntryConsumer) {
        retrieveAll(consumer, consumeEmpty = false)
    }

    override fun set(position: Point3D, value: Int) {
        var value = getPaletteIndex(value)
        val bitsPerEntry = bitsPerEntry
        val values = this.values
        val valuesPerLong = 64 / bitsPerEntry
        val sectionIndex: Int = getSectionIndex(dimension, position)
        val index = sectionIndex / valuesPerLong
        val bitIndex = (sectionIndex - index * valuesPerLong) * bitsPerEntry

        val block = values[index]
        val clear = (1L shl bitsPerEntry) - 1L
        val oldBlock = block shr bitIndex and clear
        values[index] = block and (clear shl bitIndex).inv() or (value.toLong() shl bitIndex)
        val currentAir = oldBlock == 0L
        if (currentAir != (value == 0)) count += if (currentAir) 1 else -1
    }

    override fun fill(value: Int) {
        @Suppress("NAME_SHADOWING")
        var value = value
        if (value == 0) {
            Arrays.fill(values, 0)
            count = 0
            return
        }
        value = getPaletteIndex(value)
        val bitsPerEntry = bitsPerEntry
        val valuesPerLong = 64 / bitsPerEntry
        val values = values
        var block: Long = 0
        for (i in 0 until valuesPerLong) block = block or (value.toLong() shl i * bitsPerEntry)
        Arrays.fill(values, block)
        count = maxSize

    }

    override fun setAll(supplier: EntrySupplier) {
        val cache = IntArray(4096)
        val dimension: Int = dimension
        var fillValue = -1
        var count = 0
        var index = 0
        for (y in 0 until dimension) {
            for (z in 0 until dimension) {
                for (x in 0 until dimension) {
                    var value = supplier.get(Point3D(x, y, z))
                    if (fillValue != -2) {
                        if (fillValue == -1) {
                            fillValue = value
                        } else if (fillValue != value) {
                            fillValue = -2
                        }
                    }
                    if (value != 0) {
                        value = getPaletteIndex(value)
                        count++
                    }
                    cache[index++] = value
                }
            }
        }
        assert(index == maxSize)
        if (fillValue < 0) {
            updateAll(cache)
            this.count = count
        } else {
            fill(fillValue)
        }

    }

    override fun replace(position: Point3D, operator: IntUnaryOperator) {
        val oldValue = get(position)
        val newValue = operator.applyAsInt(oldValue)
        if (oldValue != newValue) set(position, newValue)
    }

    override fun replaceAll(function: EntryFunction) {
        val cache = IntArray(4096)
        val arrayIndex = AtomicInteger()
        val count = AtomicInteger()
        getAll { point, value ->
            val newValue: Int = function.apply(point, value)
            val index = arrayIndex.getPlain()
            arrayIndex.plain = index + 1
            cache[index] = if (newValue != value) getPaletteIndex(newValue) else value
            if (newValue != 0) count.plain = count.getPlain() + 1
        }
        assert(arrayIndex.getPlain() == maxSize)
        updateAll(cache)
        this.count = count.getPlain()
    }

    private fun retrieveAll(consumer: EntryConsumer, consumeEmpty: Boolean) {
        if (!consumeEmpty && count == 0) return
        val values: LongArray = this.values
        val dimension: Int = dimension
        val bitsPerEntry = bitsPerEntry
        val magicMask = (1 shl bitsPerEntry) - 1
        val valuesPerLong = 64 / bitsPerEntry
        val size: Int = maxSize
        val dimensionMinus = dimension - 1
        val ids: IntArray? = if (hasPalette()) paletteToValueList.toIntArray() else null
        val dimensionBitCount = bitsToRepresent(dimensionMinus)
        val shiftedDimensionBitCount = dimensionBitCount shl 1
        for (i in values.indices) {
            val value = values[i]
            val startIndex = i * valuesPerLong
            val endIndex = min((startIndex + valuesPerLong).toDouble(), size.toDouble()).toInt()
            for (index in startIndex until endIndex) {
                val bitIndex = (index - startIndex) * bitsPerEntry
                val paletteIndex = (value shr bitIndex and magicMask.toLong()).toInt()
                if (consumeEmpty || paletteIndex != 0) {
                    val y = index shr shiftedDimensionBitCount
                    val z = index shr dimensionBitCount and dimensionMinus
                    val x = index and dimensionMinus
                    val result = if (ids != null && paletteIndex < ids.size) ids[paletteIndex] else paletteIndex
                    consumer.accept(Point3D(x, y, z), result)
                }
            }
        }
    }

    private fun updateAll(paletteValues: IntArray) {
        val size: Int = maxSize
        assert(paletteValues.size >= size)
        val bitsPerEntry = bitsPerEntry
        val valuesPerLong = 64 / bitsPerEntry
        val clear = (1L shl bitsPerEntry) - 1L
        val values = values
        for (i in values.indices) {
            var block = values[i]
            val startIndex = i * valuesPerLong
            val endIndex = min((startIndex + valuesPerLong).toDouble(), size.toDouble()).toInt()
            for (index in startIndex until endIndex) {
                val bitIndex = (index - startIndex) * bitsPerEntry
                block = block and (clear shl bitIndex).inv() or (paletteValues[index].toLong() shl bitIndex)
            }
            values[i] = block
        }
    }


    fun resize(newBitsPerEntry: Int) {
        var newBitsPerEntry = if (newBitsPerEntry > maxBitsPerEntry) 15 else newBitsPerEntry
        val palette = FlexiblePalette(type, newBitsPerEntry)
        palette.paletteToValueList = paletteToValueList
        palette.valueToPaletteMap = valueToPaletteMap
        getAll { position: Point3D, value: Int -> palette.set(position, value) }
        bitsPerEntry = palette.bitsPerEntry
        this.values = palette.values
        assert(count == palette.count)
    }

    private fun getPaletteIndex(value: Int): Int {
        if (!hasPalette()) return value
        val lastPaletteIndex: Int = this.paletteToValueList.size
        val bpe = bitsPerEntry.toByte()
        if (lastPaletteIndex >= maxPaletteSize(bpe.toInt())) {
            resize((bpe + 1))
            return getPaletteIndex(value)
        }
        return valueToPaletteMap.getOrPut(value) {
            this.paletteToValueList.add(value)
            assert(lastPaletteIndex < maxPaletteSize(bpe.toInt()))
            lastPaletteIndex
        }
    }

    fun hasPalette(): Boolean = bitsPerEntry <= maxBitsPerEntry

    fun getSectionIndex(dimension: Int, point: Point3D): Int {
        val dimensionMask = dimension - 1
        val dimensionBitCount: Int = bitsToRepresent(dimensionMask)
        return (point.y and dimensionMask) shl (dimensionBitCount shl 1) or (
                (point.z and dimensionMask) shl dimensionBitCount) or
                (point.x and dimensionMask)
    }

    private fun bitsToRepresent(n: Int): Int = 32 - n.countLeadingZeroBits()

    private fun maxPaletteSize(bitsPerEntry: Int): Int {
        return 1 shl bitsPerEntry
    }
}

class PaletteSerializer(override val type: PaletteType) : KSerializer<Palette>, PaletteTypeDelegate {
    @ExperimentalSerializationApi
    @InternalSerializationApi
    override val descriptor = buildSerialDescriptor(classNameOf<PaletteSerializer>(), PolymorphicKind.SEALED)

    override fun deserialize(decoder: Decoder): Palette {
        val bitsPerEntry = decoder.decodeByte().toInt()
        return AdaptivePalette(when {
            bitsPerEntry == 0 -> FilledPalette(type, value = VarIntSerializer.deserialize(decoder))
                .also { decoder.decodeEmptyDataArraySize() }
            bitsPerEntry <= maxBitsPerEntry -> FlexiblePalette(type, bitsPerEntry,
                paletteToValueList = VarIntSizedVarIntArraySerializer.deserialize(decoder).run { ArrayList(this.toList()) },
                values = VarIntSizedLongArraySerializer.deserialize(decoder))
            else -> type.globalPalette.also { decoder.decodeEmptyDataArraySize() }
        })
    }

    override fun serialize(encoder: Encoder, value: Palette) {
        encoder.encodeByte(value.bitsPerEntry.toByte())
        when (val value = (value as AdaptivePalette).palette) {
            is FilledPalette -> {
                VarIntSerializer.serialize(encoder, value.value)
                encoder.encodeEmptyDataArraySize()
            }
            is FlexiblePalette -> {
                VarIntSizedIntArrayListSerializer.serialize(encoder, value.paletteToValueList)
                VarIntSizedLongArraySerializer.serialize(encoder, value.values)
            }
            is GlobalPalette -> encoder.encodeEmptyDataArraySize()
        }
    }

    private fun Decoder.decodeEmptyDataArraySize() { decodeByte() }

    private fun Encoder.encodeEmptyDataArraySize() { encodeByte(0) }
}

class PaletteDiskSerializer(override val type: PaletteType) : KSerializer<Palette>, PaletteTypeDelegate {
    @ExperimentalSerializationApi
    @InternalSerializationApi
    override val descriptor = buildSerialDescriptor(classNameOf<PaletteSerializer>(), PolymorphicKind.SEALED)

    override fun deserialize(decoder: Decoder): Palette {
        val bitsPerEntry = decoder.decodeByte().toInt()
        return AdaptivePalette(when {
            bitsPerEntry == 0 -> FilledPalette(type, value = VarIntSerializer.deserialize(decoder))
                .also { decoder.decodeEmptyDataArraySize() }
            bitsPerEntry <= maxBitsPerEntry -> FlexiblePalette(type, bitsPerEntry,
                paletteToValueList = VarIntSizedVarIntArraySerializer.deserialize(decoder).run { ArrayList(this.toList()) },
                values = VarIntSizedLongArraySerializer.deserialize(decoder))
                .apply { count = VarIntSerializer.deserialize(decoder) }
            else -> type.globalPalette.also { decoder.decodeEmptyDataArraySize() }
        })
    }

    override fun serialize(encoder: Encoder, value: Palette) {
        encoder.encodeByte(value.bitsPerEntry.toByte())
        when (val value = (value as AdaptivePalette).palette) {
            is FilledPalette -> {
                VarIntSerializer.serialize(encoder, value.value)
                encoder.encodeEmptyDataArraySize()
            }
            is FlexiblePalette -> {
                VarIntSizedIntArrayListSerializer.serialize(encoder, value.paletteToValueList)
                VarIntSizedLongArraySerializer.serialize(encoder, value.values)
                VarIntSerializer.serialize(encoder, value.count)
            }
            is GlobalPalette -> encoder.encodeEmptyDataArraySize()
        }
    }

    private fun Decoder.decodeEmptyDataArraySize() { decodeByte() }

    private fun Encoder.encodeEmptyDataArraySize() { encodeByte(0) }
}