package mintlin.io.network

expect val httpClient: HttpClient

interface HttpClient {
    fun getRequestAndGetBody(url: String): String
}