package git.alektro3000.messenger.network

import git.alektro3000.messenger.AppConfig
import git.alektro3000.messenger.network.ApiCommon.extractAppResult
import git.alektro3000.messenger.network.ApiCommon.extractEmptyAppResult
import git.alektro3000.messenger.network.dto.ApiResult
import git.alektro3000.messenger.network.dto.JwtResponse
import git.alektro3000.messenger.network.dto.LoginRequest
import git.alektro3000.messenger.network.dto.RefreshRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi (private val client: HttpClient) {

    suspend fun register(name: String, password: String) : ApiResult<Unit> {
        return extractEmptyAppResult(client.post(
            "${AppConfig.API_URL}/auth/register"
        ){
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(name, password))
        })
    }
    suspend fun logout(refreshToken: String): ApiResult<Unit> {
        return extractEmptyAppResult(
            client.post(
                "${AppConfig.API_URL}/auth/logout") {
                contentType(ContentType.Application.Json)
                setBody(RefreshRequest(refreshToken))
            }
        )
    }

    suspend fun login(name: String, password: String) : ApiResult<JwtResponse> {
        return extractAppResult(client.post(
            "${AppConfig.API_URL}/auth/login"
        ){
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(name, password))
        })
    }

    suspend fun refresh(request: String) : ApiResult<JwtResponse> {
        return extractAppResult(client.post(
            "${AppConfig.API_URL}/auth/refresh"
        ){
            contentType(ContentType.Application.Json)
            setBody(RefreshRequest(request))
        })
    }

}