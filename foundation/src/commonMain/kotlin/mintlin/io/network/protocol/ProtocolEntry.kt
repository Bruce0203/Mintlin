package mintlin.io.network.protocol

import mintlin.lang.classNameOf

interface ProtocolEntry {
    val id: Int
    val name: String

    data object NIL : ProtocolEntry by ProtocolEntryImp(id = -1, name = classNameOf<NIL>())
}

data class ProtocolEntryImp(override val id: Int, override val name: String) : ProtocolEntry
