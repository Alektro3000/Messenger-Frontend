package git.alektro3000.messenger.local.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import git.alektro3000.messenger.model.user.UserFull
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Long,
    val username: String?,
    val displayName: String,
    val avatarUrl: String?,
    val lastSeenAt: Instant?,
    val createdAt: Instant?,
    val name: String?,
    val surname: String?,
    val bio: String?,
) {
    fun toUserFull(): UserFull? {
        return UserFull(
            id = id,
            username = username ?: return null,
            displayName = displayName,
            name = name,
            surname = surname,
            bio = bio,
            avatarUrl = avatarUrl,
            createdAt = createdAt,
            lastSeenAt = lastSeenAt,
        )
    }
}

fun UserFull.toEntity(): UserEntity {
        return UserEntity(
            id = id,
            username = username,
            displayName = displayName,
            avatarUrl = avatarUrl,
            lastSeenAt = null,
            createdAt = createdAt,
            name = name,
            surname = surname,
            bio = bio
        )
}
@Dao
interface UserDao {
    @Query("DELETE FROM users")
    suspend fun clear()
    @Upsert
    suspend fun upsert(user: UserEntity)

    @Query("""
    INSERT INTO users (id, displayName, avatarUrl)
        VALUES (:id, :displayName, :avatarUrl)
        ON CONFLICT(id) DO UPDATE SET
            displayName = excluded.displayName,
            avatarUrl = excluded.avatarUrl
    """)
    suspend fun upsertBasic(
        id: Long,
        displayName: String,
        avatarUrl: String?
    )

    @Query("""
    INSERT INTO users (id, displayName, avatarUrl, lastSeenAt)
        VALUES (:id, :displayName, :avatarUrl, :lastSeenAt)
        ON CONFLICT(id) DO UPDATE SET
            displayName = excluded.displayName,
            avatarUrl = excluded.avatarUrl,
            lastSeenAt = excluded.lastSeenAt
    """)
    suspend fun upsertPreview(
        id: Long,
        displayName: String,
        avatarUrl: String?,
        lastSeenAt: Instant?
    )

    @Upsert
    suspend fun upsertAll(users: List<UserEntity>)

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUser(userId: Long): UserEntity?

    @Transaction
    @Query("Update users set avatarUrl = :url WHERE id = :userId")
    suspend fun updateAvatar(userId: Long, url: String?)

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeUser(userId: Long?) : Flow<UserEntity?>
}
