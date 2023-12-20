package mintlin.io.network.protocol

enum class ConnectionState { Handshake, Status, Login, Configuration, Play, Closed; }

interface ConnectionStateChange {
    val connectionState: ConnectionState
}