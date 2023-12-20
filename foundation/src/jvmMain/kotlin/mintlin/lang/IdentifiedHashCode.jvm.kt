package mintlin.lang

actual fun anyIdentifiedHashCode(obj: Any?): Int = System.identityHashCode(obj)
