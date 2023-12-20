package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.io.network.protocol.ConnectionState
import mintlin.io.network.protocol.ConnectionStateChange
import mintlin.serializer.*

@Serializable
data class HandShake(
    val protocolVersion: VarInt,
    val serverAddress: VarString255,
    @Serializable(ShortSerializer::class)
    val serverPort: Int,
    val nextState: NextState
) : ConnectionStateChange {
    override val connectionState
        get() = when (nextState) {
            NextState.Status -> ConnectionState.Status
            NextState.Login -> ConnectionState.Login
        }
}

@Serializable(NextState.Serializer::class)
enum class NextState(override val value: Int) : VarIntEnum {
    Status(1), Login(2);

    companion object Serializer : KSerializer<NextState> by varIntEnumSerializer<NextState>(entries)
}
