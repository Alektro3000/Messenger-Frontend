package git.alektro3000.messenger.model.user

import kotlinx.serialization.Serializable

@Serializable
data class PickedProfile(
    val name: String,
    val surname: String,
    val bio: String
)