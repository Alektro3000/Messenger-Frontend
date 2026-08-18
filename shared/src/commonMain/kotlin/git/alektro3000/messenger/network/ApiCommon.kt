package git.alektro3000.messenger.network;

import git.alektro3000.messenger.network.dto.ApiResult
import git.alektro3000.messenger.network.dto.ErrorResponse
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

object ApiCommon {

    suspend inline fun <reified T> extractAppResult(response: HttpResponse) : ApiResult<T>
    {
        return if (response.status.isSuccess()) {
            ApiResult.Success(response.body<T>())
        } else {
            ApiResult.Error(response.body<String>())
        }
    }

     suspend fun extractEmptyAppResult(
        response: HttpResponse
    ): ApiResult<Unit> {
        return if (response.status.isSuccess()) {
            ApiResult.Success(Unit)
        } else {
            ApiResult.Error(response.body<String>())
        }
    }
}
