package git.alektro3000.messenger.ui.chat

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import git.alektro3000.messenger.local.dao.ChatType
import git.alektro3000.messenger.model.chats.ChatFull
import git.alektro3000.messenger.model.message.MessageAction
import git.alektro3000.messenger.ui.message.MessageBubble
import git.alektro3000.messenger.viewModel.ChatViewModel

@Composable
fun ChatScreen(
    onReturn: (() -> Unit)?,
    viewModel: ChatViewModel
) {

    val messages = viewModel.messages.collectAsLazyPagingItems()
    val chat by viewModel.chat.collectAsState(null)
    val members = viewModel.chatMembers.collectAsLazyPagingItems()
    val userId by viewModel.userId.collectAsStateWithLifecycle(null)

    var shouldAutoScroll by remember { mutableStateOf(true) }
    var previousItemCount by remember { mutableIntStateOf(messages.itemCount) }
    var chatInfoVisible by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val textState = rememberTextFieldState()
    var editingMessage by remember{ mutableStateOf<String?>(null) }

    LaunchedEffect(chat?.chatId, chat?.receiverId) {
        chatInfoVisible = false
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { scrolling ->
                if (scrolling) {
                    shouldAutoScroll =
                        listState.firstVisibleItemIndex == 0 &&
                                listState.firstVisibleItemScrollOffset < 50
                }
            }
    }

    LaunchedEffect(messages.itemCount) {
        if (shouldAutoScroll && messages.itemCount > previousItemCount) {
            listState.animateScrollToItem(0)
        }

        previousItemCount = messages.itemCount
    }
    LaunchedEffect(messages.itemCount) {
        val userIsNearBottom =
            listState.firstVisibleItemIndex <= 1

        if (messages.itemCount > 0 && userIsNearBottom) {
            listState.animateScrollToItem(0)
        }
    }

    Box(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        )
        {
            if (chat == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@BoxWithConstraints
            }

            val maxChatHeight = maxHeight * 0.5f
            Column {
                LazyColumn(
                    state = listState,
                    reverseLayout = true,
                    verticalArrangement = Arrangement.Bottom,
                    contentPadding = PaddingValues(
                        top = 64.dp,      // space under floating header
                        bottom = 8.dp,
                        start = 8.dp,
                        end = 8.dp
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        count = messages.itemCount,
                        key = { index -> messages[index]?.message?.clientId ?: index }
                    ) { index ->
                        val message = messages[index] ?: return@items
                        MessageBubble(message, chat!!, userId!!,
                            messageEdit = {
                                editingMessage = message.message.clientId
                                textState.clearText()
                                textState.edit {
                                    append(message.message.text ?: "")
                                }
                            }
                            ) { messageAction ->
                            viewModel.messageAction(message.message.clientId, messageAction)
                        }
                    }
                }
                ChatScreenSend(
                    isEditingMessage = editingMessage != null,
                    textState = textState,
                    onCancelMessageEdit = {
                        editingMessage = null
                    },
                    onMessageSend = {
                        if(editingMessage == null)
                            viewModel.sendMessage(it)
                        else {
                            viewModel.messageAction(
                                editingMessage,
                                MessageAction.EditText(
                                    it
                                )
                            )
                            editingMessage = null
                        }
                        textState.clearText()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp)
                        .heightIn(max = maxChatHeight)
                )
            }

        }
        ChatScreenHeader(
            onReturn,
            chat = chat,
                onMembersClick = {
                if (chat?.chatId != null) {
                    chatInfoVisible = !chatInfoVisible
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(8.dp)
        )

        ChatInfoSideSheet(
            visible = chatInfoVisible,
            chat = chat,
            members = members,
            currentUserId = userId,
            viewModel = viewModel,
            onDismiss = { chatInfoVisible = false }
        )
    }
}

@Composable
fun ChatScreenHeader(
    onReturn: (() -> Unit)?,
    chat: ChatFull?,
    onMembersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
    )
    {
        onReturn?.let {
            FilledIconButton(
                onClick = onReturn
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "return"
                )
            }
        }
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)) {
            chat?.let {
                Text(
                    chat.displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
                )
            }
        }

        if (chat?.chatId != null) {
            FilledIconButton(
                onClick = onMembersClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "chat members"
                )
            }
        }

    }
}

@Composable
fun ChatScreenSend(
    isEditingMessage: Boolean,
    onCancelMessageEdit: () -> Unit,
    textState: TextFieldState,
    onMessageSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row()
        {
            if(isEditingMessage)
            {
                IconButton(
                    onClick = onCancelMessageEdit
                ) {
                    Icon(
                        imageVector = Icons.Filled.Cancel,
                        contentDescription = "cancel edit"
                    )
                }

            }
            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEmotions,
                    contentDescription = "add_emoji"
                )
            }

            BasicTextField(
                state = textState,
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(4.dp)
                    .weight(1f)
            )

            IconButton(
                onClick = {
                    onMessageSend(textState.text.toString())
                    textState.clearText()
                }
            ) {
                Icon(
                    imageVector =
                        if(isEditingMessage)
                            Icons.Default.Edit
                        else
                            Icons.AutoMirrored.Filled.Send,
                    contentDescription = "send"
                )
            }
        }
    }
}
