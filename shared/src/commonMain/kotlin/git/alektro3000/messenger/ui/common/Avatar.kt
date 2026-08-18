package git.alektro3000.messenger.ui.common

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import coil3.compose.AsyncImage
import git.alektro3000.messenger.AppConfig
import git.alektro3000.messenger.ui.LocalImageLoader
import messenger.shared.generated.resources.Res
import messenger.shared.generated.resources.avatar_placeholder
import org.jetbrains.compose.resources.painterResource


@Composable
fun AvatarImage(avatarUrl: String?, modifier: Modifier = Modifier) {
    AsyncImage(
        model = avatarUrl?.let { AppConfig.HOST_URL + it },
        contentDescription = "Avatar",
        placeholder = painterResource(Res.drawable.avatar_placeholder),
        fallback = painterResource(Res.drawable.avatar_placeholder),
        imageLoader = LocalImageLoader.current,
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20))
    )
}
