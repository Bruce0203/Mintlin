package mintlin.minecraft.server.server

import mintlin.format.json.Json
import mintlin.lang.fastCastTo
import mintlin.minecraft.packet.LoginSuccess
import mintlin.serializer.UUID
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class HttpRequestThreadPool : Executor by Executors.newSingleThreadExecutor()

class MojangAPI(private val threadPool: HttpRequestThreadPool) {
    private val httpClient = mintlin.io.network.httpClient

    class PlayerInfo(map: Map<String, Any>) {
        val id: String by map
        val name: String by map
        val properties: Array<LoginSuccess.Property> by lazy {
            map[::properties.name].fastCastTo<List<Map<String, String>>>().map {
                LoginSuccess.Property(
                    name = it[LoginSuccess.Property::name.name]!!,
                    value = it[LoginSuccess.Property::value.name]!!,
                    signature = it[LoginSuccess.Property::signature.name]
                )
            }.toTypedArray()
        }
    }

    fun getPlayerInfoByUsername(username: String, serverId: String, callback: (PlayerInfo?) -> Unit) =
        threadPool.execute {
            val response = httpClient.getRequestAndGetBody(AUTH_URL.format(username, serverId))
            if (response.isEmpty()) callback(null)
            val map = Json.parseFast(response).fastCastTo<Map<String, String>>()
            callback(PlayerInfo(map))
        }

    fun getPlayerInfoByUUID(uuid: UUID, callback: (PlayerInfo?) -> Unit) = threadPool.execute {
        val uuidString = uuid.toString(includeBrackets = false)
        val response = httpClient.getRequestAndGetBody(FROM_USERNAME_URL.format(uuidString))
        if (response.isEmpty()) callback(null)
        val map = Json.parseFast(response).fastCastTo<Map<String, String>>()
        callback(PlayerInfo(map))
    }

    companion object {
        private const val FROM_USERNAME_URL =
            "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false"
        private const val AUTH_URL =
            "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s"
    }
}
