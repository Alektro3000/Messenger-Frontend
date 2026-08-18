package git.alektro3000.messenger.model.chats

import git.alektro3000.messenger.local.dao.ChatEntity
import git.alektro3000.messenger.local.dao.ChatType
import git.alektro3000.messenger.local.dao.UserEntity
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ChatMemberInfo(
    val id: Long,
    val displayName: String,
    val avatarUrl: String?,
    val lastSeenAt: Instant?,
    val lastReadMessageId: Long?,
)

@Serializable
data class ChatFull(
    val chatId: Long?,
    val receiverId: Long?,
    val displayName: String,
    val avatarUrl: String?,
    val type: ChatType,
    val createdAt: Instant?,
)


fun UserEntity.toChatFull(): ChatFull {
    return ChatFull(
        chatId = null,
        receiverId = id,
        displayName = displayName,
        avatarUrl = avatarUrl,
        type = ChatType.Direct,
        createdAt = null,
    )
}
