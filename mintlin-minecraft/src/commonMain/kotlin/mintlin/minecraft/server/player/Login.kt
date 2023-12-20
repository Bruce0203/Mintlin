package mintlin.minecraft.server.player

import mintlin.datastructure.scope.Scope
import mintlin.datastructure.scope.get
import mintlin.minecraft.server.server.MojangAPI
import mintlin.io.encryption.AESCipher
import mintlin.io.encryption.RSACipher
import mintlin.io.encryption.digester
import mintlin.lang.Init
import mintlin.minecraft.network.CompressionState
import mintlin.minecraft.network.EncryptionState
import mintlin.minecraft.network.PacketListener
import mintlin.minecraft.network.PacketWriter
import mintlin.minecraft.packet.*
import mintlin.serializer.UUID
import java.math.BigInteger

class Login(
    val packetListener: PacketListener,
    private val packetWriter: PacketWriter,
    player: Player,
    rsaCipher: RSACipher,
    aesCipher: AESCipher,
    mojang: MojangAPI,
    scope: Scope
) : Init(player.listeners {
    var threshold by scope.get<CompressionState>()
    var encryptionState by scope.get<EncryptionState>()
    packetListener.onEvent<LoginStart> { login ->
        player.uuid = UUID.randomUUID()
        player.name = login.name
        val compression = SetCompression(256)
        packetWriter.send(compression)
        threshold = compression.threshold
        packetWriter.send(LoginSuccess(player.uuid, player.name))
//        packetWriter.send(
//            EncryptionRequest(
//            serverID = "", publicKey = rsaCipher.publicKey, verifyToken = rsaCipher.verifyToken
//        )
//        )
    }
    packetListener.onEvent<EncryptionResponse> {
        if (!(rsaCipher.verifyToken).contentEquals(rsaCipher.decrypt(it.verifyToken))) {
            return@onEvent
        }
        val secretKey = rsaCipher.decrypt(it.sharedSecret)
        val digestedData = digester.digest("", publicKey = rsaCipher.publicKey, secretKey = secretKey)
        val serverId = BigInteger(digestedData).toString(16)

        mojang.getPlayerInfoByUsername(player.name, serverId) { playerInfo ->
            aesCipher.setSecretKey(secretKey)
            encryptionState = true
            with(playerInfo!!.properties[0]) {
                player.texture = value
                player.session.keySignature = signature!!
            }
            packetWriter.send(LoginSuccess(player.uuid, player.name, playerInfo.properties))
        }
    }
})
