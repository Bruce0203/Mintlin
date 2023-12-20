package mintlin.format.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.serializer.VarStringSerializer

fun <T> jsonSerializer(
    stringSerializer: VarStringSerializer,
    kSerializer: KSerializer<T>
) = JsonSerializer(stringSerializer = stringSerializer, kSerializer = kSerializer)

class JsonSerializer<T>(
    private val stringSerializer: VarStringSerializer,
    private val kSerializer: KSerializer<T>
) : KSerializer<T> {
    override val descriptor: SerialDescriptor = kSerializer.descriptor

    override fun deserialize(decoder: Decoder): T {
        val string = stringSerializer.deserialize(decoder)
        return JsonFormat.decodeFromString(kSerializer, string)
    }

    override fun serialize(encoder: Encoder, value: T) {
        val string = JsonFormat.encodeToString(kSerializer, value)
        stringSerializer.serialize(encoder, string)
    }
}