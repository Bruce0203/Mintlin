package mintlin.minecraft.network

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class CompressionState : ReadWriteProperty<Any?, Int> {
    private var value = COMPRESSION_DISABLED

    val isCompressionEnabled: Boolean get() = COMPRESSION_DISABLED != value

    override fun getValue(thisRef: Any?, property: KProperty<*>): Int = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        this.value = value
    }

    companion object {
        const val COMPRESSION_DISABLED = -1
    }
}