package mintlin.minecraft.packet

import kotlinx.serialization.Serializable

@Serializable
data class ServerBoundPlayerAbilities(val flags: Byte) {
    val isFlying get() = flags.toInt() and 0x02 == 0x02
}

