package mintlin.io.network

import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class JvmSocketChannel(
    private val socketChannel: SocketChannel,
) : SocketChannelState, SocketReadListener {
    private lateinit var socketReadListenerDelegate: SocketReadListener
    private lateinit var socketCloseListenerDelegate: SocketCloseListener

    override fun closeChannel() {
        socketCloseListenerDelegate.onSocketClosed()
        socketChannel.close()
    }

    private var socketClosed = false

    override val isOpen: Boolean get() = socketChannel.isOpen && !socketClosed

    override fun write(data: ByteArray) {
        if (socketClosed) return
        try {
            socketChannel.write(ByteBuffer.wrap(data))
        } catch (e: Throwable) {
            socketClosed = true
        }
    }

    override fun setSocketReadListener(listener: SocketReadListener) {
        socketReadListenerDelegate = listener
    }

    override fun setSocketCloseListener(listener: SocketCloseListener) {
        socketCloseListenerDelegate = listener
    }

    override fun onRead(data: ByteArray) {
        socketReadListenerDelegate.onRead(data)
    }

    fun closeConnection() {
        socketCloseListenerDelegate.onSocketClosed()
    }
}