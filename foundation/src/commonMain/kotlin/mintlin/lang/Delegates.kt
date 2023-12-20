package mintlin.lang

import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Delegate<T> : ReadOnlyProperty<Any?, T> {
    val value: T

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }
}

interface MutableDelegate<T> : ReadWriteProperty<Any?, T>, Delegate<T> {
    override var value: T

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return this.value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

interface NullableDelegate<T> : ReadOnlyProperty<Any?, T?> {
    val value: T?

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return value
    }
}

interface NullableMutableDelegate<T> : ReadWriteProperty<Any?, T?>, NullableDelegate<T?> {
    override var value: T?

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return this.value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        this.value = value
    }
}

class DelegateImp<T>(override val value: T) : Delegate<T>

class MutableDelegateImp<T>(override var value: T) : MutableDelegate<T>

class NullableDelegateImp<T>(override val value: T) : NullableDelegate<T>

class NullableMutableDelegateImp<T>(override var value: T?) : NullableMutableDelegate<T>

fun <T> Delegates.nullable(initialValue: T? = null) = object : MutableDelegate<T?> {
    override var value: T? = initialValue

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        this.value = value
    }
}

fun <T> Delegates.lateInit() = object : MutableDelegate<T> {
    private var internalValue: T? = null
    override var value: T get() = internalValue!!
        set(value) { internalValue = value }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        internalValue ?: throw NullPointerException("property $property is not initialized yet")

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.internalValue = value
    }
}

fun <T> Delegates.notNull(initialValue: T) = object : MutableDelegate<T> {
    override var value: T = initialValue

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}


fun <T> Delegates.notNull(func: () -> T) = object : ReadOnlyProperty<Any?, T> {
    val value: T get() = func()

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}
