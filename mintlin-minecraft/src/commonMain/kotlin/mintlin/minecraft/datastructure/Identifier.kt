package mintlin.minecraft.datastructure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.VarString32767Serializer
import mintlin.serializer.varIntSizedArraySerializer

typealias Identifier = @Serializable(IdentifierSerializer::class) String

typealias IdentifierSerializer = VarString32767Serializer

object IdentifiersSerializer : KSerializer<Array<String>> by varIntSizedArraySerializer(IdentifierSerializer)
