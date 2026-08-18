package git.alektro3000.messenger.model.chats

import git.alektro3000.messenger.local.dao.ChatType
import git.alektro3000.messenger.local.dao.MessageStatus
import kotlinx.serialization.Serializable
import javax.print.attribute.standard.RequestingUserName
import kotlin.time.Instant

@Serializable
data class MessageUi(
    val message: MessagePreview,
    val sender: MessageUserPreview,
    val showOtherNames: Boolean,
    val isMine: Boolean,
    val status: MessageStatus
)
@Serializable
data class MessagePreview(
    val id: Long,
    val text: String?,
    val type: String,
    val sendAt: Instant,
    val editAt: Instant?,
    val deleteAt: Instant?
)
@Serializable
data class MessageUserPreview(
    val id: Long,
    val displayName: String,
    val avatarUrl: String?
)
@Serializable
data class MessagePreviewResponse(
    val message: MessagePreview,
    val sender: MessageUserPreview,
)


@Serializable
data class ChatEntryUI(
    val chatId: Long,
    val displayName: String,
    val avatarUrl: String?,
    val type: ChatType,
    val createdAt: Instant,
    val unreadMessageCount: Int,
    val lastMessage: MessagePreviewResponse,
)
@Serializable
data class ChatEntryPreviewInfo(
    val chatId: Long,
    val receiverId: Long?,
    val displayName: String,
    val avatarUrl: String?,
    val type: String,
    val createdAt: Instant,
    val unreadMessageCount: Int,
    val lastMessage: MessagePreviewResponse,
)

@Serializable
data class ChatInfo(
    val chatId: Long?,
    val receiverId: Long?,
    val displayName: String,
    val avatarUrl: String?,
    val chatType: ChatType,
)
@Serializable
data class ChatBaseInfo(
    val chatId: Long?,
    val receiverId: Long?
    )

@Serializable
data class MessageDraft(
    val text: String,
    val type: String
)