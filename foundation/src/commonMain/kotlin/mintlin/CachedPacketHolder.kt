
package mintlin

import kotlinx.atomicfu.atomic
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

inline fun <reified T> cachedPacket(noinline factory: () -> T): CachedPacketHolder<T> =
    CachedPacketHolderImp(serializer<T>(), factory)

inline fun <reified T> lazyCachedPacket(noinline factory: () -> T): CachedPacketHolder<T> =
    LazyCachedPacketHolderImp(serializer<T>(), factory)

interface CachedPacketHolder<T> {
    val factory: () -> T
    val serializer: KSerializer<T>
    var value: T
    var binary: ByteArray
    var isOld: Boolean

    fun refresh(): CachedPacketHolder<T>
    fun scheduleRefresh(): CachedPacketHolder<T>
}

val EMPTY_BYTE_ARRAY = ByteArray(0)

abstract class AbstractCachedPacketHolder<T> : CachedPacketHolder<T> {
    override var binary: ByteArray by atomic(ByteArray(0))
    override var isOld: Boolean = false

    override fun refresh(): CachedPacketHolder<T> {
        value = factory()
        binary = EMPTY_BYTE_ARRAY
        return this
    }

    override fun scheduleRefresh(): CachedPacketHolder<T> {
        isOld = true
        binary = EMPTY_BYTE_ARRAY
        return this
    }
}

class CachedPacketHolderImp<T>(
    override val serializer: KSerializer<T>,
    override val factory: () -> T
) : AbstractCachedPacketHolder<T>() {
    private val atomic = atomic(factory())
    override var value get() = atomic.value ;set(value) {atomic.value = value}
}

class LazyCachedPacketHolderImp<T>(
    override val serializer: KSerializer<T>,
    override val factory: () -> T
) : AbstractCachedPacketHolder<T>() {
    private val atomic by lazy { atomic(factory()) }
    override var value get() = atomic.value ;set(value) {atomic.value = value}
}