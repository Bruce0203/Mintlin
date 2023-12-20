package mintlin.minecraft.datastructure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf
import kotlin.math.sqrt

data class Point2DImp(override val x: Int, override val y: Int) : Point2D {
    override fun toString() = "Point($x, $y)"
}

data class Point3DImp(override val x: Int, override val y: Int, override val z: Int) : Point3D {
    override fun toString() = "Point($x, $y, $z)"
}

typealias Vector = DoublePoint3D

fun Vector(): Vector = DoublePoint3DImp()

fun square(num: Double) = num * num

fun Vector.lengthSquared() = square(x) + square(y) + square(z)

fun Vector.length() = sqrt(lengthSquared())

fun Vector.normalize(): Vector {
    val length = length()
    return Vector(x / length, y / length, z / length)
}


data class FloatPoint3DImp(override val x: Float, override val y: Float, override val z: Float) : FloatPoint3D {
    constructor() : this(0f, 0f, 0f)

    override fun toString() = "Point($x, $y, $z)"
}

data class DoublePoint3DImp(override val x: Double, override val y: Double, override val z: Double) : DoublePoint3D {
    constructor() : this(.0, .0, .0)

    override fun toString() = "Point($x, $y, $z)"
}

@Serializable(Point2D.Serializer::class)
sealed interface Point2D {
    val x: Int
    val y: Int

    companion object {
        operator fun invoke(x: Int, y: Int): Point2D = Point2DImp(x, y)
    }

    object Serializer : KSerializer<Point2D> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<Point2D>())

        override fun deserialize(decoder: Decoder): Point2D {
            return Point2DImp(
                x = decoder.decodeInt(),
                y = decoder.decodeInt()
            )
        }

        override fun serialize(encoder: Encoder, value: Point2D) {
            encoder.encodeInt(value.x)
            encoder.encodeInt(value.y)
        }

    }
}

@Serializable(Point3D.Serializer::class)
sealed interface Point3D {
    val x: Int
    val y: Int
    val z: Int

    companion object {
        operator fun invoke(x: Int, y: Int, z: Int): Point3D = Point3DImp(x, y, z)
    }

    object Serializer : KSerializer<Point3D> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<Point3D>())

        override fun deserialize(decoder: Decoder): Point3D = Point3DImp(
            decoder.decodeInt(),
            decoder.decodeInt(),
            decoder.decodeInt()
        )

        override fun serialize(encoder: Encoder, value: Point3D) {
            encoder.encodeInt(value.x)
            encoder.encodeInt(value.y)
            encoder.encodeInt(value.z)
        }
    }
}

sealed interface FloatPoint3D {
    val x: Float
    val y: Float
    val z: Float

    companion object {
        operator fun invoke(x: Float, y: Float, z: Float): FloatPoint3D = FloatPoint3DImp(x, y, z)
    }
}

sealed interface DoublePoint3D {
    val x: Double
    val y: Double
    val z: Double

    companion object {
        operator fun invoke(x: Double, y: Double, z: Double): DoublePoint3D = DoublePoint3DImp(x, y, z)
    }
}

typealias PositionSerializer = Position.Serializer

@Serializable(PositionSerializer::class)
data class Position(
    override val x: Int,
    override val y: Int,
    override val z: Int
) : Point3D {
    companion object Serializer : KSerializer<Position> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<Position>())

        override fun deserialize(decoder: Decoder): Position {
            val value = decoder.decodeLong()
            val x = (value shr 38).toInt()
            val y = (value shl 52 shr 52).toInt()
            val z = (value shl 26 shr 38).toInt()
            return Position(x, y, z)
        }

        override fun serialize(encoder: Encoder, value: Position) {
            val blockX: Int = value.x
            val blockY: Int = value.y
            val blockZ: Int = value.z
            val longPos = blockX.toLong() and 0x3FFFFFFL shl 38 or
                    (blockZ.toLong() and 0x3FFFFFFL shl 12) or
                    (blockY.toLong() and 0xFFFL)
            encoder.encodeLong(longPos)
        }
    }
}

operator fun Position.plus(face: Face) = this + face.relativeDirection
operator fun Position.plus(o: Point3D) = Position(x + o.x, y + o.y, z + o.z)

@Serializable
data class FloatPosition(override val x: Float, override val y: Float, override val z: Float) : FloatPoint3D {
    companion object {
        operator fun invoke(x: Int, y: Int, z: Int) = FloatPosition(x.toFloat(), y.toFloat(), z.toFloat())
    }
}

@Serializable
data class DoublePosition(override val x: Double, override val y: Double, override val z: Double) : DoublePoint3D {
    companion object {
        operator fun invoke(x: Int, y: Int, z: Int) = DoublePosition(x.toDouble(), y.toDouble(), z.toDouble())
    }
}

@Serializable(ShortPosition.Serializer::class)
data class ShortPosition(override val x: Int, override val y: Int, override val z: Int) : Point3D {
    companion object Serializer : KSerializer<ShortPosition> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<ShortPosition>())

        override fun deserialize(decoder: Decoder) = ShortPosition(
            decoder.decodeShort().toInt(),
            decoder.decodeShort().toInt(),
            decoder.decodeShort().toInt()
        )

        override fun serialize(encoder: Encoder, value: ShortPosition) {
            encoder.encodeShort(value.x.toShort())
            encoder.encodeShort(value.y.toShort())
            encoder.encodeShort(value.z.toShort())
        }
    }
}

@Serializable
data class AngleRotation(
    val yaw: Angle,
    val pitch: Angle
)

@Serializable
data class FloatRotation(val yaw: Float, val pitch: Float) {
    companion object {
        operator fun invoke(yaw: Int, pitch: Int) = FloatRotation(yaw.toFloat(), pitch.toFloat())
    }
}

fun FloatRotation.toAngle() =
    AngleRotation((fixYaw(yaw) / 360f * 256f).toInt().angle, (pitch / 360f * 256f).toInt().angle)

fun DoublePoint3D.abs() = Position(x.toInt(), y.toInt(), z.toInt())
fun DoublePoint3D.toShort() = ShortPosition(x.toInt(), y.toInt(), z.toInt())

operator fun DoublePoint3D.minus(o: DoublePosition) = DoublePosition(x - o.x, y - o.y, z - o.z)
operator fun DoublePoint3D.times(num: Int) = DoublePosition(x * num, y * num, z * num)
operator fun DoublePoint3D.times(num: Float) = DoublePosition(x * num, y * num, z * num)

fun fixYaw(yaw: Float): Float {
    var yaw = yaw
    yaw %= 360
    if (yaw < -180.0f) {
        yaw += 360.0f
    } else if (yaw > 180.0f) {
        yaw -= 360.0f
    }
    return yaw
}

@Serializable
data class Location(val position: DoublePosition, val rotation: FloatRotation)