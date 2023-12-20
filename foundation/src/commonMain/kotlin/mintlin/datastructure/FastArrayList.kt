@file:Suppress("unused", "ReplaceManualRangeWithIndicesCalls")

package mintlin.datastructure

import mintlin.lang.identityHashCode

/**
 * Not thread safe
 * no index supplement
 * null generic not supported
 * generic primitive not supported
 */
class FastArrayList<T : Any> {
    var size = 0
    //TODO OptIn
    var array: Array<Any?>

    constructor() {
        array = emptyNullableAnyArray
    }

    constructor(capacity: Int) {
        array = arrayOfNulls(capacity)
    }

    constructor(size: Int, init: (index: Int) -> T) {
        array = Array(size) { init(it) }
        this.size = size
    }

    constructor(newArray: Array<out T>) {
        @Suppress("UNCHECKED_CAST")
        this.array = newArray as Array<Any?>
        this.size = array.size
    }

    fun allocateForNext(): Int {
        val newIndex = size
        val currentArr = array
        if (currentArr.size != ++size) {
            val newArr = arrayOfNulls<Any>(size)
            var newArrI = 0
            for (i in 0 until newIndex) {
                val value = currentArr[i]
                if (value !== null) {
                    newArr[newArrI++] = value
                }
            }
            size = newArrI + 1
            array = newArr
            return newArrI
        }
        return newIndex
    }

    fun allocateForNext(sizeIncrement: Int): Int {
        val newIndex = size
        val currentArr = array
        size += sizeIncrement
        if (currentArr.size != size) {
            val newArr = arrayOfNulls<Any>(size)
            var newArrI = 0
            for (i in 0 until newIndex) {
                val value = currentArr[i]
                if (value !== null) {
                    newArr[newArrI++] = value
                }
            }
            size = newArrI + 1
            array = newArr
            return newArrI
        }
        return newIndex
    }

    fun add(value: T) {
        val newIndex = allocateForNext()
        array[newIndex] = value
    }

    fun add(init: (index: Int) -> T): T {
        val newIndex = allocateForNext()
        val newListener = init.invoke(newIndex)
        array[newIndex] = newListener
        return newListener
    }

    fun remove(value: T): Boolean {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val otherValue = currentArray[i]
            if (value.identityHashCode() == otherValue.identityHashCode()) {
                currentArray[i] = null
                --size
                return true
            }
        }
        return false
    }

    fun removeAll(value: T) {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val otherValue = currentArray[i]
            if (value.identityHashCode() == otherValue.identityHashCode()) {
                currentArray[i] = null
                --size
            }
        }
    }

    inline fun forEach(consumer: (T) -> Unit) {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val value = currentArray[i]
            if (value === null) continue
            @Suppress("UNCHECKED_CAST")
            consumer(value as T)
        }
    }

    fun forEachIndexed(block: (index: Int, value: T) -> Unit) {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val value = currentArray[i]
            if (value === null) continue
            @Suppress("UNCHECKED_CAST")
            block(i, value as T)
        }
    }

    inline fun forEachReverse(consumer: (T) -> Unit) {
        val currentArray = array
        var i = size
        while (--i >= 0) {
            val value = currentArray[i]
            if (value === null) {
                continue
            }
            @Suppress("UNCHECKED_CAST")
            consumer(value as T)
        }
    }

    fun forEachReverseIndexed(block: (index: Int, value: T) -> Unit) {
        val currentArray = array
        var i = size
        while (--i >= 0) {
            val value = currentArray[i]
            if (value === null) {
                i--
                continue
            }
            @Suppress("UNCHECKED_CAST")
            block(i, value as T)
            i--
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun first(block: (value: T) -> Boolean): T {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val value = currentArray[i]
            if (value === null) continue
            value as T
            if (block(value)) {
                return value
            }
        }
        throw NoSuchElementException()
    }

    @Suppress("UNCHECKED_CAST")
    fun firstOrNull(block: (value: T) -> Boolean): T? {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val value = currentArray[i]
            if (value === null) continue
            value as T
            if (block(value)) {
                return value
            }
        }
        return null
    }

    fun firstIndexed(block: (index: Int, value: T) -> Boolean): T {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val value = currentArray[i]
            if (value === null) continue
            @Suppress("UNCHECKED_CAST")
            if (block(i, value as T)) {
                return value
            }
        }
        throw NoSuchElementException()
    }

    fun firstIndexedOrNull(block: (index: Int, value: T) -> Boolean): T? {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val value = currentArray[i]
            if (value === null) continue
            @Suppress("UNCHECKED_CAST")
            if (block(i, value as T)) {
                return value
            }
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    fun <R> find(block: (value: T) -> R?): R {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val value = currentArray[i]
            if (value === null) continue
            val found = block(value as T)
            if (found !== null) {
                return found
            }
        }
        throw NoSuchElementException()
    }

    @Suppress("UNCHECKED_CAST")
    fun <R> findOrNull(block: (value: T) -> R?): R? {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val value = currentArray[i]
            if (value === null) continue
            val found = block(value as T)
            if (found !== null) {
                return found
            }
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    fun <R> findIndexedOrNull(block: (index: Int, value: T) -> R?): R? {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val value = currentArray[i]
            if (value === null) continue
            val found = block(i, value as T)
            if (found !== null) {
                return found
            }
        }
        return null
    }

    fun contains(value: T): Boolean {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val otherValue = currentArray[i]
            if (otherValue === null) continue
            if (value == otherValue) return true
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    fun all(block: (value: T) -> Boolean): Boolean {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val value = currentArray[i]
            if (value === null) continue
            value as T
            if (!block(value)) {
                return false
            }
        }
        return true
    }

    @Suppress("UNCHECKED_CAST")
    fun none(block: (value: T) -> Boolean): Boolean {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val value = currentArray[i]
            if (value === null) continue
            if (block(value as T)) {
                return false
            }
        }
        return true
    }

    @Suppress("UNCHECKED_CAST")
    fun reversedRemoveIf(block: (value: T) -> Boolean) {
        val currentArray = array
        var i = currentArray.size
        while (--i >= 0) {
            val value = currentArray[i]
            if (value !== null && block(value as T)) {
                currentArray[i] = null
                --size
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun removeFirst(block: (value: T) -> Boolean): T {
        val currentArray = array
        var i = currentArray.size
        while (--i >= 0) {
            val value = currentArray[i]
            if (value !== null && block(value as T)) {
                currentArray[i] = null
                --size
                return value
            }
        }
        throw NoSuchElementException()
    }

    @Suppress("UNCHECKED_CAST")
    fun removeFirstOrNull(block: (value: T) -> Boolean): T? {
        val currentArray = array
        var i = currentArray.size
        while (--i >= 0) {
            val value = currentArray[i]
            if (value !== null && block(value as T)) {
                currentArray[i] = null
                --size
                return value
            }
        }
        return null
    }

    fun removeLast(): T {
        val currentArray = array
        var i = currentArray.size
        while (--i >= 0) {
            val value = currentArray[i]
            if (value !== null) {
                currentArray[i] = null
                --size
                @Suppress("UNCHECKED_CAST")
                return value as T
            }
        }
        throw NoSuchElementException()
    }

    fun removeLastOrNull(): T? {
        val currentArray = array
        var i = currentArray.size
        while (--i >= 0) {
            val value = currentArray[i]
            if (value !== null) {
                currentArray[i] = null
                --size
                @Suppress("UNCHECKED_CAST")
                return value as T
            }
        }
        return null
    }

    fun removeFirst(): T {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val value = currentArray[i]
            if (value !== null) {
                currentArray[i] = null
                @Suppress("UNCHECKED_CAST")
                return value as T
            }
        }
        throw NoSuchElementException()
    }

    fun removeFirstOrNull(): T? {
        val currentArray = array
        for (i in 0 until currentArray.size) {
            val value = currentArray[i]
            if (value !== null) {
                currentArray[i] = null
                @Suppress("UNCHECKED_CAST")
                return value as T
            }
        }
        return null
    }

    fun last(): T {
        val currentArray = array
        var i = size - 1
        while (i != 0) {
            val value = currentArray[i]
            if (value !== null) {
                @Suppress("UNCHECKED_CAST")
                return value as T
            }
            i--
        }
        throw NoSuchElementException()
    }

    fun replaceLast(value: T) {
        val currentArray = array
        var i = size
        while (--i >= 0) {
            val otherValue = currentArray[i]
            if (otherValue !== null) {
                currentArray[i] = value
                return
            }
        }
        throw NoSuchElementException()
    }

    fun replaceLast(block: (origin: T) -> T): T {
        val currentArray = array
        var i = size
        while (--i >= 0) {
            val otherValue = currentArray[i]
            if (otherValue !== null) {
                @Suppress("UNCHECKED_CAST")
                otherValue as T
                val newValue = block(otherValue)
                currentArray[i] = newValue
                return newValue
            }
        }
        throw NoSuchElementException()
    }

    fun first(): T {
        var i = 0
        val currentArray = array
        while (i < size) {
            val value = currentArray[i]
            if (value !== null) {
                @Suppress("UNCHECKED_CAST")
                return value as T
            } else i++
        }
        throw NoSuchElementException()
    }

    fun addBulk(fromArraySize: Int, init: (Int) -> T) {
        val newIndex = size
        val currentArr = array
        size += fromArraySize
        val newSize = size
        val newArr = if (currentArr.size == newSize) currentArr else {
            val newArr = arrayOfNulls<Any>(newSize)
            array = newArr
            newArr
        }
        var newArrI = 0
        for (i in 0 until newIndex) {
            val value = currentArr[i]
            if (value !== null) {
                newArr[newArrI++] = value
            }
        }
        for (i in 0 until fromArraySize) {
            val value = init(i)
            newArr[newArrI++] = value
        }
    }

    fun clear() {
        array = emptyNullableAnyArray
        size = 0
    }

    fun copy(): FastArrayList<T> {
        @Suppress("UNCHECKED_CAST")
        return FastArrayList(array.copyOf() as Array<T>)
    }

    @Suppress("UNCHECKED_CAST")
    fun toArray(): Array<Any> {
        val currentArray = array
        val newArr = arrayOfNulls<Any>(size)
        var newArrI = 0
        for (i in array.indices) {
            val value = currentArray[i]
            if (value === null) continue
            newArr[newArrI++] = value
        }
        array = newArr
        return newArr as Array<Any>
    }

    inline fun <reified R : Any> map(crossinline block: (T) -> R): FastArrayList<R> {
        @Suppress("UNCHECKED_CAST")
        val currentArray = array as Array<T>
        var i = 0
        return FastArrayList(size) {
            var value: T?
            do {
                value = currentArray[i++]
            } while (value === null)
            block(value)
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("[")
        var last = size
        forEach { value ->
            sb.append(value.toString())
            if (0 != --last) sb.append(", ")
        }
        sb.append("]")
        return sb.toString()
    }

    companion object {
        private val emptyNullableAnyArray = emptyArray<Any?>()
    }
}

fun <T : Any> List<T>.toFast(): FastArrayList<T> = FastArrayList<T>(size).also { list ->
    forEach { list.add(it) }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> FastArrayList<T>.toTypedArray() = toArray() as Array<T>
