package git.alektro3000.messenger.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class JwtResponse(
    val refreshToken: String,
    val accessToken: String,
    val tokenType: String,
    val expiresInMinutes: Int
)

@Serializable
data class RefreshRequest(
    val refreshToken: String
)
