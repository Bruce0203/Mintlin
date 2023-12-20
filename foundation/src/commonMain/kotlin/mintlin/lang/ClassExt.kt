package mintlin.lang

import kotlin.reflect.KClass

inline fun <reified T> classNameOf() = T::class.name

inline val KClass<*>.name get() = simpleName!!
