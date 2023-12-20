package mintlin.datastructure.eventbus

import mintlin.datastructure.FastArrayList
import mintlin.datastructure.FastIdentityMap
import mintlin.datastructure.scope.Scope
import kotlin.reflect.KClass

typealias ListenerInvocation<T> = EventContext.(T) -> Unit

interface EventContext {
    var isCancelled: Boolean
    fun cancelEvent()
    fun closeListener()
}

class EventContextImp(internal var currentListener: Listener<Any>? = null) : EventContext {
    override var isCancelled: Boolean = false

    override fun cancelEvent() { isCancelled = true }

    override fun closeListener() { currentListener?.closeListener() }
}

class Listener<T>(
    private var registrar: Scope,
    internal val priority: Priority,
    private var listener: ListenerInvocation<T>?,
    private val isIgnoringCancelled: Boolean,
) {
    var closed = false

    fun listen(event: T, eventContext: EventContextImp) {
        if (registrar.isScopeClosed || closed) return
        if (!eventContext.isCancelled || isIgnoringCancelled) {
            eventContext.currentListener = null
            listener!!(eventContext, event)
        }
    }

    fun closeListener() {
        closed = true
        listener = null
    }
}

class ListenerList {
    private var listeners = FastArrayList<Listener<Any>>()

    fun addListener(listener: Listener<Any>): Listener<Any> {
        if (listeners.size == 0) {
            listeners.add(listener)
            return listener
        }
        var i = 0
        val currentArray = listeners.array
        val currentArraySize = currentArray.size
        val newArraySize = listeners.size + 1
        val newArray = arrayOfNulls<Any?>(newArraySize)
        var newArrayIndex = 0
        var nulls = 0
        while (i < currentArraySize) {
            val otherValue = currentArray[i]
            if (otherValue === null) continue
            val otherListener = otherValue as Listener<*>
            if (listener.priority.ordinal >= otherListener.priority.ordinal) {
                break
            }
            i++
            newArray[newArrayIndex++] = if (otherListener.closed) {
                nulls++
                null
            } else otherListener
        }
        newArray[newArrayIndex++] = listener
        while (i < currentArraySize) {
            val otherValue = currentArray[i++]
            if (otherValue === null) continue
            val otherListener = otherValue as Listener<*>
            newArray[newArrayIndex++] =  if (otherListener.closed) {
                nulls++
                null
            } else otherListener
        }
        listeners.array = newArray
        listeners.size = newArraySize - nulls
        return listener
    }

    fun dispatch(event: Any, eventContext: EventContextImp) {
        listeners.forEachReverse {
            it.listen(event, eventContext)
        }
    }
}

@Suppress("unused")
enum class Priority { LOWEST, LOW, NORMAL, HIGH, HIGHEST, MONITOR; }

@Suppress("unused")
class ListenerRegistrar(private val eventBus: EventBusImp) {
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> EventBus.onEvent(
        clazz: KClass<T>, listener: ListenerInvocation<T>,
        priority: Priority = defaultPriority, isIgnoringCancelled: Boolean = false,
    ) = getListeners(clazz).addListener(Listener(
        registrar = eventBus.scope, priority = priority,
        isIgnoringCancelled = isIgnoringCancelled, listener = listener
    ) as Listener<Any>)

    inline fun <reified T : Any> EventBus.onEvent(
        ignoreCancelled: Boolean, noinline listener: ListenerInvocation<T>
    ) = onEvent(T::class, listener, defaultPriority, isIgnoringCancelled = ignoreCancelled)

    inline fun <reified T : Any> EventBus.onEvent(
        priority: Priority = defaultPriority, ignoreCancelled: Boolean = false,
        noinline listener: ListenerInvocation<T>
    ) = onEvent(T::class, listener, priority, isIgnoringCancelled = ignoreCancelled)

    inline fun <reified T : Any> EventBus.onEvent(
        noinline listener: ListenerInvocation<T>
    ) = onEvent(T::class, listener, priority = defaultPriority, isIgnoringCancelled = false)

    companion object {
        val defaultPriority = Priority.NORMAL
    }
}

interface EventBus {
    val scope: Scope
    fun dispatch(event: Any)
    fun listeners(block: ListenerRegistrar.() -> Unit)
    fun getListeners(key: KClass<*>): ListenerList
}

class EventBusImp(override val scope: Scope) : EventBus {
    private val listeners = FastIdentityMap<KClass<*>, ListenerList>()

    override fun getListeners(key: KClass<*>): ListenerList = listeners.getOrPut(key) { ListenerList() }

    override fun dispatch(event: Any) = getListeners(event::class).dispatch(event, EventContextImp())

    override fun listeners(block: ListenerRegistrar.() -> Unit) = ListenerRegistrar(this).run(block)
}
