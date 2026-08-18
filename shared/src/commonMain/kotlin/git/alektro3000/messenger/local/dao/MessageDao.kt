package git.alektro3000.messenger.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import git.alektro3000.messenger.model.user.UserFull
import kotlin.time.Clock
import kotlin.time.Instant

enum class MessageStatus {
    Pending,
    Sent,
    Read,
    Error
}

@Entity(
    tableName = "Messages",
    indices = [
        Index(value = ["serverId"], unique = true),
        Index(value = ["chatId"])
    ]
)
data class MessageEntity(
    @PrimaryKey
    val clientId: String,
    val serverId: Long? = null,

    val status: MessageStatus,

    val chatId: Long? = null,
    val receiverId: Long? = null,

    val senderId: Long,

    val text: String?,
    val type: String,

    val sendAt: Instant = Clock.System.now(),
    val editAt: Instant? = null,
    val deleteAt: Instant? = null,

)

data class MessageWithUser(
    @Embedded val message: MessageEntity,

    @Relation(
        parentColumn = "senderId",
        entityColumn = "id"
    )
    val user: UserEntity
)



@Dao
interface MessageDao {

    @Query("Delete FROM messages WHERE clientId = :id")
    suspend fun removeId(id: String?)
    @Query("SELECT * FROM messages WHERE clientId = :id")
    suspend fun findId(id: String?): MessageEntity?

    @Query("SELECT * FROM messages WHERE serverId = :id")
    suspend fun findId(id: Long): MessageEntity?
    @Query("DELETE FROM Messages")
    suspend fun clear()
    @Insert
    suspend fun insertMessage(message: MessageEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<MessageEntity>)

    @Query("""
        SELECT * FROM Messages
        WHERE chatId = :chatId and deleteAt is null
        ORDER BY (serverId IS NOT NULL), serverId DESC, clientId DESC
    """)
    fun pagingLocalMessages(chatId: Long): PagingSource<Int, MessageEntity>


    @Query("""
        SELECT * FROM Messages
        WHERE chatId = :chatId and deleteAt is null
        ORDER BY (serverId IS NOT NULL), serverId DESC, clientId DESC
    """)
    suspend fun localMessages(chatId: Long): List<MessageEntity>

    @Transaction
    @Query("""
        SELECT * FROM Messages
        WHERE
        (
            (:chatId IS NOT NULL AND chatId = :chatId)
            OR
            (:chatId IS NULL AND receiverId = :receiverId)
        )
        AND 
            deleteAt is null
        ORDER BY (serverId IS NOT NULL), serverId DESC, clientId DESC
    """)
    fun pagingLocalMessagesWithUsers(chatId: Long?, receiverId: Long?): PagingSource<Int, MessageWithUser>
    @Transaction
    @Query("""
        SELECT * FROM Messages
        WHERE 
        (
            (:chatId IS NOT NULL AND chatId = :chatId)
            OR
            (:chatId IS NULL AND receiverId = :receiverId)
        )
        AND 
            deleteAt is null
        ORDER BY (serverId IS NOT NULL), serverId DESC, clientId DESC
    """)
    suspend fun localMessagesWithUsers(chatId: Long?, receiverId: Long?): List<MessageWithUser>
    @Query("""
        UPDATE Messages
        SET
            serverId = :serverId,
            sendAt = :sendAt,
            chatId = :chatId,
            receiverId = NULL,
            status = :status
        WHERE clientId = :clientId
    """)
    suspend fun updateMessageFromServerResponse(
        clientId: String,
        serverId: Long,
        chatId: Long,
        sendAt: Instant,
        status: MessageStatus = MessageStatus.Sent)

    @Query("""
        UPDATE Messages
        SET
            status = :status
        WHERE clientId = :clientId
    """)
    suspend fun updateMessageFromErrorServerResponse(
        clientId: String,
        status: MessageStatus = MessageStatus.Error)

    @Query("""
        SELECT MIN(serverId)
        FROM Messages
        WHERE chatId = :chatId
          AND serverId IS NOT NULL
    """)
    suspend fun getOldestServerMessageId(chatId: Long): Long?


    @Query("Update messages SET text = null, deleteAt = :deleteAt WHERE serverId = :id")
    suspend fun deleteId(id: Long, deleteAt: Instant)

    @Query("Update messages SET text = :newText, editAt = :editAt WHERE serverId = :id")
    suspend fun editId(id: Long, newText: String, editAt: Instant)
}
