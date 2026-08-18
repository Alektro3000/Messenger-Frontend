package git.alektro3000.messenger.network.dto

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ChatMemberResponse(
    val user: UserPreviewResponse,
    val lastReadMessageId: Long?,
)

@Serializable
data class ChatFullResponse(
    val chatId: Long,
    val receiverId: Long?,
    val displayName: String,
    val avatarUrl: String?,
    val type: String,
    val createdAt: Instant,
    val lastMessageId: Long,
    val unreadMessageCount: Int,
    val chatMembers: List<ChatMemberResponse>,
)
