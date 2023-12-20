package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.varIntSizedArraySerializer

@Serializable
data class Explosion(
    val x: Double, val y: Double, val z: Double,
    val strength: Float,
    @Serializable(RecordsSerializer::class)
    val records: Array<Record>,
    val playerMotion: PlayerMotion,
)

object RecordsSerializer : KSerializer<Array<Record>> by varIntSizedArraySerializer(Record.serializer())

@Serializable
data class Record(
    val x: Byte,
    val y: Byte,
    val z: Byte
)

@Serializable
data class PlayerMotion(
    val x: Float,
    val y: Float,
    val z: Float
)