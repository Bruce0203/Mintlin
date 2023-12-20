package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.VarIntSerializer
import mintlin.serializer.varIntSizedArraySerializer

@Serializable
data class RemoveEntities(
    @Serializable(EntityIdsSerializer::class)
    val entityIds: Array<Int>
) {
    object EntityIdsSerializer : KSerializer<Array<Int>> by varIntSizedArraySerializer(
        VarIntSerializer
    )
}