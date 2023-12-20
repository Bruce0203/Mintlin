package mintlin.minecraft.datastructure

import kotlinx.serialization.Serializable

@Serializable
data class Angle(val rotation: Byte)

val Int.angle get() = toUByte().toInt().toByte().angle
val Byte.angle get() = Angle(this)