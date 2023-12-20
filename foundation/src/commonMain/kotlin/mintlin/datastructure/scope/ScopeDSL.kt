package mintlin.datastructure.scope

import kotlin.reflect.KClass

interface ScopeDSL : Scope {
    fun <T> declare(key: KClass<*>, value: T)

    fun <T> single(key: KClass<*>, single: () -> T): ScopeValueHolder<T>

    fun <T> factory(key: KClass<*>, factory: () -> T): ScopeValueHolder<T>

    infix fun <T : T2, T2 : Any> ScopeValueHolder<T>.bind(key: KClass<T2>)

    @Suppress("unchecked_cast")
    infix fun <T : Any> ScopeValueHolder<T>.binds(keys: Array<KClass<*>>) = keys.forEach { this bind (it as KClass<T>) }
}

inline fun <reified T : Any> Scope.get(): T = get(T::class)

inline fun <reified T : Any> Scope.getOrNull(): T? = getOrNull(T::class)

inline fun <reified T : Any> ScopeDSL.declare(value: T) = declare(T::class, value)

inline fun <reified T : Any> ScopeDSL.single(noinline lazy: () -> T) = single(T::class, lazy)

inline fun <reified T : Any> ScopeDSL.factory(noinline factory: () -> T) = factory(T::class, factory)
