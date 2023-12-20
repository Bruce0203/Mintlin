package mintlin.io.network

expect val socketServerFactory: SocketServerFactory

fun interface SocketServerFactory {
    fun bind(host: String, port: Int, listener: SocketListener): Selector
}

fun interface SocketListener {
    fun accept(channel: SocketChannelState)
}
