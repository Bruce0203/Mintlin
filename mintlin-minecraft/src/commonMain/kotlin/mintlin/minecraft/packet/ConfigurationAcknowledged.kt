package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.lang.classNameOf

@Serializable
class ConfigurationAcknowledged {
    override fun toString() = classNameOf<ConfigurationAcknowledged>()
}