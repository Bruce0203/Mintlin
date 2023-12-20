package mintlin.io.network

interface SocketReadListener {
    fun onRead(data: ByteArray)
}

interface SocketReadListenerRegistrar {
    fun setSocketReadListener(listener: SocketReadListener)
}
