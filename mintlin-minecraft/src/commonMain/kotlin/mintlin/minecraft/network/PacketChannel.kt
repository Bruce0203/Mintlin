package mintlin.minecraft.network

import mintlin.datastructure.eventbus.EventBus
import mintlin.datastructure.eventbus.EventBusImp
import mintlin.datastructure.scope.*
import mintlin.internal.MyInternalAPI
import mintlin.io.compression.Compression
import mintlin.io.compression.zLibCompression
import mintlin.io.encryption.createAESCipher
import mintlin.io.network.*
import mintlin.io.network.protocol.ConnectionStateDelegate
import mintlin.io.network.protocol.PacketRegistryDelegate
import mintlin.io.network.protocol.PacketRegistryDelegateImp
import mintlin.io.network.protocol.ProtocolEntryDelegate

class PacketChannelImp(
    private val socketChannel: SocketChannelState, scope: Scope
) : SocketCloseListener, PacketChannel, EventBus by EventBusImp(scope), Scope by scope,
    SocketChannel by socketChannel, SocketWriter by socketChannel {
    override fun closeChannel() {
        socketChannel.closeChannel()
    }

    override fun onSocketClosed() {
        this.dispatch(ConnectionSocketClosedEvent())
        this.closeScope()
    }
}

interface PacketChannel : Scope, EventBus, SocketCloseListener, SocketChannel {
    companion object : ScopeFactoryDSL<PacketChannel, SocketChannelState> by scopedDSL({
        singleOf(::PacketChannelImp) binds arrayOf(PacketChannel::class, EventBus::class, SocketWriter::class)
        singleOf(::PacketReader) bind SocketReadListener::class
        singleOf(::RawPacketPayloadReader)
        singleOf(::PacketWriter)
        singleOf(::RawPacketWriter)
        singleOf(::PacketFetcherToEventListener) bind PacketConsumer::class
        singleOf(::PacketCompression)
        singleOf(::CompressionState)
        singleOf(::zLibCompression) bind Compression::class
        singleOf(::EncryptionState)
        singleOf(::PacketDecryption)
        singleOf(::PacketEncryption)
        singleOf(::createAESCipher)
        singleOf(::ConnectionStateDelegate)
        singleOf(::ProtocolEntryDelegate)
        singleOf(::PacketRegistryDelegateImp) bind PacketRegistryDelegate::class
    })
}
