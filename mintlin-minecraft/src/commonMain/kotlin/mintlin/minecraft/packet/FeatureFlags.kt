package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.IdentifiersSerializer

@Serializable
data class FeatureFlags(
    @Serializable(IdentifiersSerializer::class)
    val featureFlags: Array<String>
)