package git.alektro3000.messenger.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatCreateRequest(
    val displayName: String,
    val memberIds: List<Long>
)
