package mintlin.minecraft.server.server.factory

import kotlinx.atomicfu.atomic
import mintlin.lang.notNull
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty

open class AbstractIdFactory : ReadOnlyProperty<Any?, Int> by run({
    val nextId = atomic(1)
    Delegates.notNull { nextId.getAndAdd(1) }
})