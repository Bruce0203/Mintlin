package mintlin.io.network.protocol

import kotlinx.serialization.KSerializer

data class PacketEntry(
    val serializer: KSerializer<*>,
    val bounded: Bound,
    val connState: ConnectionState,
    val id: Int,
    val protocolEntry: ProtocolEntry
)

