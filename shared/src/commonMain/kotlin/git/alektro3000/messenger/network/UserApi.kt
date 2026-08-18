package git.alektro3000.messenger.network

import git.alektro3000.messenger.AppConfig
import git.alektro3000.messenger.actions.PickedAvatar
import git.alektro3000.messenger.model.user.UserFull
import git.alektro3000.messenger.model.user.PickedProfile
import git.alektro3000.messenger.network.ApiCommon.extractAppResult
import git.alektro3000.messenger.network.ApiCommon.extractEmptyAppResult
import git.alektro3000.messenger.network.dto.ApiResult
import git.alektro3000.messenger.network.dto.AvatarUpdateResponse
import git.alektro3000.messenger.network.dto.UserPreviewResponse
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


class UserApi (private val client: HttpClient) {
    suspend fun me(): ApiResult<UserFull> {
        return extractAppResult(
            client.get(
                "${AppConfig.API_URL}/user/me"
            )
        )
    }

    suspend fun updateProfile(pickedProfile: PickedProfile) : ApiResult<Unit> {
        return extractEmptyAppResult(
            client.post(
                "${AppConfig.API_URL}/user/updateprofile"
            ) {
                contentType(ContentType.Application.Json)
                setBody(pickedProfile)
            }
        )
    }

    suspend fun getUsers(query: String, page: Int, pageSize: Int): ApiResult<List<UserPreviewResponse>> {
        return extractAppResult(
            client.get(
                "${AppConfig.API_URL}/user/query"
            ) {
                parameter("query", query)
                parameter("page", page)
                parameter("pageSize", pageSize)
            })
    }

    suspend fun uploadAvatar(avatar: PickedAvatar): ApiResult<AvatarUpdateResponse> {
        return extractAppResult(
            client.post(
                "${AppConfig.API_URL}/user/uploadavatar"
            ) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
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
}

