package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.io.network.protocol.ConnectionState
import mintlin.io.network.protocol.ConnectionStateChange
import mintlin.lang.classNameOf

@Serializable
class FinishConfiguration : ConnectionStateChange {
    override val connectionState get() = ConnectionState.Play
    override fun toString() = classNameOf<FinishConfiguration>()
}