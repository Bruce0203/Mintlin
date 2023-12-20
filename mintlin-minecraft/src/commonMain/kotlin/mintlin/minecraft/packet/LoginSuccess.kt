package mintlin.minecraft.packet

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.*

@Serializable
data class LoginSuccess @OptIn(ExperimentalSerializationApi::class) constructor(
    @Serializable(UUIDSerializer::class)
    val uuid: UUID,
    @Serializable(VarString16Serializer::class)
    val username: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    @Serializable(PropertyArraySerializer::class)
    val property: Array<Property> = arrayOf()
) {
    @Serializable
    data class Property(
        @Serializable(VarString32767Serializer::class)
        val name: String,
        @Serializable(VarString32767Serializer::class)
        val value: String,
        val signature: VarString32767? = null,
    )

    object PropertyArraySerializer : KSerializer<Array<Property>> by varIntSizedArraySerializer(
        Property.serializer()
    )
}