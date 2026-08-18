package git.alektro3000.messenger.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Upsert
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "ChatMembers",
    primaryKeys = ["chatId", "userId"],
    indices = [
        Index(value = ["chatId"]),
        Index(value = ["userId"]),
    ]
)
data class ChatMemberEntity(
    val userId: Long,
    val chatId: Long,
    val lastReadMessageId: Long?,
)

data class ChatMemberWithUser(
    @Embedded val member: ChatMemberEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: UserEntity
)

@Dao
interface ChatMemberDao {
    @Query("DELETE FROM ChatMembers")
    suspend fun clear()

    @Upsert
    suspend fun upsertAll(members: List<ChatMemberEntity>)

    @Transaction
    @Query("""
        SELECT * FROM ChatMembers
        WHERE chatId = :chatId
        ORDER BY userId ASC
    """)
    fun pagingChatMembers(chatId: Long): PagingSource<Int, ChatMemberWithUser>
}
