package mintlin.minecraft.server.ping

import mintlin.cachedPacket
import mintlin.datastructure.scope.Scope
import mintlin.datastructure.scope.get
import mintlin.minecraft.server.server.MessageOfTheDayAccessor
import mintlin.minecraft.server.server.list.PlayerList
import mintlin.io.network.protocol.ProtocolEntryDelegate
import mintlin.lang.Init
import mintlin.minecraft.network.PacketListener
import mintlin.minecraft.network.PacketWriter
import mintlin.minecraft.packet.Status
import mintlin.minecraft.packet.StatusRequest
import mintlin.minecraft.packet.StatusResponse

class StatusRequestResponse(
    private val packetListener: PacketListener,
    private val packetWriter: PacketWriter,
    private val playerList: PlayerList,
    private val scope: Scope,
    protocolEntryDelegate: ProtocolEntryDelegate
) : Init(packetListener.listeners {
    val protocolEntry by protocolEntryDelegate
    val messageOfTheDay by scope.get<MessageOfTheDayAccessor>()
    val statusResponse = cachedPacket {
        StatusResponse(
            status = Status(
                version = Status.Version(name = "v${protocolEntry.name}", protocol = protocolEntry.id),
                players = Status.Players(max = 6974, online = playerList.size, sample = arrayOf()),
                description = messageOfTheDay,
                enforcesSecureChat = true, previewsChat = true
            )
        )
    }
    packetListener.onEvent<StatusRequest> {
        statusResponse.scheduleRefresh()
        packetWriter.sendCachedPacket(statusResponse)
    }
})