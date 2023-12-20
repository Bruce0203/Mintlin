@file:Suppress("ArrayInDataClass")

package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import mintlin.format.json.JsonFormat
import mintlin.minecraft.datastructure.Chat
import mintlin.serializer.UUID
import mintlin.serializer.VarString32767Max2Bytes

@Serializable
data class StatusResponse(
    @Serializable(VarString32767Max2Bytes::class)
    val json: String
) {
    constructor(status: Status) : this(JsonFormat.encodeToString(status))
}

@Serializable
data class Status(
    val version: Version,
    val players: Players,
    @Serializable(Chat.Serializer::class)
    val description: Chat,
    val enforcesSecureChat: Boolean,
    val previewsChat: Boolean,
) {
    @Serializable
    data class Version(val name: String, val protocol: Int)

    @Serializable
    data class Players(val max: Int, val online: Int, val sample: Array<Sample>) {
        @Serializable
        data class Sample(val name: String, val id: UUID)
    }
}
