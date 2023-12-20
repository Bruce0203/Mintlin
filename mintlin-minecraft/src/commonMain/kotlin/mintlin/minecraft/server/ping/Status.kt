package mintlin.minecraft.server.ping

import mintlin.datastructure.scope.*
import mintlin.lang.Init

interface Status {
    class StatusImp(scope: Scope) : Status, Init({
        scope.get<StatusRequestResponse>()
        scope.get<PingRequestResponse>()
    })

    companion object : ScopeFactoryDSL<Status, Unit> by scopedDSL({
        singleOf(::StatusImp) bind Status::class
        singleOf(::StatusRequestResponse)
        singleOf(::PingRequestResponse)
    })
}
