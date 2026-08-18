package git.alektro3000.messenger.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import git.alektro3000.messenger.extensions.toChatTimeText
import git.alektro3000.messenger.model.chats.ChatMemberInfo
import git.alektro3000.messenger.ui.common.AvatarEntry
import git.alektro3000.messenger.ui.common.AvatarImage
import messenger.shared.generated.resources.Res
import messenger.shared.generated.resources.chat_direct_chat
import messenger.shared.generated.resources.chat_last_seen
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatMembersPanel(
    members: LazyPagingItems<ChatMemberInfo>,
    currentUserId: Long?,
    modifier: Modifier = Modifier
) {
    Column(
    ) {
        if (members.loadState.refresh is androidx.paging.LoadState.Loading && members.itemCount == 0) {
            CircularProgressIndicator()
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 240.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    count = members.itemCount,
                    key = { index -> members.peek(index)?.id ?: index }
                ) { index ->
                    val member = members[index] ?: return@items
                    ChatMemberRow(
                        member = member,
                        isCurrentUser = member.id == currentUserId
                    )
                }
            }
        }
    }

}

@Composable
private fun ChatMemberRow(
    member: ChatMemberInfo,
    isCurrentUser: Boolean,
) {
    AvatarEntry(member.avatarUrl)
    {
        Text(
            text = if (isCurrentUser) "${member.displayName} (you)" else member.displayName,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        val details = buildList {
            member.lastSeenAt?.let {
                add(stringResource(Res.string.chat_last_seen)
                        + it.toChatTimeText())
            }
        }
        if (details.isNotEmpty()) {
            Text(
                text = details.joinToString(" • "),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
}
