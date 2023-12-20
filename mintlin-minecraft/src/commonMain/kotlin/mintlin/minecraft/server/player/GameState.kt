package mintlin.minecraft.server.player

class HandShakeState(
)

class LoginState(
    login: Login,
    loginAcknowledged: LoginAcknowledged,
    initializePlayer: InitializePlayer,
)

class ConfigurationState(
    handShakeState: HandShakeState,
    disconnect: Disconnection
)

class PlayState(
    disconnect: Disconnection,
    initializePlayer: InitializePlayer,
    blockBreakAndPlacement: BlockListener,
    keepAliveEvery10Sec: KeepAliveEvery10Sec,
    playerInventoryFetcher: PlayerInventoryFetcher,
    broadcaster: Broadcaster,
    chatDebugger: ChatDebugger,
    entitySpawner: EntitySpawner,
    playerMovement: PlayerMovement,
    chatting: Chatting,
    entityDamage: EntityDamage,
)
