package mintlin.minecraft.datastructure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.ByteEnum
import mintlin.serializer.varByteEnumSerializer
import mintlin.serializer.varIntEnumSerializer

@Serializable(Face.Serializer::class)
enum class Face(override val value: Int, val relativeDirection: Point3D) : ByteEnum {
    Bottom(0, Point3D(0, -1, 0)),
    Top(1, Point3D(0, 1, 0)),
    North(2, Point3D(0, 0, -1)),
    South(3, Point3D(0, 0, 1)),
    West(4, Point3D(-1, 0, 0)),
    East(5, Point3D(1, 0, 0));

    companion object Serializer : KSerializer<Face> by varByteEnumSerializer(entries)
    object VarIntSerializer : KSerializer<Face> by varIntEnumSerializer(entries)
}