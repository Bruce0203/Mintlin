package mintlin.minecraft.server.player

import mintlin.lang.MutableDelegate
import mintlin.lang.lateInit
import mintlin.lang.notNull
import kotlin.properties.Delegates

class PlayerIsInvulnerable
    : MutableDelegate<Boolean> by Delegates.notNull(false)

class PlayerIsFlying
    : MutableDelegate<Boolean> by Delegates.notNull(false)

class PlayerIsFlyingAllowed
    : MutableDelegate<Boolean> by Delegates.notNull(false)

class PlayerIsInstantBreak
    : MutableDelegate<Boolean> by Delegates.notNull(false)

class PlayerFlyingSpeed
    : MutableDelegate<Float> by Delegates.notNull(0f)

class PlayerModifier
    : MutableDelegate<Float> by Delegates.notNull(0f)

class PlayerIsListed
    : MutableDelegate<Boolean> by Delegates.notNull(true)

class PlayerLatency
    : MutableDelegate<Int> by Delegates.notNull(0)

class PlayerName
    : MutableDelegate<String> by Delegates.lateInit()

class PlayerViewDistance
    : MutableDelegate<Int> by Delegates.lateInit()

class PlayerSimulationDistances
    : MutableDelegate<Int> by Delegates.lateInit()