package mintlin.io.network.protocol

import kotlinx.atomicfu.atomic
import mintlin.lang.MutableDelegate
import kotlin.reflect.KProperty

class ConnectionStateDelegate(private val state: PacketRegistryDelegate) : MutableDelegate<ConnectionState> {
    override var value: ConnectionState
        get() = state.connectionState
        set(value) { state.changeConnectionStateTo(value) }
}

class ProtocolEntryDelegate(private val state: PacketRegistryDelegate) : MutableDelegate<ProtocolEntry> {
    override var value: ProtocolEntry get() = state.protocolEntry
        set(value) { state.changeProtocolEntryTo(value) }
}

interface PacketRegistryDelegate : MutableDelegate<PacketRegistry> {
    val connectionState: ConnectionState
    val protocolEntry: ProtocolEntry
    fun changeConnectionStateTo(connectionState: ConnectionState)
    fun changeProtocolEntryTo(protocolEntry: ProtocolEntry)
}

class PacketRegistryDelegateImp(
    private val protocolPacketRegistry: ProtocolPacketRegistry,
    serverProtocolEntry: ProtocolEntry
) : PacketRegistryDelegate {
    override var connectionState: ConnectionState by atomic(ConnectionState.Handshake)
    override var protocolEntry: ProtocolEntry by atomic(serverProtocolEntry)
    override var value: PacketRegistry by atomic(getPacketRegistryNotNull())

    override fun changeConnectionStateTo(connectionState: ConnectionState) {
        this.connectionState = connectionState
        refreshPacketRegistry()
    }

    override fun changeProtocolEntryTo(protocolEntry: ProtocolEntry) {
        this.protocolEntry = protocolEntry
        refreshPacketRegistry()
    }

    private fun refreshPacketRegistry() {
        value = getPacketRegistryNotNull()
    }

    private fun getPacketRegistryNotNull(): PacketRegistry {
        val entry = protocolEntry
        val connStatePacketRegistry = protocolPacketRegistry.getProtocolPacketRegistry(entry)
            ?: throw AssertionError("$entry is not exists")
        return connStatePacketRegistry.getConnStatePacketRegistry(connectionState)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = value
}
