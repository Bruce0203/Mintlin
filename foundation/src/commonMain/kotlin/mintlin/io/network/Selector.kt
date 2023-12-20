package mintlin.io.network

/**
 * Selector is Socket connection pool
 */
interface Selector {
    fun select()
}

fun Selector.run() {
    while(true) {
        select()
    }
}