package mintlin.minecraft.server.handshake

import mintlin.datastructure.scope.*
import mintlin.io.network.protocol.ProtocolEntryDelegate
import mintlin.lang.Init
import mintlin.minecraft.datastructure.TranslationComponent
import mintlin.minecraft.server.entity.Entity
import mintlin.minecraft.server.player.LoginState
import mintlin.minecraft.server.player.Player
import mintlin.minecraft.network.PacketChannel
import mintlin.minecraft.network.PacketListener
import mintlin.minecraft.network.PacketWriter
import mintlin.minecraft.packet.Disconnect
import mintlin.minecraft.packet.HandShake
import mintlin.minecraft.packet.NextState
import mintlin.minecraft.server.player.Login
import mintlin.minecraft.server.server.list.EntityList
import kotlin.system.measureNanoTime

interface HandShakeFetcher : Scope {
    companion object : ScopeFactoryDSL<HandShakeFetcher, Unit> by scopedDSL({
        singleOf(::HandShakeFetcherImp) bind HandShakeFetcher::class
    })
}

class HandShakeFetcherImp(
    packetChannel: PacketChannel,
    packetListener: PacketListener,
    packetWriter: PacketWriter,
    protocolEntryDelegate: ProtocolEntryDelegate,
    val scope: Scope
) : HandShakeFetcher, Scope by scope, Init(packetListener.listeners {
    val protocolEntry by protocolEntryDelegate
    packetListener.onEvent<HandShake> { handShake ->
        when (handShake.nextState) {
            NextState.Status -> {
                mintlin.minecraft.server.ping.Status(packetChannel)
            }
            NextState.Login -> {
                if (handShake.protocolVersion != protocolEntry.id) {
                    packetWriter.send(
                        Disconnect(
                            Disconnect.TextReason(
                                TranslationComponent(
                                    if (handShake.protocolVersion < protocolEntry.id) {
                                        "pack.incompatible.new"
                                    } else "pack.incompatible.old"
                                )
                            )
                        )
                    )
                } else {
                    val createScopeAndLinkTo = Player.createScopeAndLinkTo(packetChannel, Entity())
                    measureNanoTime{
                        createScopeAndLinkTo.get<PacketChannel>()
                    }.apply(::println)
                    createScopeAndLinkTo.get<LoginState>()
                }
            }
        }
        closeListener()
        scope.closeScope()
    }
})