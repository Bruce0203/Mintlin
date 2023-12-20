package mintlin.io.network

import java.net.InetSocketAddress
import java.nio.channels.SelectionKey


actual val clientSelectorFactory: ClientSelectorFactory = object : ClientSelectorFactory {
    override fun createClientSelector(listener: SocketListener): ClientSelector {
        return JvmClientSelector(listener)
    }
}

class JvmClientSelector(listener: SocketListener) : ClientSelector {
    private val selector = JvmSelector(listener)

    override fun createClient(host: String, port: Int): SocketChannel {
        val socketChannel = java.nio.channels.SocketChannel.open()
        socketChannel.configureBlocking(false)
        socketChannel.connect(InetSocketAddress(host, port))
        socketChannel.register(selector.socketSelector, SelectionKey.OP_CONNECT)
        return JvmSocketChannel(socketChannel)
    }

    override fun select() = selector.select()
}