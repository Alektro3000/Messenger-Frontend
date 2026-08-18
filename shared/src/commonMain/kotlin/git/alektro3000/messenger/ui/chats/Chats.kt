package git.alektro3000.messenger.ui.chats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddComment
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import git.alektro3000.messenger.CreateGroupRoute
import git.alektro3000.messenger.GroupChatRoute
import git.alektro3000.messenger.extensions.ifRefreshButtonPressed
import git.alektro3000.messenger.repository.toChatPreview
import git.alektro3000.messenger.ui.common.Header
import git.alektro3000.messenger.ui.common.HeaderDescription
import git.alektro3000.messenger.ui.common.SearchBar
import git.alektro3000.messenger.viewModel.ChatsViewModel
import messenger.shared.generated.resources.Res
import messenger.shared.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime


@ExperimentalTime
@Composable
fun ChatsScreen(
    description: HeaderDescription,
    onOpenCreateGroups: () -> Unit,
    onOpenChat: (Long) -> Unit,
    viewModel: ChatsViewModel
) {
    val chats = viewModel.chats.collectAsLazyPagingItems()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            Column{
                Header(
                    description,
                    stringResource(Res.string.app_name)
                )
                SearchBar(
                    onValueChanged = {
                        viewModel.updateChatQuery(it)
                    },
                    value = viewModel.chatQuery
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenCreateGroups,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddComment,
                    contentDescription = "New chat"
                )
            }
        },

        modifier = Modifier.onPreviewKeyEvent {
            it.ifRefreshButtonPressed {
                chats.refresh()
            }
        }
            .focusRequester(focusRequester)
            .focusable())
    { padding ->
        PullToRefreshBox(
            isRefreshing = chats.loadState.refresh is LoadState.Loading,
            onRefresh = {
                chats.refresh()
            },
        )
        {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize()
            ) {
                items(
                    count = chats.itemCount,
                    key = { index -> chats[index]?.chat?.id ?: index }
                ) { index ->
                    val chat = chats[index]

                    if (chat != null) {
                        ChatEntry(
                            chat = chat.toChatPreview(),
                            modifier = Modifier.clickable {
                                onOpenChat(chat.chat.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

