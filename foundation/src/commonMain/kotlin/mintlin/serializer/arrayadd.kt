package mintlin.serializer

public fun arrayadd(array: ByteArray, value: Byte, start: Int = 0, end: Int = array.size) { for (n in start until end) array[n] = (array[n] + value).toByte() }
public fun arrayadd(array: ShortArray, value: Short, start: Int = 0, end: Int = array.size) { for (n in start until end) array[n] = (array[n] + value).toShort() }
public fun arrayadd(array: IntArray, value: Int, start: Int = 0, end: Int = array.size) { for (n in start until end) array[n] = array[n] + value }
public fun arrayadd(array: LongArray, value: Long, start: Int = 0, end: Int = array.size) { for (n in start until end) array[n] = array[n] + value }
public fun arrayadd(array: FloatArray, value: Float, start: Int = 0, end: Int = array.size) { for (n in start until end) array[n] = array[n] + value }
public fun arrayadd(array: DoubleArray, value: Double, start: Int = 0, end: Int = array.size) { for (n in start until end) array[n] = array[n] + value }

public fun <T> arrayfill(array: Array<T>, value: T, start: Int = 0, end: Int = array.size): Unit = array.fill(value, start, end)
public fun arrayfill(array: BooleanArray, value: Boolean, start: Int = 0, end: Int = array.size): Unit = array.fill(value, start, end)
public fun arrayfill(array: LongArray, value: Long, start: Int = 0, end: Int = array.size): Unit = array.fill(value, start, end)
public fun arrayfill(array: ByteArray, value: Byte, start: Int = 0, end: Int = array.size): Unit = array.fill(value, start, end)
public fun arrayfill(array: ShortArray, value: Short, start: Int = 0, end: Int = array.size): Unit = array.fill(value, start, end)
public fun arrayfill(array: IntArray, value: Int, start: Int = 0, end: Int = array.size): Unit = array.fill(value, start, end)
public fun arrayfill(array: FloatArray, value: Float, start: Int = 0, end: Int = array.size): Unit = array.fill(value, start, end)
public fun arrayfill(array: DoubleArray, value: Double, start: Int = 0, end: Int = array.size): Unit = array.fill(value, start, end)

public fun <T> arraycopy(src: Array<out T>, srcPos: Int, dst: Array<out T>, dstPos: Int, size: Int) {
    src.copyInto(dst as Array<T>, dstPos, srcPos, srcPos + size)
}

public fun arraycopy(src: BooleanArray, srcPos: Int, dst: BooleanArray, dstPos: Int, size: Int) {
    src.copyInto(dst, dstPos, srcPos, srcPos + size)
}

public fun arraycopy(src: LongArray, srcPos: Int, dst: LongArray, dstPos: Int, size: Int) {
    src.copyInto(dst, dstPos, srcPos, srcPos + size)
}

public fun arraycopy(src: ByteArray, srcPos: Int, dst: ByteArray, dstPos: Int, size: Int) {
    src.copyInto(dst, dstPos, srcPos, srcPos + size)
}

public fun arraycopy(src: ShortArray, srcPos: Int, dst: ShortArray, dstPos: Int, size: Int) {
    src.copyInto(dst, dstPos, srcPos, srcPos + size)
}

public fun arraycopy(src: CharArray, srcPos: Int, dst: CharArray, dstPos: Int, size: Int) {
    src.copyInto(dst, dstPos, srcPos, srcPos + size)
}

public fun arraycopy(src: IntArray, srcPos: Int, dst: IntArray, dstPos: Int, size: Int) {
    src.copyInto(dst, dstPos, srcPos, srcPos + size)
}

public fun arraycopy(src: FloatArray, srcPos: Int, dst: FloatArray, dstPos: Int, size: Int) {
    src.copyInto(dst, dstPos, srcPos, srcPos + size)
}

public fun arraycopy(src: DoubleArray, srcPos: Int, dst: DoubleArray, dstPos: Int, size: Int) {
    src.copyInto(dst, dstPos, srcPos, srcPos + size)
}

public fun <T> arraycopy(src: List<T>, srcPos: Int, dst: MutableList<T>, dstPos: Int, size: Int) {
    if (src === dst) error("Not supporting the same array")
    for (n in 0 until size) {
        dst[dstPos + n] = src[srcPos]
    }
}

public inline fun <T> arraycopy(size: Int, src: Any?, srcPos: Int, dst: Any?, dstPos: Int, setDst: (Int, T) -> Unit, getSrc: (Int) -> T) {
    val overlapping = src === dst && dstPos > srcPos
    if (overlapping) {
        var n = size
        while (--n >= 0) setDst(dstPos + n, getSrc(srcPos + n))
    } else {
        for (n in 0 until size) setDst(dstPos + n, getSrc(srcPos + n))
    }
}
