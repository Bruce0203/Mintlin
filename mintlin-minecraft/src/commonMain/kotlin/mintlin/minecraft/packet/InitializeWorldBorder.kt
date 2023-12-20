package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.serializer.VarInt
import mintlin.serializer.VarLong

@Serializable
data class InitializeWorldBorder(
    val x: Double, val z: Double,
    val oldDiameter: Double, val newDiameter: Double,
    val speed: VarLong,
    val portalTeleportBoundary: VarInt,
    val warningBlocks: VarInt,
    val warningTime: VarInt,
)