package mintlin.minecraft.network

typealias PacketListener = PacketChannel

interface PacketConsumer {
    fun consume(packet: Any)
}

class PacketFetcherToEventListener(private val packetListener: PacketListener) : PacketConsumer {
    override fun consume(packet: Any) {
        packetListener.dispatch(packet)
    }
}
