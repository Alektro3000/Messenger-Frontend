package git.alektro3000.messenger.network

import git.alektro3000.messenger.AppConfig
import git.alektro3000.messenger.model.chats.MessageDraft
import git.alektro3000.messenger.model.chats.MessagePreviewResponse
import git.alektro3000.messenger.network.ApiCommon.extractAppResult
import git.alektro3000.messenger.network.dto.ApiResult
import git.alektro3000.messenger.network.dto.DeleteMessageResponse
import git.alektro3000.messenger.network.dto.EditMessageResponse
import git.alektro3000.messenger.network.dto.EditTextRequest
import git.alektro3000.messenger.network.dto.MessageResponseWithId
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType


class MessageApi (private val client: HttpClient)
{
    suspend fun sendMessageChat(chatId: Long, localId: String, request: MessageDraft): ApiResult<MessageResponseWithId>
    {
        return extractAppResult(
            client.post(
                "${AppConfig.API_URL}/message/send"
            ) {
                parameter("clientId", localId)
                parameter("chatId", chatId)
                contentType(ContentType.Application.Json)
                setBody(request)
            })
    }

    suspend fun sendMessageDirect(receiverId: Long, localId: String, request: MessageDraft): ApiResult<MessageResponseWithId>
    {
        return extractAppResult(
            client.post(
                "${AppConfig.API_URL}/message/direct"
            ) {
                parameter("clientId", localId)
                parameter("receiverId", receiverId)
                contentType(ContentType.Application.Json)
                setBody(request)
            })
    }

    suspend fun getMessages(chatId: Long, beforeMessageId: Long?, pageSize: Int): ApiResult<List<MessagePreviewResponse> >
    {
        return extractAppResult(
            client.get(
                "${AppConfig.API_URL}/message/get"
            ) {
                parameter("chatId", chatId)
                parameter("beforeMessageId", beforeMessageId)
                parameter("pageSize", pageSize)
            })
    }

    suspend fun deleteMessage(messageId: Long, chatId: Long): ApiResult<DeleteMessageResponse>
    {
        return extractAppResult(
            client.delete(
                "${AppConfig.API_URL}/message/delete"
            ) {
                parameter("messageId", messageId)
                parameter("chatId", chatId)
            })
    }
    suspend fun editTextMessage(messageId: Long, chatId: Long, newText: String): ApiResult<EditMessageResponse >
    {
        return extractAppResult(
            client.patch(
                "${AppConfig.API_URL}/message/edittext"
            ) {
                parameter("messageId", messageId)
                parameter("chatId", chatId)
                contentType(ContentType.Application.Json)
                setBody(EditTextRequest(newText))
            })
    }
}