package mintlin.io.network.protocol

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import mintlin.datastructure.FastArrayList
import mintlin.datastructure.FastIdentityMap
import mintlin.datastructure.FastMap

interface PacketRegistry {
    fun getSerializerById(id: Int): KSerializer<*>?
    fun getIdBySerializer(serializer: KSerializer<*>): Int
}

class PacketRegistryImp(
    private val idToSerializer: FastMap<Int, KSerializer<*>>,
    private val serializerToId: FastMap<KSerializer<*>, Int>
) : PacketRegistry {

    override fun getSerializerById(id: Int) = idToSerializer.getOrNull(id)

    @ExperimentalSerializationApi
    override fun getIdBySerializer(serializer: KSerializer<*>) = serializerToId.getOrNull(serializer)
        ?: throw NoSuchElementException("Packet[${serializer.descriptor.serialName}]")
}

interface ProtocolPacketRegistry {
    fun getProtocolPacketRegistry(protocolEntry: ProtocolEntry): ConnStatePacketRegistry?
}

class ProtocolPacketRegistryImp(
    private val registry: FastMap<ProtocolEntry, ConnStatePacketRegistry>
) : ProtocolPacketRegistry {

    override fun getProtocolPacketRegistry(protocolEntry: ProtocolEntry) = registry[protocolEntry]
}

interface ConnStatePacketRegistry {
    fun getConnStatePacketRegistry(connectionState: ConnectionState): PacketRegistry
}

class ConnStatePacketRegistryImp(
    private val registry: Array<PacketRegistry>
) : ConnStatePacketRegistry {

    override fun getConnStatePacketRegistry(connectionState: ConnectionState) = registry[connectionState.ordinal]
}


fun protocolPacketRegistryOf(bounded: Bound, vararg packetEntries: PacketEntry): ProtocolPacketRegistry {
    val entries = FastIdentityMap<ProtocolEntry, FastArrayList<PacketEntry>>()
    packetEntries.forEach {
        entries.getOrPut(it.protocolEntry) { FastArrayList() }.add(it)
    }
    return ProtocolPacketRegistryImp(entries.map {
        conStatePacketRegistryOf(bounded, entries.getNotNull(it.key))
    })
}

fun conStatePacketRegistryOf(bounded: Bound, packetEntries: FastArrayList<PacketEntry>): ConnStatePacketRegistry {
    val entries = Array<FastArrayList<PacketEntry>>(ConnectionState.entries.size) { FastArrayList() }
    packetEntries.forEach {
        entries[it.connState.ordinal].add(it)
    }
    return ConnStatePacketRegistryImp(Array(ConnectionState.entries.size) {
        val entry = entries[it]
        packetRegistryOf(bounded, entry)
    })
}

fun packetRegistryOf(bounded: Bound, packetEntries: FastArrayList<PacketEntry>): PacketRegistry {
    val idToSerializer = FastIdentityMap<Int, KSerializer<*>>()
    val serializerToId = FastIdentityMap<KSerializer<*>, Int>()
    packetEntries.forEach {
        if (bounded == it.bounded) {
            idToSerializer.put(it.id, it.serializer)
        } else {
            serializerToId.put(it.serializer, it.id)
        }
    }
    return PacketRegistryImp(idToSerializer, serializerToId)
}