package mintlin.io.network

import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse


actual val httpClient: HttpClient = object : HttpClient {
    override fun getRequestAndGetBody(url: String): String {
        val client = java.net.http.HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI(url))
            .header("Content-Type", "application/json")
            .GET()
//            .POST(BodyPublishers.ofString(requestBody))
            .build()
        val response: HttpResponse<*> = client.send(request, HttpResponse.BodyHandlers.ofString())
        val responseString = response.body() as String
        return responseString
    }
}
