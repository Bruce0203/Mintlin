package mintlin.lang

fun Any?.identityHashCode(): Int = anyIdentifiedHashCode(this)

expect fun anyIdentifiedHashCode(obj: Any?): Int

