package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Chat
import mintlin.serializer.VarInt
import mintlin.serializer.VarString32767Serializer
import mintlin.serializer.varIntSizedArraySerializer

@Serializable
data class CommandSuggestionsResponse(
    val id: VarInt,
    val start: VarInt,
    val length: VarInt,
    @Serializable(MatchesSerializer::class)
    val matches: Array<Matches>
) {
    object MatchesSerializer
        : KSerializer<Array<Matches>> by varIntSizedArraySerializer(
        Matches.serializer()
    )

    @Serializable
    data class Matches(
        @Serializable(VarString32767Serializer::class)
        val match: String,
        val tooltip: Chat? = null,
    )
}