package git.alektro3000.messenger.network.dto

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class MessageResponse(
    val chatId: Long,
    val messageId: Long,
    val sendAt: Instant
)
@Serializable
data class MessageResponseWithId(
    val id: String,
    val mes: MessageResponse,
)

@Serializable
data class Message(
    val type: String,
    val text: String?
)


@Serializable
data class DeleteMessageResponse(
    val messageId: Long,
    val chatId: Long,
    val newLastMessageId: Long?,
    val deleteAt: Instant
)
@Serializable
data class EditMessageResponse(
    val messageId: Long,
    val newText: String,
    val editAt: Instant
)

@Serializable
data class EditTextRequest(
    val newText: String
)