package mintlin.lang

open class Init(@Suppress("UNUSED_PARAMETER") any: Any) {
    constructor(block: () -> Unit) : this(block())
}
