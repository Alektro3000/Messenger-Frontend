package git.alektro3000.messenger.ui.message


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ScheduleSend
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import git.alektro3000.messenger.extensions.toMessageTime
import git.alektro3000.messenger.local.dao.MessageStatus
import git.alektro3000.messenger.model.chats.MessageUi
import messenger.shared.generated.resources.Res
import messenger.shared.generated.resources.no_message
import org.jetbrains.compose.resources.stringResource


@Composable
fun MessageDisplay(message: MessageUi, modifier: Modifier = Modifier, dropDown: @Composable () -> Unit) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
            .padding(vertical = 4.dp),
    )
    {
        Surface(
            color =
                if (message.isMine) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .widthIn(max = maxWidth * 0.7f)
                .align(if (message.isMine) Alignment.CenterEnd else Alignment.CenterStart)
                .clip(RoundedCornerShape(14.dp))
        ) {
            Box()
            {
                Column(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    message.sender.displayName.let {
                        if (!message.isMine && message.showOtherNames)
                            Text(
                                text = it,
                                modifier = Modifier,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall
                            )
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                    )
                    {
                        SelectionContainer {
                            Text(
                                text = message.message.text
                                    ?: stringResource(Res.string.no_message),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                        Row(
                            modifier = Modifier.align(Alignment.Bottom)
                        )
                        {
                            Text(
                                text = message.message.sendAt.toMessageTime(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            if (message.isMine) {
                                MessageStatusIcon(
                                    message.status,
                                    modifier = Modifier
                                        .height(18.dp)
                                )
                            }
                        }
                    }
                }

                dropDown()
            }
        }
    }
}

@Composable
private fun MessageStatusIcon(status: MessageStatus, modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.primary
    when (status) {
        MessageStatus.Pending ->
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ScheduleSend,
                tint = color,
                contentDescription = "status_pending",
                modifier = modifier
            )

        MessageStatus.Sent ->
            Icon(
                imageVector = Icons.Default.Check,
                tint = color,
                contentDescription = "status_sent",
                modifier = modifier
            )

        MessageStatus.Error ->
            Icon(
                imageVector = Icons.Default.Error,
                tint = color,
                contentDescription = "status_sent",
                modifier = modifier
            )

        MessageStatus.Read ->
            Box(
                modifier = modifier.aspectRatio(1.33f, true)
            )
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = color,
                    contentDescription = "status_read",
                )
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = color,
                    contentDescription = "status_read",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
    }
}
