@file:Suppress("ArrayInDataClass")

package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf
import mintlin.minecraft.datastructure.Chat
import mintlin.minecraft.datastructure.GameMode
import mintlin.serializer.*
import kotlin.experimental.and

@Serializable(PlayerInfoUpdate.Serializer::class)
data class PlayerInfoUpdate internal constructor(
    val players: Array<PlayerInfo>,
    @Transient
    private val flag: Byte,
    @Transient
    private val serializers: List<KSerializer<Any>>
) {

    companion object Serializer : KSerializer<PlayerInfoUpdate> {
        operator fun invoke(players: Array<PlayerInfo>): PlayerInfoUpdate {
            return if (players.isEmpty() || players[0].actions.isEmpty()) PlayerInfoUpdate(players = players, flag = 0x00, serializers = emptyList())
            else {
                var flag = 0x00
                val actions = players[0].actions
                val serializers = ArrayList<KSerializer<Any>>(actions.size)
                actions.map {
                    flag = flag or when (it) {
                        is PlayerInfo.AddPlayer -> 0x01
                        is PlayerInfo.InitializeChat -> 0x02
                        is PlayerInfo.UpdateGameMode -> 0x04
                        is PlayerInfo.UpdateListed -> 0x08
                        is PlayerInfo.UpdateLatency -> 0x10
                        is PlayerInfo.UpdateDisplayName -> 0x20
                    }
                    @Suppress("unchecked_cast")
                    serializers.add(when(it) {
                        is PlayerInfo.AddPlayer -> PlayerInfo.AddPlayer.serializer()
                        is PlayerInfo.InitializeChat -> PlayerInfo.InitializeChat.serializer()
                        is PlayerInfo.UpdateDisplayName -> PlayerInfo.UpdateDisplayName.serializer()
                        is PlayerInfo.UpdateGameMode -> PlayerInfo.UpdateGameMode.serializer()
                        is PlayerInfo.UpdateLatency -> PlayerInfo.UpdateLatency.serializer()
                        is PlayerInfo.UpdateListed -> PlayerInfo.UpdateListed.serializer()
                    } as KSerializer<Any>)
                }
                PlayerInfoUpdate(players = players, flag = flag.toByte(), serializers = serializers)
            }
        }

        override val descriptor = buildClassSerialDescriptor(classNameOf<PlayerInfoUpdate>())

        override fun deserialize(decoder: Decoder): PlayerInfoUpdate {
            val flag = decoder.decodeByte()
            val list = ArrayList<KSerializer<out PlayerInfo.PlayerAction>>()
            if (flag has 0x01) list.add(PlayerInfo.AddPlayer.serializer())
            if (flag has 0x02) list.add(PlayerInfo.InitializeChat.serializer())
            if (flag has 0x04) list.add(PlayerInfo.UpdateGameMode.serializer())
            if (flag has 0x08) list.add(PlayerInfo.UpdateListed.serializer())
            if (flag has 0x10) list.add(PlayerInfo.UpdateLatency.serializer())
            if (flag has 0x20) list.add(PlayerInfo.UpdateDisplayName.serializer())
            return PlayerInfoUpdate(Array(VarIntSerializer.deserialize(decoder)) {
                PlayerInfo(
                    uuid = UUIDSerializer.deserialize(decoder),
                    actions = list.map { it.deserialize(decoder) }.toTypedArray()
                )
            })
        }

        private infix fun Byte.has(flag: Byte) = this and flag == flag

        override fun serialize(encoder: Encoder, value: PlayerInfoUpdate) {
            encoder.encodeByte(value.flag)
            VarIntSerializer.serialize(encoder, value.players.size)
            value.players.forEach { player ->
                UUIDSerializer.serialize(encoder, player.uuid)
                value.serializers.forEachIndexed { index, serializer ->
                    serializer.serialize(encoder, player.actions[index])
                }
            }
        }

    }
}

@Serializable
data class PlayerInfo(
    val uuid: UUID,
    val actions: Array<PlayerAction>
) {

    @Serializable
    sealed interface PlayerAction

    @Serializable
    data class AddPlayer(
        @Serializable(VarString32767Serializer::class)
        val name: String,
        @Serializable(PropertiesSerializer::class)
        val properties: Array<Property>
    ) : PlayerAction {

        data object PropertiesSerializer :
            KSerializer<Array<Property>> by varIntSizedArraySerializer(Property.serializer())

        @Serializable
        data class Property(
            @Serializable(VarString32767Serializer::class)
            val name: String,
            @Serializable(VarString32767Serializer::class)
            val value: String,
            val signature: VarString32767?
        )
    }

    @Serializable
    data class InitializeChat(val signatureData: PlayerChatSignature?) : PlayerAction

    @Serializable
    data class UpdateGameMode(val gameMode: GameMode) : PlayerAction

    @Serializable
    data class UpdateListed(val listed: Boolean) : PlayerAction

    @Serializable
    data class UpdateLatency(val ping: VarInt) : PlayerAction

    @Serializable
    data class UpdateDisplayName(val displayName: Chat?) : PlayerAction

}
