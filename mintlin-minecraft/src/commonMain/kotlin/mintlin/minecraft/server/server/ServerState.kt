package mintlin.minecraft.server.server

import mintlin.datastructure.scope.Scope
import mintlin.datastructure.scope.get
import mintlin.lang.Init
import mintlin.minecraft.datastructure.StringComponent
import mintlin.minecraft.server.server.list.EntityListFetcher
import mintlin.minecraft.server.server.list.PlayerListFetcher

class ServerState(scope: Scope) : Init(with(scope) {
    get<BlockUpdater>()
    get<ChunkLoader>()
    get<PlayerListFetcher>()
    get<EntityListFetcher>()
})