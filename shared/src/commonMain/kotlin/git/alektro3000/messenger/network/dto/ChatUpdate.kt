package git.alektro3000.messenger.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatUpdateRequest(
    val chatId: Long,
    val displayName: String,
)
