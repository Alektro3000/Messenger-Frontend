package git.alektro3000.messenger.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String
)
sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val message: String) : ApiResult<Nothing>
}
