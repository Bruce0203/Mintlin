package mintlin.datastructure.scope

import mintlin.datastructure.*
import mintlin.internal.MyInternalAPI
import mintlin.lang.fastCastTo
import kotlin.reflect.KClass

interface Scope {
    fun <T> get(key: ScopeKey): T
    fun <T> getOrNull(key: ScopeKey): T?
    val isScopeClosed: Boolean
    fun closeScope()
    @MyInternalAPI
    val linkers: FastArrayList<Scope>
}

typealias ScopeKey = KClass<*>

@Suppress("unused")
@JvmInline
value class ScopeValueHolder<T>(internal val valueHolder: ValueHolder)

@OptIn(MyInternalAPI::class)
class ScopeImp(private val links: FastArrayList<Scope> = FastArrayList()) : Scope, ScopeDSL {
    override var isScopeClosed: Boolean = false
    @MyInternalAPI
    override val linkers = FastArrayList<Scope>()
    init { links.forEach { it.linkers.add(this) } }

    private val map = FastIdentityMapImp<ScopeKey, Any?>(FastArrayList<MutableEntry<ScopeKey, ValueHolder>>(48).also {
        it.array[0] = EntryImp(Scope::class, ValueHolderImp(this))
        it.size = 1
    })

    override fun <T> get(key: ScopeKey): T {
        try {
            return map.getOrNull(key)?.fastCastTo()
                ?: getFromLinks(key)
                ?: throw ScopeCreationException(key)
        } catch (ex: ScopeCreationException) {
            throw ScopeCreationException(key, *ex.scopeKeys)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getOrNull(key: ScopeKey): T? {
        return map.getOrNull(key) as T? ?: getFromLinks(key)
    }

    override fun closeScope() {
        if (isScopeClosed) return
        isScopeClosed = true
        links.forEach {
            it.linkers.remove(this)
        }
        links.clear()
        map.clear()
        linkers.forEach {
            it.closeScope()
        }
    }

    private fun <T> getFromLinks(key: ScopeKey): T? {
        return links.findOrNull { it.getOrNull(key) }
    }

    override fun <T> declare(key: ScopeKey, value: T) = map.put(key, value)

    override fun <T> single(key: ScopeKey, single: () -> T): ScopeValueHolder<T> {
        return ScopeValueHolder(map.lazyPut(key, single))
    }

    override fun <T> factory(key: ScopeKey, factory: () -> T): ScopeValueHolder<T> {
        return ScopeValueHolder(map.putFactory(key, factory))
    }

    override fun <T : T2, T2 : Any> ScopeValueHolder<T>.bind(key: KClass<T2>) {
        map.putValueHolder(key, valueHolder)
    }
}

class ScopeCreationException(vararg val scopeKeys: ScopeKey) : Exception(run {
    scopeKeys.foldIndexed("Cannot Create:\n") { i, acc, key ->
        acc + "    " + "  ".repeat(i) + key +"\n"
    }
})