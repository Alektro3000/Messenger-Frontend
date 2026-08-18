package git.alektro3000.messenger

import io.ktor.http.URLProtocol
import java.net.http.WebSocket

expect val HOST: String
expect val Port: Int

expect val USEHTTPS: Boolean

object AppConfig {
    private val EndPointProtocol = if(USEHTTPS) "https" else "http"
    val WebSocketProtocol = if(USEHTTPS) URLProtocol.WSS else URLProtocol.WS
    val HOST_URL: String = "${EndPointProtocol}://$HOST:${Port}"
    val API_URL: String = "$HOST_URL/api/v1"

    val WS_HOST = HOST
}