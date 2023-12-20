package mintlin.minecraft.server.server.list

import mintlin.datastructure.eventbus.Priority
import mintlin.lang.Init
import mintlin.minecraft.server.EntityRemoveEvent
import mintlin.minecraft.server.EntitySpawnEvent
import mintlin.minecraft.server.entity.Entity
import mintlin.minecraft.server.server.Server

class EntityList : MutableList<Entity> by ArrayList()

class EntityListFetcher(
    private val server: Server,
    private val entities: EntityList
) : Init(server.listeners {
    server.onEvent<EntitySpawnEvent>(Priority.LOWEST) {
        entities.add(it.entity)
    }
    server.onEvent<EntityRemoveEvent>(Priority.HIGHEST) {
        entities.remove(it.entity)
    }
})