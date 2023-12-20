package mintlin.io.network

interface SocketChannel {
    fun closeChannel()
    val isOpen: Boolean
}

interface SocketChannelState : SocketWriter, SocketChannel, SocketReadListenerRegistrar, SocketCloseListenerRegistrar

