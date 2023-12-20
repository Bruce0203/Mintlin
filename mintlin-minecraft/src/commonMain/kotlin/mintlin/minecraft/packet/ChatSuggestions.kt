package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.serializer.VarIntEnum
import mintlin.serializer.VarString32767Serializer
import mintlin.serializer.varIntEnumSerializer
import mintlin.serializer.varIntSizedArraySerializer

@Serializable
data class ChatSuggestions(
    val action: Action,
    @Serializable(EntriesSerializer::class)
    val entries: Array<String>
) {
    object EntriesSerializer : KSerializer<Array<String>> by varIntSizedArraySerializer(
        VarString32767Serializer
    )

    @Serializable(Action.Serializer::class)
    enum class Action(override val value: Int) : VarIntEnum {
        Add(0), Remove(1), Set(2);

        companion object Serializer :
            KSerializer<Action> by varIntEnumSerializer(entries)
    }
}