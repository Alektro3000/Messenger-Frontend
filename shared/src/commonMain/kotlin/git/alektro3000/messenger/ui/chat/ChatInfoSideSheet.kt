package git.alektro3000.messenger.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import git.alektro3000.messenger.extensions.toChatTimeText
import git.alektro3000.messenger.local.dao.ChatType
import git.alektro3000.messenger.model.chats.ChatFull
import git.alektro3000.messenger.model.chats.ChatMemberInfo
import git.alektro3000.messenger.ui.common.AvatarImage
import git.alektro3000.messenger.viewModel.ChatViewModel
import messenger.shared.generated.resources.Res
import messenger.shared.generated.resources.chat_direct_chat
import messenger.shared.generated.resources.chat_edit_display_name
import messenger.shared.generated.resources.chat_group_chat
import messenger.shared.generated.resources.chat_info
import messenger.shared.generated.resources.chat_members
import messenger.shared.generated.resources.chat_save_profile
import messenger.shared.generated.resources.chat_yesterday
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatInfoSideSheet(
    visible: Boolean,
    chat: ChatFull?,
    members: LazyPagingItems<ChatMemberInfo>,
    currentUserId: Long?,
    viewModel: ChatViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentChat = chat ?: return
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(180)),
            exit = fadeOut(animationSpec = tween(140))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f))
                    .clickable(onClick = onDismiss)
            )
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(180)) + slideInHorizontally(
                animationSpec = tween(240),
                initialOffsetX = { it }
            ),
            exit = fadeOut(animationSpec = tween(140)) + slideOutHorizontally(
                animationSpec = tween(220),
                targetOffsetX = { it }
            ),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .fillMaxHeight()
                    .clickable(enabled = false) {},
                tonalElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                val editable = currentChat.type == ChatType.Group
                val displayNameState = rememberTextFieldState(currentChat.displayName)

                LaunchedEffect(currentChat.displayName) {
                    if (displayNameState.text.toString() != currentChat.displayName) {
                        displayNameState.clearText()
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back"
                            )
                        }
                        Text(
                            text = stringResource(Res.string.chat_info),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "close"
                            )
                        }
                    }

                    if (currentChat.chatId != null && editable) {
                        ChatAvatarPickerUi(
                            avatarUrl = currentChat.avatarUrl,
                            viewModel = viewModel,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        AvatarImage(
                            avatarUrl = currentChat.avatarUrl,
                            modifier = Modifier.align(Alignment.CenterHorizontally).size(120.dp)
                        )
                    }

                    if (editable) {
                        OutlinedTextField(
                            value = displayNameState.text.toString(),
                            onValueChange = { displayNameState.edit { replace(0, length, it) } },
                            label = { Text(stringResource(Res.string.chat_edit_display_name)) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { viewModel.updateChatDisplayName(displayNameState.text.toString()) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(Res.string.chat_save_profile))
                        }
                    } else {
                        Text(
                            text = currentChat.displayName,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    Text(
                        text = when (currentChat.type) {
                            ChatType.Direct -> stringResource(Res.string.chat_direct_chat)
                            ChatType.Group -> stringResource(Res.string.chat_group_chat)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    currentChat.createdAt?.let {
                        Text(
                            text = stringResource(Res.string.chat_direct_chat)
                                    + it.toChatTimeText(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider()

                    Text(
                        text = stringResource(Res.string.chat_members),
                        style = MaterialTheme.typography.titleMedium
                    )

                    ChatMembersPanel(
                        members = members,
                        currentUserId = currentUserId,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
