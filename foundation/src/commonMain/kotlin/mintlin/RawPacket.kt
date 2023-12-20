package mintlin

fun RawPacket(id: Int, data: ByteArray): RawPacket = RawPacketImp(id, data)

interface RawPacket {
    val id: Int
    val data: ByteArray
}

@Suppress("ArrayInDataClass")
data class RawPacketImp(override val id: Int, override val data: ByteArray) : RawPacket
