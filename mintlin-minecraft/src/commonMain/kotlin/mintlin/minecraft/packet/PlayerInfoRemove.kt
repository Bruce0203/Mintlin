package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.UUID
import mintlin.serializer.UUIDSerializer
import mintlin.serializer.varIntSizedArraySerializer

@Serializable
data class PlayerInfoRemove(
    @Serializable(PlayersSerializer::class)
    val players: Array<UUID>
) {
    object PlayersSerializer : KSerializer<Array<UUID>> by varIntSizedArraySerializer(UUIDSerializer)
}