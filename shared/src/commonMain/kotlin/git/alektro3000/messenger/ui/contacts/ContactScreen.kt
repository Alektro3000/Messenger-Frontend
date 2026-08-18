package git.alektro3000.messenger.ui.contacts

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import git.alektro3000.messenger.AppConfig
import git.alektro3000.messenger.DirectChatRoute
import git.alektro3000.messenger.extensions.toChatTimeText
import git.alektro3000.messenger.extensions.toLastSeenText
import git.alektro3000.messenger.model.user.UserFull
import git.alektro3000.messenger.model.user.UserPreview
import git.alektro3000.messenger.ui.common.AvatarEntry
import git.alektro3000.messenger.ui.common.AvatarImage
import git.alektro3000.messenger.ui.common.Header
import git.alektro3000.messenger.ui.common.HeaderDescription
import git.alektro3000.messenger.ui.common.SearchBar
import git.alektro3000.messenger.viewModel.UsersViewModel
import messenger.shared.generated.resources.Res
import messenger.shared.generated.resources.app_name
import messenger.shared.generated.resources.common_search
import messenger.shared.generated.resources.contact_last_seen
import messenger.shared.generated.resources.contact_no_last_seen
import messenger.shared.generated.resources.nav_contacts
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ContactScreen(
    description: HeaderDescription,
    onOpenDirect: (Long) -> Unit,
    viewModel: UsersViewModel
) {
    val users = viewModel.users.collectAsLazyPagingItems()
    Scaffold(topBar = {
        Column {
            Header(
                description,
                stringResource(Res.string.nav_contacts)
            )
            SearchBar(
                onValueChanged = {
                    viewModel.updateQuery(it)
                },
                value = viewModel.query
            )
        }
    })
    {
        LazyColumn(
            modifier = Modifier.padding(it),
        ) {
            items(
                count = users.itemCount,
                key = { index -> users[index]?.id ?: index }
            ) { index ->
                val user = users[index]

                if (user != null) {
                    UserEntry(
                        user = user,
                        onClick = {
                            onOpenDirect(user.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserEntry(user: UserPreview, onClick: (UserPreview) -> Unit) {
    AvatarEntry(user.avatarUrl, modifier = Modifier
        .padding(4.dp)
        .clickable {
        onClick(user)
    })
    {
        Text(
            user.displayName,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.size(4.dp))

        Text(
            user.lastSeenAt?.toLastSeenText() ?: stringResource(Res.string.contact_no_last_seen),
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
@Preview
fun UserEntryPreview() {
    UserEntry(
        UserPreview(
            0,
            displayName = "DisplayName",
            avatarUrl = "/uploads/avatars/user-6-250e2cc1-b1e2-4dc0-87ac-36b6706e073e.png",
            lastSeenAt = null
        ),
        {}
    )
}