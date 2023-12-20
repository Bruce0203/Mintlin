package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Hand

@Serializable
data class SwingArm(val swingHand: Hand)
