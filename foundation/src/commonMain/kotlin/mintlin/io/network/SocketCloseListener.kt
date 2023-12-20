package mintlin.io.network

interface SocketCloseListener {
    fun onSocketClosed()
}

interface SocketCloseListenerRegistrar {
    fun setSocketCloseListener(listener: SocketCloseListener)
}
