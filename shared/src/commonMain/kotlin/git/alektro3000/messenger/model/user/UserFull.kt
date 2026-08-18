package git.alektro3000.messenger.model.user

import git.alektro3000.messenger.local.dao.UserEntity
import kotlinx.serialization.Serializable
import kotlin.time.Instant


@Serializable
data class UserFull (
    val id: Long,
    val username: String,
    val displayName: String,
    val name: String?,
    val bio: String?,
    val surname: String?,
    val avatarUrl: String?,
    val createdAt: Instant?,
    val lastSeenAt: Instant?
) {
}

@Serializable
data class UserPreview (
    val id: Long,
    val displayName: String,
    val avatarUrl: String?,
    val lastSeenAt: Instant?

)