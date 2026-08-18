package git.alektro3000.messenger.ui.message

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import git.alektro3000.messenger.local.dao.ChatType
import git.alektro3000.messenger.local.dao.MessageStatus
import git.alektro3000.messenger.local.dao.MessageWithUser
import git.alektro3000.messenger.model.chats.ChatFull
import git.alektro3000.messenger.model.chats.MessagePreview
import git.alektro3000.messenger.model.chats.MessageUi
import git.alektro3000.messenger.model.chats.MessageUserPreview
import git.alektro3000.messenger.model.message.MessageAction
import androidx.compose.ui.Alignment

@Composable
fun MessageBubble(
    message: MessageWithUser,
    chat: ChatFull,
    userId: Long,
    messageEdit: () -> Unit,
    action: (MessageAction) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val messageUi = MessageUi(
        message = MessagePreview(
            message.message.serverId ?: 0,
            message.message.text,
            message.message.type,
            message.message.sendAt,
            message.message.editAt,
            message.message.deleteAt
        ),
        sender = MessageUserPreview(
            message.user.id,
            message.user.displayName,
            message.user.avatarUrl
        ),
        isMine = message.user.id == userId,
        status = message.message.status,
        showOtherNames = chat.type != ChatType.Direct
    )
    val interactionSource = remember { MutableInteractionSource() }
    MessageDisplay(
        message = messageUi,

        modifier = Modifier
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    menuExpanded = true
                },
                onLongClick = {
                }
            )
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.buttons.isSecondaryPressed) {
                            menuExpanded = true
                        }
                    }
                }
            },
    )
    {
        MessageDropDown(
            menuExpanded = menuExpanded,
            closeMenu = { menuExpanded = false },
            messageUi = messageUi,
            messageEdit = messageEdit,
            action = action
        )
    }
}


@Composable
fun MessageDropDown(
    menuExpanded: Boolean,
    closeMenu: () -> Unit,
    messageUi: MessageUi,
    messageEdit: () -> Unit,
    action: (MessageAction) -> Unit
) {
    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = closeMenu,
    ) {
        if (messageUi.isMine) {
            when (messageUi.status) {
                MessageStatus.Pending -> {

                    DropdownMenuItem(
                        text = { Text("Cancel sending") },
                        onClick = {
                            closeMenu()
                            action(MessageAction.Cancel)
                        }
                    )
                }

                MessageStatus.Error -> {
                    DropdownMenuItem(
                        text = { Text("Cancel sending") },
                        onClick = {
                            closeMenu()
                            action(MessageAction.Cancel)
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Repeat sending") },
                        onClick = {
                            closeMenu()
                            action(MessageAction.Resend)
                        }
                    )
                }

                else -> {

                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            closeMenu()
                            messageEdit()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            closeMenu()
                            action(MessageAction.Delete)
                        }
                    )
                }
            }
        } else {

            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    closeMenu()
                    action(MessageAction.Delete)
                }
            )
        }
    }
}