package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.MainHand
import mintlin.serializer.VarIntEnum
import mintlin.serializer.VarString16Serializer
import mintlin.serializer.varIntEnumSerializer

@Serializable
data class ClientInformation(
    @Serializable(VarString16Serializer::class)
    val locale: String,
    val viewDistance: Byte,
    @Serializable(ChatMode.Serializer::class)
    val chatMode: ChatMode,
    val chatColors: Boolean,
    val displayedSkinParts: Byte,
    @Serializable(MainHand.Serializer::class)
    val mainHand: MainHand,
    val enableTextFiltering: Boolean,
    val allowServerListings: Boolean
)

@Serializable
enum class ChatMode(override val value: Int) : VarIntEnum {
    Enabled(0), CommandsOnly(1), Hidden(2);

    companion object Serializer : KSerializer<ChatMode> by varIntEnumSerializer(entries)
}

