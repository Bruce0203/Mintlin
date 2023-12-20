package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.io.network.protocol.ConnectionState
import mintlin.io.network.protocol.ConnectionStateChange
import mintlin.lang.classNameOf

@Serializable
class LoginAcknowledged : ConnectionStateChange {
    override val connectionState get() = ConnectionState.Configuration
    override fun toString() = classNameOf<LoginAcknowledged>()
}