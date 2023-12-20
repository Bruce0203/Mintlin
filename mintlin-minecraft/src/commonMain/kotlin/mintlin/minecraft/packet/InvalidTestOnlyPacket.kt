package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.ByteSerializer
import mintlin.serializer.fixedSizeArraySerializer

@Serializable
data object InvalidTestOnlyPacket

@Serializable
data class BigSizeTestOnlyPacket(
    @Serializable(BigSizeTestOnlyPacketFixedSizeBytesSerializer::class)
    val fixedSizeBytes: Array<Byte>
) {
    object BigSizeTestOnlyPacketFixedSizeBytesSerializer :
        KSerializer<Array<Byte>> by fixedSizeArraySerializer(20_000, ByteSerializer)
}