package mintlin.minecraft.server.server

import mintlin.datastructure.eventbus.Priority
import mintlin.minecraft.server.BlockBreakEvent
import mintlin.minecraft.server.BlockPlaceEvent
import mintlin.lang.Init
import mintlin.minecraft.datastructure.level.World

class BlockUpdater(val server: Server, world: World) : Init(server.listeners {
    server.onEvent<BlockPlaceEvent>(Priority.LOW) { world.setBlockAt(it.position, it.block) }
    server.onEvent<BlockBreakEvent>(Priority.LOW) { world.setBlockAt(it.position, 0) }
})