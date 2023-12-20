package mintlin.io.network

expect val clientSelectorFactory: ClientSelectorFactory

interface ClientSelectorFactory {
    fun createClientSelector(listener: SocketListener): ClientSelector
}

interface ClientSelector : Selector {
    fun createClient(host: String, port: Int): SocketChannel
}
