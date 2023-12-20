package mintlin.format

interface NamedTagEncoder {
    val currentNamedTag: String
    val currentNamedTagOrNull: String?

    fun encodeNamedValue(tag: String, value: Any)
}
