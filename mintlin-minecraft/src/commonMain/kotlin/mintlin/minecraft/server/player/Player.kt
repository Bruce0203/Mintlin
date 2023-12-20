package mintlin.minecraft.server.player

import mintlin.datastructure.eventbus.EventBus
import mintlin.datastructure.eventbus.EventBusImp
import mintlin.datastructure.scope.*
import mintlin.minecraft.server.entity.Entity
import mintlin.minecraft.server.entity.EntityFacade
import mintlin.minecraft.server.entity.HeldSlotManipulator
import mintlin.minecraft.server.ping.PingRequestResponse
import mintlin.minecraft.server.ping.StatusRequestResponse
import mintlin.minecraft.network.PacketChannel
import mintlin.minecraft.network.PacketWriter

interface Player : PlayerFacade, EntityFacade, Scope, EventBus {
    companion object : ScopeFactoryDSL<Player, PacketChannel> by scopedDSL({
        singleOf(::PlayerImp) bind Player::class
        singleOf(::StatusRequestResponse)
        singleOf(::PingRequestResponse)
        singleOf(::Login)
        singleOf(::Disconnection)
        singleOf(::LoginAcknowledged)
        singleOf(::InitializePlayer)
        singleOf(::HandShakeState)
        singleOf(::LoginState)
        singleOf(::ConfigurationState)
        singleOf(::PlayState)
        singleOf(::KeepAliveEvery10Sec)
        singleOf(::PlayState)
        singleOf(::BlockListener)
        singleOf(::PlayerInventoryFetcher)
        singleOf(::Broadcaster)
        singleOf(::ChatDebugger)
        singleOf(::EntitySpawner)
        singleOf(::PlayerMovement)
        singleOf(::Chatting)
        singleOf(::EntityDamage)

        singleOf(::PlayerTexture)
        singleOf(::PlayerSession)

        singleOf(::PlayerIsInvulnerable)
        singleOf(::PlayerIsFlying)
        singleOf(::PlayerIsFlyingAllowed)
        singleOf(::PlayerIsInstantBreak)
        singleOf(::PlayerFlyingSpeed)
        singleOf(::PlayerName)
        singleOf(::PlayerViewDistance)
        singleOf(::PlayerSimulationDistances)
        singleOf(::PlayerModifier)
        singleOf(::PlayerLatency)
        singleOf(::PlayerIsListed)
        singleOf(::PlayerAttributes)
    })

}

interface PlayerFacade : EntityFacade {
    val packetWriter: PacketWriter
    var session: PlayerSession
    var name: String
    var latency: Int
    var isListed: Boolean
    var texture: String?
    var isInvulnerable: Boolean
    var isFlying: Boolean
    var isFlyingAllowed: Boolean
    var isInstantBreak: Boolean
    var heldSlot: Int
    var viewDistance: Int
    var simulationDistance: Int
    var attributes: PlayerAttributes
}

class PlayerImp(scope: Scope) : Player,
    EntityFacade by scope.get<Entity>(),
    EventBus by EventBusImp(scope), Scope by scope {
    override val packetWriter: PacketWriter = scope.get()
    override var session = scope.get<PlayerSession>()
    override var name by scope.get<PlayerName>()
    override var latency by scope.get<PlayerLatency>()
    override var isListed by scope.get<PlayerIsListed>()
    override var texture by scope.get<PlayerTexture>()
    override var isInvulnerable by scope.get<PlayerIsInvulnerable>()
    override var isFlying by scope.get<PlayerIsFlying>()
    override var isFlyingAllowed by scope.get<PlayerIsFlyingAllowed>()
    override var isInstantBreak by scope.get<PlayerIsInstantBreak>()

    //    override var flyingSpeed by scope.get<PlayerFlyingSpeed>()
//    override var playerModifier by scope.get<PlayerModifier>()
    override var heldSlot by scope.get<HeldSlotManipulator>()
    override var viewDistance by scope.get<PlayerViewDistance>()
    override var simulationDistance by scope.get<PlayerViewDistance>()
    override var attributes = scope.get<PlayerAttributes>()
}