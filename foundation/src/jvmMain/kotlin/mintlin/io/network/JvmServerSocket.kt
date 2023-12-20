package mintlin.io.network

import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.ServerSocketChannel

fun createSocketServer(host: String = "0.0.0.0", port: Int, listener: SocketListener): Selector =
    socketServerFactory.bind(host, port, listener)

actual val socketServerFactory: SocketServerFactory = SocketServerFactory { host, port, listener ->
    val socketSelector = JvmSelector(listener)
    ServerSocketChannel.open().apply {
        configureBlocking(false)
        socket().bind(InetSocketAddress(host, port))
        register(socketSelector.socketSelector, SelectionKey.OP_ACCEPT)
    }
    socketSelector
}
