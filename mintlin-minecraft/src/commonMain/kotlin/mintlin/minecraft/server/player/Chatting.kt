package mintlin.minecraft.server.player

import mintlin.minecraft.server.PlayerChatEvent
import mintlin.minecraft.server.server.Server
import mintlin.lang.Init
import mintlin.minecraft.datastructure.StringComponent
import mintlin.minecraft.network.PacketListener
import mintlin.minecraft.network.PacketWriter
import mintlin.minecraft.packet.ChatMessage
import mintlin.minecraft.packet.PlayerChatMessage
import mintlin.serializer.toUnicodeEscape

class Chatting(
    val packetListener: PacketListener,
    val packetWriter: PacketWriter,
    val player: Player,
    val server: Server,
) : Init(player.listeners {
    packetListener.onEvent<ChatMessage> {
        val packet = PlayerChatMessage(
            player.uuid, it.messageCount, messageSignature = it.signature, message = it.message.toUnicodeEscape(),
            timestamp = it.timestamp, salt = it.salt, previousMessages = arrayOf(),
            unsignedContent = StringComponent(it.message),
            filterType = PlayerChatMessage.FilterType.PassThrough,
            chatType = 0, senderName = StringComponent(player.name),
            targetName = player.customName
        )
        server.dispatch(PlayerChatEvent(player, message = it.message, packet = packet))
    }
    server.onEvent<PlayerChatEvent> {
        packetWriter.sendCachedPacket(it.playerChatMessagePacket)
    }
})
