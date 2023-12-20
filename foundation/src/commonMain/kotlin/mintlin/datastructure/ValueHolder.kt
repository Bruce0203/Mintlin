package mintlin.datastructure

sealed interface ValueHolder {
    val value: Any
    fun clone(): ValueHolder
}

interface LateInit

@JvmInline
value class ValueHolderImp(override val value: Any) : ValueHolder {
    override fun clone(): ValueHolder = this
}

class LazyValueHolder(private val provider: () -> Any) : ValueHolder, LateInit {
    private var valueOrNull: Any? = null
    override val value: Any
        get() {
            val value = valueOrNull
            return if (value === null) {
                val newValue = provider()
                valueOrNull = newValue
                newValue
            } else value
        }

    override fun clone(): ValueHolder {
        return LazyValueHolder(provider)
    }
}

class FactoryValueHolder(private val provider: () -> Any) : ValueHolder, LateInit {
    override val value: Any get() = provider()

    override fun clone(): ValueHolder = this
}
