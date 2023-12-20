package mintlin.minecraft.server.server.factory

import mintlin.datastructure.scope.Scope
import mintlin.minecraft.server.server.Server

class PlayerFactory(val scope: Scope, val server: Server) {
//    fun createPlayer(socketChannel: Connection): Player {
//        val koin = scope.getKoin()
//        val connectionScope = socketChannel.scope
//        val entityScope = koin.createScope<Entity>()
//        entityScope.declare(entityScope)
//        val playerScope = koin.createScope<Player>()
//        connectionScope.registerCallback(object : ScopeCallback {
//            override fun onScopeClose(scope: Scope) {
//                playerScope.close()
//                entityScope.close()
//            }
//        })
//        playerScope.declare(playerScope)
//        playerScope.linkTo(entityScope, connectionScope, server.scope)
//        var entityType by entityScope.get<EntityTypeManipulator>()
//        entityType = EntityType.Player
//        return playerScope.get<Player>()
//    }
}