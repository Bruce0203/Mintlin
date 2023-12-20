package mintlin.minecraft.server.server.list

import mintlin.datastructure.eventbus.Priority
import mintlin.lang.Init
import mintlin.minecraft.server.PlayerJoinEvent
import mintlin.minecraft.server.PlayerLeaveEvent
import mintlin.minecraft.server.player.Player
import mintlin.minecraft.server.server.Server

class PlayerList : MutableList<Player> by ArrayList()

class PlayerListFetcher(val server: Server, val list: PlayerList) : Init(server.listeners {
    server.onEvent<PlayerJoinEvent>(Priority.LOWEST) {
        list.add(it.player)
    }
    server.onEvent<PlayerLeaveEvent>(Priority.HIGHEST) {
        list.remove(it.player)
    }
})