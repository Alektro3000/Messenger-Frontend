package git.alektro3000.messenger.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant


enum class ChatType {
    Direct,
    Group
}

@Entity(
    tableName = "Chats",
)
data class ChatEntity(
    @PrimaryKey
    val id: Long,
    val receiverId: Long?,

    val type: ChatType,


    val displayName: String?,
    val avatarUrl: String?,

    val createdAt: Instant,

    val lastMessageId: Long,
    val unreadMessageCount: Int,
)



data class ChatWithLastMessage(
    @Embedded val chat: ChatEntity,

    @Embedded(prefix = "lastMessage_")
    val lastMessage: MessageEntity,

    @Relation(
        parentColumn = "lastMessage_senderId",
        entityColumn = "id"
    )
    val userOfLastMessage: UserEntity,

    @Relation(
        parentColumn = "receiverId",
        entityColumn = "id"
    )
    val receiverUser: UserEntity?
){
    val displayName: String
        get() = (receiverUser?.displayName ?: chat.displayName)!!

    val avatarUrl: String?
        get() = receiverUser?.avatarUrl ?: chat.avatarUrl
}
@Dao
interface ChatDao {
    @Query("DELETE FROM Chats")
    suspend fun clear()


    @Transaction
    @Query("""
    SELECT 
        Chats.*,

        M.clientId AS lastMessage_clientId,
        M.serverId AS lastMessage_serverId,
        M.status AS lastMessage_status,
        M.chatId AS lastMessage_chatId,
        M.receiverId AS lastMessage_receiverId,
        M.senderId AS lastMessage_senderId,
        M.text AS lastMessage_text,
        M.type AS lastMessage_type,
        M.sendAt AS lastMessage_sendAt

    FROM Chats
    LEFT JOIN Messages M
        ON M.clientId = (
            SELECT M2.clientId
            FROM Messages M2
            WHERE M2.chatId = Chats.id and M2.deleteAt IS NULL
            ORDER BY M2.sendAt DESC
            LIMIT 1
        )
    where displayName like '%' || :query || '%'
    ORDER BY M.sendAt IS NULL, M.sendAt DESC
""")
    fun pagingChats(query: String): PagingSource<Int, ChatWithLastMessage>


    @Transaction
    @Query("""    SELECT 
        Chats.*,

        M.clientId AS lastMessage_clientId,
        M.serverId AS lastMessage_serverId,
        M.status AS lastMessage_status,
        M.chatId AS lastMessage_chatId,
        M.receiverId AS lastMessage_receiverId,
        M.senderId AS lastMessage_senderId,
        M.text AS lastMessage_text,
        M.type AS lastMessage_type,
        M.sendAt AS lastMessage_sendAt

    FROM Chats
    LEFT JOIN Messages M
        ON M.clientId = (
            SELECT M2.clientId
            FROM Messages M2
            WHERE M2.chatId = Chats.id and M2.deleteAt IS NULL
            ORDER BY M2.sendAt DESC
            LIMIT 1
        )
    WHERE id = :chatId""")
    fun observeChat(chatId: Long): Flow<ChatWithLastMessage?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chats: List<ChatEntity>)


    @Upsert
    suspend fun upsert(chat: ChatEntity)

    @Upsert
    suspend fun upsertAll(chats: List<ChatEntity>)

    @Query("""
        SELECT id FROM Chats
        WHERE type = :type
        AND receiverId = :receiverId
        LIMIT 1
    """)
    suspend fun findDirectChatIdByReceiver(
        receiverId: Long,
        type: ChatType = ChatType.Direct
    ): Long?

    @Query("""
        SELECT id FROM Chats
        WHERE type = :type
        AND receiverId = :receiverId
        LIMIT 1""")
    fun observeChatId(receiverId: Long,
                      type: ChatType = ChatType.Direct): Flow<Long?>

    @Query("""
        Update Chats
        Set lastMessageId = :newLastMessageId
        Where id = :chatId
    """)
    suspend fun updateLastMessage(chatId: Long, newLastMessageId: Long?)
}
