package git.alektro3000.messenger.ui.chats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import git.alektro3000.messenger.extensions.toChatTimeText
import git.alektro3000.messenger.model.chats.ChatEntryUI
import git.alektro3000.messenger.ui.common.AvatarEntry

@Composable
fun ChatEntry(chat: ChatEntryUI, modifier: Modifier = Modifier) {
    AvatarEntry(chat.avatarUrl, modifier)
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = chat.displayName,
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = chat.lastMessage.message.sendAt.toChatTimeText(),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Top),
                text = chat.lastMessage.message.text ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.weight(1f))
            if (chat.unreadMessageCount != 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(bottom = 2.dp)
                )
                {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp)),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    {
                        Text(
                            modifier = Modifier
                                .padding(start = 4.dp, end = 4.dp, bottom = 2.dp, top = 0.dp)
                                .align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge,
                            text = chat.unreadMessageCount.toString(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

        }
    }
}

