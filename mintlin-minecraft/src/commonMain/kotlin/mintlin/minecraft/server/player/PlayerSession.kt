package mintlin.minecraft.server.player

import mintlin.minecraft.packet.PlayerChatSignature

class PlayerSession {
    var keySignature: String? = null
    var chatSignature: PlayerChatSignature? = null
}