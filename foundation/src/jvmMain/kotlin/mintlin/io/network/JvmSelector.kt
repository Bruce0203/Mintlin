package mintlin.io.network

import kotlinx.io.readByteArray
import mintlin.datastructure.FastIdentityMap
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.channels.spi.SelectorProvider

class JvmSelector(private var socketListener: SocketListener) : mintlin.io.network.Selector {
    val socketSelector: Selector = SelectorProvider.provider().openSelector()
    private val socketMap = FastIdentityMap<SocketChannel, JvmSocketChannel>()
    private val readBuffer: ByteBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE)
    
    override fun select() {
        try {
            socketSelector.selectNow { key ->
                if (!key.isValid) {
                    socketMap.remove(key.channel() as SocketChannel)
                    return@selectNow
                }
                else if (key.isAcceptable) accept(key)
                else if (key.isReadable) read(key)
                else if (key.isConnectable) connect(key)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun accept(key: SelectionKey) {
        val serverSocketChannel = key.channel() as ServerSocketChannel
        val socketChannel = serverSocketChannel.accept()
        socketChannel.configureBlocking(false)
        val jvmSocketChannel = JvmSocketChannel(socketChannel)
        socketMap[socketChannel] = jvmSocketChannel
        socketListener.accept(jvmSocketChannel)
        socketChannel.register(socketSelector, SelectionKey.OP_READ or SelectionKey.OP_WRITE)
    }

    private fun connect(key: SelectionKey) {
        val socketChannel: SocketChannel = key.channel() as SocketChannel
        socketChannel.configureBlocking(false)
        socketChannel.finishConnect()
        val jvmSocketChannel = JvmSocketChannel(socketChannel)
        socketMap[socketChannel] = jvmSocketChannel
        socketListener.accept(jvmSocketChannel)
        socketChannel.register(socketSelector, SelectionKey.OP_READ or SelectionKey.OP_WRITE)
    }

    private fun read(key: SelectionKey) {
        val socketChannel = key.channel() as SocketChannel
        if (!socketChannel.isOpen) return
        readBuffer.clear()
        val numRead: Int
        try {
            numRead = socketChannel.read(readBuffer)
        } catch (e: IOException) {
            key.cancel()
            val channel = socketMap.getNotNull(socketChannel)
            channel.closeConnection()
            socketChannel.close()
            return
        }
        if (numRead == -1) {
            key.channel()
            val channel = socketMap.getNotNull(socketChannel)
            channel.closeConnection()
            socketChannel.close()
            return
        }
        if (numRead == 0) {
            println("numRead=0")
            return
        }
        socketMap.getNotNull(socketChannel).onRead(readBuffer.array().copyOf(numRead))
    }

    companion object {
        private const val MAX_PACKET_SIZE = 2097151
    }
}
