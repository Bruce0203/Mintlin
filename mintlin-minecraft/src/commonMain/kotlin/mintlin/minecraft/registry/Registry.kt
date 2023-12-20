package mintlin.minecraft.registry

import mintlin.datastructure.scope.*
import mintlin.io.network.protocol.Bound
import mintlin.io.network.protocol.ProtocolEntry
import mintlin.io.network.protocol.protocolPacketRegistryOf
import mintlin.minecraft.packet.packets

class RegistryImp(scope: Scope) : Registry, Scope by scope

interface Registry : Scope {
    companion object : ScopeFactoryDSL<Registry, RegistryEntry> by scopedDSL({
        singleOf(::RegistryImp) bind Registry::class
        single { get<RegistryEntry>().bound }
        single { get<RegistryEntry>().protocolEntry }

        single { protocolPacketRegistryOf(bounded = get<Bound>(), *packets) }
    })
}

data class RegistryEntry(val protocolEntry: ProtocolEntry, val bound: Bound)