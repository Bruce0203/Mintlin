package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.ByteEnum
import mintlin.serializer.varByteEnumSerializer

@Serializable
data class ClientBoundChangeDifficulty(
    val difficulty: Difficulty,
    val isDifficultyLocked: Boolean
)

@Serializable(Difficulty.Serializer::class)
enum class Difficulty(override val value: Int) : ByteEnum {
    Peaceful(0), Easy(1), Normal(2), Hard(3);

    companion object Serializer : KSerializer<Difficulty> by varByteEnumSerializer(entries)
}