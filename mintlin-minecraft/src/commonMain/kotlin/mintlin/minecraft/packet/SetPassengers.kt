package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.VarInt
import mintlin.serializer.VarIntSerializer
import mintlin.serializer.varIntSizedArraySerializer

@Serializable
data class SetPassengers(
    override val entityId: VarInt,
    @Serializable(PassengersSerializer::class)
    val passengers: Array<Int>
) : IdentifiedEntity

object PassengersSerializer : KSerializer<Array<Int>> by varIntSizedArraySerializer(VarIntSerializer)