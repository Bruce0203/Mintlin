package mintlin.minecraft.server.ping

import mintlin.lang.Init
import mintlin.minecraft.network.PacketListener
import mintlin.minecraft.network.PacketWriter
import mintlin.minecraft.packet.PingRequestStatus
import mintlin.minecraft.packet.PingResponse

data class PingRequestResponse(
    val packetListener: PacketListener,
    val packetWriter: PacketWriter,
) : Init(packetListener.listeners {
    packetListener.onEvent<PingRequestStatus> { request ->
        packetWriter.send(PingResponse(request.payload))
    }
})