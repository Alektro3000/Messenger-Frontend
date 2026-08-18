package git.alektro3000.messenger.network

import git.alektro3000.messenger.AppConfig
import git.alektro3000.messenger.actions.PickedAvatar
import git.alektro3000.messenger.network.dto.AvatarUpdateResponse
import git.alektro3000.messenger.network.dto.ChatFullResponse
import git.alektro3000.messenger.network.dto.ChatCreateRequest
import git.alektro3000.messenger.network.dto.ChatUpdateRequest
import git.alektro3000.messenger.model.chats.ChatEntryPreviewInfo
import git.alektro3000.messenger.network.ApiCommon.extractAppResult
import git.alektro3000.messenger.network.dto.ApiResult
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType


class ChatApi (private val client: HttpClient) {

    suspend fun getChats(page: Int, pageSize: Int, query: String): ApiResult<List<ChatEntryPreviewInfo>> {
        return extractAppResult(
            client.get(
                "${AppConfig.API_URL}/chat/chats"
            ) {
                parameter("page", page)
                parameter("pageSize", pageSize)
                parameter("query", query)
            })
    }

    suspend fun getChatInfo(chatId: Long): ApiResult<ChatFullResponse> {
        return extractAppResult(
            client.get(
                "${AppConfig.API_URL}/chat/full"
            ) {
                parameter("chatId", chatId)
            }
        )
    }

    suspend fun updateChatInfo(chatId: Long, displayName: String): ApiResult<ChatFullResponse> {
        return extractAppResult(
            client.post(
                "${AppConfig.API_URL}/chat/update"
            ) {
                contentType(ContentType.Application.Json)
                setBody(ChatUpdateRequest(chatId, displayName))
            }
        )
    }

    suspend fun uploadChatAvatar(chatId: Long, avatar: PickedAvatar): ApiResult<ChatFullResponse> {
        return extractAppResult(
            client.post(
                "${AppConfig.API_URL}/chat/uploadavatar"
            ) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("chatId", chatId.toString())
                            append(
                                "avatar",
                                avatar.bytes,
                                Headers.build {
                                    append(HttpHeaders.ContentType, avatar.mimeType)
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        ContentDisposition.File
                                            .withParameter(ContentDisposition.Parameters.Name, "avatar")
                                            .withParameter(ContentDisposition.Parameters.FileName, avatar.fileName)
                                            .toString()
                                    )
                                }
                            )
                        }
                    )
                )
            }
        )
    }

    suspend fun createGroupChat(displayName: String, memberIds: List<Long>): ApiResult<ChatFullResponse> {
        return extractAppResult(
            client.post(
                "${AppConfig.API_URL}/chat/group"
            ) {
                contentType(ContentType.Application.Json)
                setBody(ChatCreateRequest(displayName, memberIds))
            }
        )
    }
}
