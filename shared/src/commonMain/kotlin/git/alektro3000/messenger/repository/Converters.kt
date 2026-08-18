package git.alektro3000.messenger.repository

import git.alektro3000.messenger.local.dao.ChatEntity
import git.alektro3000.messenger.local.dao.ChatType
import git.alektro3000.messenger.local.dao.ChatMemberEntity
import git.alektro3000.messenger.local.dao.ChatMemberWithUser
import git.alektro3000.messenger.local.dao.ChatWithLastMessage
import git.alektro3000.messenger.local.dao.MessageEntity
import git.alektro3000.messenger.local.dao.MessageStatus
import git.alektro3000.messenger.local.dao.MessageWithUser
import git.alektro3000.messenger.local.dao.UserEntity
import git.alektro3000.messenger.model.chats.ChatEntryPreviewInfo
import git.alektro3000.messenger.model.chats.ChatMemberInfo
import git.alektro3000.messenger.model.chats.ChatEntryUI
import git.alektro3000.messenger.model.chats.ChatFull
import git.alektro3000.messenger.model.chats.MessagePreview
import git.alektro3000.messenger.model.chats.MessagePreviewResponse
import git.alektro3000.messenger.model.chats.MessageUserPreview
import git.alektro3000.messenger.network.dto.ChatFullResponse
import git.alektro3000.messenger.network.dto.ChatMemberResponse


fun MessagePreviewResponse.toMessageEntity(chatId: Long): MessageEntity {
    return MessageEntity(
        clientId = "s:" + message.id.toString(),
        serverId = message.id,
        status = MessageStatus.Sent,

        chatId = chatId,
        receiverId = null,

        senderId = sender.id,

        text = message.text,
        type = message.type,

        sendAt = message.sendAt,
        editAt = message.editAt,
        deleteAt = message.deleteAt,
    )
}

fun MessageEntity.toMessagePreview(): MessagePreview {
    return MessagePreview(
        id = serverId ?: -1,
        text = text,
        type = type,
        sendAt = sendAt,
        editAt = editAt,
        deleteAt = deleteAt,
    )
}

fun MessageEntity.toMessageWithUserPreview(user: UserEntity): MessagePreviewResponse {
    return MessagePreviewResponse(
        message = this.toMessagePreview(),
        sender = user.toUserPreview()
    )
}


fun UserEntity.toUserPreview(): MessageUserPreview {
    return MessageUserPreview(
        id = id,
        displayName = displayName,
        avatarUrl = avatarUrl
    )
}

fun MessageWithUser.toMessagePreviewResponse(): MessagePreviewResponse {
    return MessagePreviewResponse(
        message = message.toMessagePreview(),
        sender = user.toUserPreview()
    )
}

fun ChatEntryPreviewInfo.toChatEntity(): ChatEntity {
    return ChatEntity(
        id = chatId,
        receiverId = receiverId,
        displayName = displayName,
        avatarUrl = avatarUrl,
        lastMessageId = this.lastMessage.message.id,
        type = enumValueOf<ChatType>(type),
        createdAt = createdAt,
        unreadMessageCount = unreadMessageCount
    )
}

fun ChatFullResponse.toChatEntity(): ChatEntity {
    return ChatEntity(
        id = chatId,
        receiverId = receiverId,
        displayName = displayName,
        avatarUrl = avatarUrl,
        lastMessageId = lastMessageId,
        type = enumValueOf<ChatType>(type),
        createdAt = createdAt,
        unreadMessageCount = unreadMessageCount,
    )
}

fun ChatEntryPreviewInfo.toMessageEntity(): MessageEntity {
    return lastMessage.toMessageEntity(chatId)
}

fun ChatWithLastMessage.toChatPreview(): ChatEntryUI {
    return ChatEntryUI(
        chatId = chat.id,
        displayName = displayName,
        avatarUrl = avatarUrl,
        type = chat.type,
        createdAt = chat.createdAt,
        unreadMessageCount = chat.unreadMessageCount,
        lastMessage = lastMessage.toMessageWithUserPreview(userOfLastMessage)
    )
}
fun ChatWithLastMessage.toChatFull(): ChatFull {
    return ChatFull(
        chatId = chat.id,
        receiverId = chat.receiverId,
        displayName = displayName,
        avatarUrl = avatarUrl,
        type = chat.type,
        createdAt = chat.createdAt,
    )
}

fun ChatMemberResponse.toChatMemberEntity(chatId: Long): ChatMemberEntity {
    return ChatMemberEntity(
        userId = user.id,
        chatId = chatId,
        lastReadMessageId = lastReadMessageId,
    )
}

fun ChatMemberWithUser.toChatMemberInfo(): ChatMemberInfo {
    return ChatMemberInfo(
        id = user.id,
        displayName = user.displayName,
        avatarUrl = user.avatarUrl,
        lastSeenAt = user.lastSeenAt,
        lastReadMessageId = member.lastReadMessageId,
    )
}
