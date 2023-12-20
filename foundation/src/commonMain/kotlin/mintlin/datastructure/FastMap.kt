@file:OptIn(MyInternalAPI::class)

package mintlin.datastructure

import mintlin.internal.MyInternalAPI
import mintlin.lang.identityHashCode

//todo remove valueHOlder than, use another data structure in scope impl
interface FastMap<K, V> {
    @MyInternalAPI
    val entries: FastArrayList<MutableEntry<K, ValueHolder>>
    val size: Int
    fun put(key: K, value: V)
    fun getOrNull(key: K): V?
    fun remove(key: K): Boolean
    fun lazyPut(key: K, lazy: () -> V): ValueHolder
    fun putFactory(key: K, factory: () -> V): ValueHolder
    fun getNotNull(key: K): V
    fun getOrPut(key: K, provider: () -> V): V
    fun forEachValue(consumer: (V) -> Unit)
    fun forEachKey(consumer: (K) -> Unit)
    fun forEach(consumer: (Entry<K, V>) -> Unit)
    fun containsKey(key: K): Boolean
    fun copy(): FastMap<K, V>
    fun putAllFrom(map: FastMap<K, V>)
    fun putValueHolder(key: K, valueHolder: ValueHolder)
    fun mapValue(block: (Entry<K, V>) -> V): FastMap<K, V>
    fun getKeyByValue(value: V): K
    fun getKeyByValueOrNull(value: V): K?
    fun <R> map(block: (Entry<K, V>) -> R): FastMap<K, R>
    fun clear()
    fun <R> findOrNull(block: (Entry<K, V>) -> R?): R?
    fun <R> find(block: (Entry<K, V>) -> R?): R
    fun firstValue(block: (Entry<K, V>) -> Boolean): V
    fun firstValueOrNull(block: (Entry<K, V>) -> Boolean): V?
    operator fun set(key: K, value: V) = put(key, value)
    operator fun get(key: K): V? = getOrNull(key)
}

interface Entry<K, V> {
    val key: K
    val value: V
}

interface MutableEntry<K, V> : Entry<K, V> {
    override val key: K
    override var value: V
}

data class EntryImp<K, V>(override val key: K, override var value: V) : MutableEntry<K, V>

fun <K, V> FastIdentityMap(): FastMap<K, V> = FastIdentityMapImp()

fun <K, V> FastIdentityMap(capacity: Int): FastMap<K, V> = FastIdentityMapImp(capacity = capacity)

fun <K, V> FastIdentityMap(size: Int, init: (Int) -> Entry<K, V>?): FastMap<K, V> {
    val list = FastArrayList<MutableEntry<K, ValueHolder>>(size)
    for (i in 0 until size) {
        val entry = init(i)
        if (entry !== null) {
            list.array[i] = (EntryImp(entry.key, ValueHolderImp(entry.value as Any)))
        }
    }
    list.size = size
    return FastIdentityMapImp(list)
}

class FastIdentityMapImp<K, V>(
    override val entries: FastArrayList<MutableEntry<K, ValueHolder>>
) : AbstractFastMap<K, V>(entries) {
    constructor(capacity: Int) : this(FastArrayList(capacity = capacity))

    constructor() : this(FastArrayList())

    override fun compareBetween(value: Any, other: Any): Boolean {
        return value.identityHashCode() == other.identityHashCode()
    }
}

class FastMapImp<K, V>(
    override val entries: FastArrayList<MutableEntry<K, ValueHolder>>
) : AbstractFastMap<K, V>(entries) {
    constructor(capacity: Int) : this(FastArrayList(capacity = capacity))

    constructor() : this(FastArrayList())

    override fun compareBetween(value: Any, other: Any): Boolean {
        return value == other
    }
}

abstract class AbstractFastMap<K, V>(
    @OptIn(MyInternalAPI::class)
    override val entries: FastArrayList<MutableEntry<K, ValueHolder>>
) : FastMap<K, V> {
    override val size: Int get() = entries.size

    abstract fun compareBetween(value: Any, other: Any): Boolean

    @Suppress("UNCHECKED_CAST")
    override fun <R> map(block: (Entry<K, V>) -> R): FastMap<K, R> {
        val currentEntries = entries
        val currentEntriesArray = currentEntries.array
        return FastIdentityMapImp<K, V>(FastArrayList(Array(currentEntries.size) { i ->
            val currentEntry = currentEntriesArray[i] as Entry<K, ValueHolder>
            val key = currentEntry.key
            val entry = EntryImp(key, currentEntry.value.value as V)
            EntryImp(key, ValueHolderImp(block(entry) as Any))
        })) as FastMap<K, R>
    }

    @Suppress("UNCHECKED_CAST")
    override fun mapValue(block: (Entry<K, V>) -> V): FastMap<K, V> {
        val currentEntries = entries
        val currentEntriesArray = currentEntries.array as Array<Entry<K, ValueHolder>>
        return FastIdentityMapImp(FastArrayList(Array(currentEntries.size) { i ->
            val currentEntry = currentEntriesArray[i]
            val key = currentEntry.key
            val entry = EntryImp(key, currentEntry.value.value as V)
            EntryImp(key, ValueHolderImp(block(entry) as Any))
        }))
    }

    override fun getKeyByValueOrNull(value: V): K? {
        return entries.findOrNull { entry ->
            if (entry.value.identityHashCode() == value.identityHashCode()) {
                entry.key
            } else null
        }
    }

    override fun getKeyByValue(value: V): K {
        return entries.findOrNull { entry ->
            if (entry.value.identityHashCode() == value.identityHashCode()) {
                entry.key
            } else null
        }?: throw NoSuchElementException()
    }

    override fun getNotNull(key: K): V {
        return entries.findOrNull { entry ->
            if (key.identityHashCode() == entry.key.identityHashCode()) {
                @Suppress("UNCHECKED_CAST")
                entry.value.value as V
            } else null
        } ?: throw NoSuchElementException()
    }

    override fun getOrNull(key: K): V? {
        return entries.findOrNull { entry ->
            if (key.identityHashCode() == entry.key.identityHashCode()) {
                @Suppress("UNCHECKED_CAST")
                entry.value.value as V
            } else null
        }
    }

    override fun lazyPut(key: K, lazy: () -> V): ValueHolder {
        val currentEntries = entries
        @Suppress("UNCHECKED_CAST")
        val valueHolder = LazyValueHolder(lazy as () -> Any)
        currentEntries.findOrNull { entry ->
            if (key.identityHashCode() == entry.key.identityHashCode()) {
                entry.value = valueHolder
            } else null
        }?: currentEntries.add(EntryImp(key, valueHolder))
        return valueHolder
    }

    override fun putFactory(key: K, factory: () -> V): ValueHolder {
        val currentEntries = entries
        @Suppress("UNCHECKED_CAST")
        val valueHolder = FactoryValueHolder(factory as () -> Any)
        currentEntries.findOrNull { entry ->
            if (key.identityHashCode() == entry.key.identityHashCode()) {
                entry.value = valueHolder
            } else null
        }?: currentEntries.add(EntryImp(key, valueHolder))
        return valueHolder
    }

    override fun put(key: K, value: V) {
        val currentEntries = entries
        val valueHolder = ValueHolderImp(value as Any)
        currentEntries.findOrNull { entry ->
            if (key.identityHashCode() == entry.key.identityHashCode()) {
                entry.value = valueHolder
            } else null
        }
        currentEntries.add(EntryImp(key, valueHolder))
    }

    override fun getOrPut(key: K, provider: () -> V): V {
        val currentEntries = entries
        val identityHashCode = key.identityHashCode()
        val entryOrNull = currentEntries.firstOrNull { entry ->
            entry.key.identityHashCode() == identityHashCode
        }
        @Suppress("UNCHECKED_CAST")
        return if (entryOrNull === null) {
            val value = provider()
            currentEntries.add(EntryImp(key, ValueHolderImp(value as Any)))
            value
        } else entryOrNull.value.value as V
    }

    override fun forEachValue(consumer: (V) -> Unit) {
        entries.forEach {
            val value = it.value
            if (value !is LateInit) {
                @Suppress("UNCHECKED_CAST")
                consumer(value.value as V)
            }
        }
    }

    override fun forEachKey(consumer: (K) -> Unit) {
        entries.forEach {
            consumer(it.key)
        }
    }

    override fun forEach(consumer: (Entry<K, V>) -> Unit) {
        entries.forEach {
            val value = it.value
            if (value !is LateInit) {
                @Suppress("UNCHECKED_CAST")
                consumer(EntryImp(it.key, value.value as V))
            }
        }
    }

    override fun containsKey(key: K): Boolean {
        return entries.firstOrNull { it.key.identityHashCode() == key.identityHashCode() } !== null
    }

    override fun putValueHolder(key: K, valueHolder: ValueHolder) {
        val currentEntries = entries
        currentEntries.findOrNull { entry ->
            if (key.identityHashCode() == entry.key.identityHashCode()) {
                entry.value = valueHolder
            } else null
        }?: currentEntries.add(EntryImp(key, valueHolder))
    }

    override fun remove(key: K): Boolean {
        return entries.removeFirstOrNull { entry -> key.identityHashCode() == entry.key.identityHashCode() } !== null
    }

    override fun copy(): FastMap<K, V> {
        @Suppress("UNCHECKED_CAST")
        val currentEntries = entries.array as Array<MutableEntry<K, ValueHolder>>
        val size = currentEntries.size
        val newList = FastArrayList(Array<MutableEntry<K, ValueHolder>>(size) { i ->
            val entry = currentEntries[i]
            EntryImp(entry.key, entry.value.clone())
        })
        return FastIdentityMapImp(newList)
    }

    override fun putAllFrom(map: FastMap<K, V>) {
        val fromEntries = map.entries
        @Suppress("UNCHECKED_CAST")
        val fromEntriesArray = fromEntries.array as Array<Entry<K, ValueHolder>>
        entries.addBulk(fromEntries.size) { i ->
            val entry = fromEntriesArray[i]
            EntryImp(entry.key, entry.value.clone())
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        var i = entries.size
        sb.append("[")
        forEach {
            sb.append("${it.key}=${it.value}")
            if (--i != 0) sb.append(", ")
        }
        sb.append("]")
        return sb.toString()
    }

    override fun <R> findOrNull(block: (Entry<K, V>) -> R?): R? {
        return entries.findOrNull { entry ->
            val value = entry.value
            if (value !is LateInit) {
                @Suppress("UNCHECKED_CAST")
                block(EntryImp(entry.key, value.value as V))
            } else null
        }
    }

    override fun <R> find(block: (Entry<K, V>) -> R?): R {
        return entries.find { entry ->
            val value = entry.value
            if (value !is LateInit) {
                @Suppress("UNCHECKED_CAST")
                block(EntryImp(entry.key, value.value as V))
            } else null
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun firstValue(block: (Entry<K, V>) -> Boolean): V {
        return entries.first { entry ->
            val value = entry.value
            if (value !is LateInit) {
                block(EntryImp(entry.key, value.value as V))
            } else false
        }.value.value as V
    }

    @Suppress("UNCHECKED_CAST")
    override fun firstValueOrNull(block: (Entry<K, V>) -> Boolean): V? {
        return entries.findOrNull { entry ->
            val valueHolder = entry.value
            if (valueHolder !is LateInit) {
                val value = valueHolder.value as V
                if (block(EntryImp(entry.key, value))) {
                    value
                } else null
            } else null
        }
    }

    override fun clear() {
        entries.clear()
    }
}

fun <T, V> Map<T, V>.toFast(): FastMap<T, V> = FastIdentityMapImp<T, V>().also { map ->
    forEach { (k, v) ->
        map.put(k, v)
    }
}

fun <T, V> List<Pair<T, V>>.toFast(): FastMap<T, V> = FastIdentityMapImp<T, V>().also { map ->
    forEach { (k, v) ->
        map.put(k, v)
    }
}