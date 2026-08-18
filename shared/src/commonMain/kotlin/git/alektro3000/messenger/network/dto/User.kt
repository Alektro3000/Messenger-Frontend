package git.alektro3000.messenger.network.dto

import git.alektro3000.messenger.model.user.UserFull
import git.alektro3000.messenger.model.user.UserPreview
import kotlinx.serialization.Serializable
import javax.print.attribute.standard.RequestingUserName
import kotlin.time.Instant

@Serializable
data class UserPreviewResponse(
    val id: Long,
    val displayName: String,
    val avatarUrl: String?,
    val lastSeenAt: Instant?
)
@Serializable
data class AvatarUpdateResponse(
    val url: String,
    )



fun UserPreviewResponse.toUserPreview(): UserPreview
{
    return UserPreview(
        id = id,
        displayName = displayName,
        avatarUrl = avatarUrl,
        lastSeenAt = lastSeenAt,
    )
}

