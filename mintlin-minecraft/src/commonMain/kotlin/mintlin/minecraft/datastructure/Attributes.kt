package mintlin.minecraft.datastructure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf
import mintlin.serializer.*

@Serializable(AttributeKey.Serializer::class)
enum class AttributeKey(val identifier: Identifier, val default: Double, val min: Double, val max: Double) {
    MovementSpeed("generic.movement_speed", default = 0.7, min = .0, max = 1024.0),
    FlyingSpeed("generic.flying_speed", default = 0.1, min = .0, max = 1024.0);

    companion object Serializer : KSerializer<AttributeKey> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<AttributeKey>())

        override fun deserialize(decoder: Decoder): AttributeKey {
            return AttributeKey.entries.find { it.identifier == IdentifierSerializer.deserialize(decoder) }
                ?: throw RuntimeException("unknown attribute identifier")
        }

        override fun serialize(encoder: Encoder, value: AttributeKey) {
            IdentifierSerializer.serialize(encoder, value.identifier)
        }
    }
}

@Suppress("ArrayInDataClass")
@Serializable
data class Attribute(
    val key: AttributeKey,
    var value: Double,
    @Serializable(ModifiersSerializer::class)
    var modifiers: Array<Modifier> = emptyArray()
)

object AttributesSerializer : KSerializer<Array<Attribute>> by varIntSizedArraySerializer(Attribute.serializer())

@Serializable
object ModifiersSerializer : KSerializer<Array<Modifier>> by varIntSizedArraySerializer(Modifier.serializer())

@Serializable
data class Modifier(
    @Serializable(UUIDSerializer::class)
    val uuid: UUID,
    val amount: Double,
    val operation: Operation,
) {
    @Serializable(Operation.Serializer::class)
    enum class Operation(override val value: Int) : ByteEnum {
        Adding(0), Percentage(1), Multiply(2);

        companion object Serializer : KSerializer<Operation> by varByteEnumSerializer(entries)
    }
}
