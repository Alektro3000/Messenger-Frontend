package git.alektro3000.messenger.network

import git.alektro3000.messenger.AppConfig
import git.alektro3000.messenger.model.chats.ChatEntryPreviewInfo
import git.alektro3000.messenger.model.chats.MessagePreviewResponse
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Instant

@Serializable
sealed interface IncomingSocketEvent {
    @Serializable
    @SerialName("new_message")
    data class NewMessage(
        val chatId: Long,
        val message: MessagePreviewResponse
    ) : IncomingSocketEvent
    @Serializable
    @SerialName("new_chat")
    data class NewChat(
        val chat: ChatEntryPreviewInfo
    ) : IncomingSocketEvent
    @Serializable
    @SerialName("delete_message")
    data class DeleteMessage(
        val messageId: Long,
        val chatId: Long,
        val newLastMessageId: Long?,
        val deleteAt: Instant,
    ) : IncomingSocketEvent
    @Serializable
    @SerialName("edit_message")
    data class EditMessage(
        val messageId: Long,
        val newText: String,
        val editAt: Instant,
    ) : IncomingSocketEvent
}

class MessageSocketClient(
    private val client: HttpClient,
    ) {

    fun observeMessages(): Flow<IncomingSocketEvent> = flow {
        client.webSocket(
            method = HttpMethod.Get,
            host = AppConfig.WS_HOST,
            port = 443,
            path = "/ws",
            request = {
                url.protocol = AppConfig.WebSocketProtocol
            }
        ) {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        try
                        {
                            val text = frame.readText()
                            val event = Json.decodeFromString<IncomingSocketEvent>(text)
                            emit(event)
                        }
                        catch (ex: Exception)
                        {
                            println(ex)
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}
