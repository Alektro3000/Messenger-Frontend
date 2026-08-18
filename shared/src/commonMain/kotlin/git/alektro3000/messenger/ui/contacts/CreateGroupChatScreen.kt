package git.alektro3000.messenger.ui.contacts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import git.alektro3000.messenger.extensions.toLastSeenText
import git.alektro3000.messenger.model.user.UserPreview
import git.alektro3000.messenger.ui.common.AvatarEntry
import git.alektro3000.messenger.ui.common.SearchBar
import git.alektro3000.messenger.viewModel.CreateGroupChatViewModel
import messenger.shared.generated.resources.Res
import messenger.shared.generated.resources.contact_no_last_seen
import messenger.shared.generated.resources.group_create_group
import messenger.shared.generated.resources.group_group_name
import messenger.shared.generated.resources.group_no_group_name
import messenger.shared.generated.resources.group_select_users
import messenger.shared.generated.resources.group_selected_amount
import messenger.shared.generated.resources.group_tap_hint
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateGroupChatScreen(
    onCreateGroup: (Long) -> Unit,
    onDismiss: () -> Unit,
    viewModel: CreateGroupChatViewModel
) {
    val users = viewModel.users.collectAsLazyPagingItems()
    val selectedCount = viewModel.selectedUsers.size

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateGroupChatViewModel.Event.Created -> onCreateGroup(event.chatId)

                CreateGroupChatViewModel.Event.Dismiss -> onDismiss()
            }
        }
    }

    Scaffold(
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                FilledIconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back"
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(Res.string.group_selected_amount) + selectedCount,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            OutlinedTextField(
                value = viewModel.groupName,
                onValueChange = viewModel::updateGroupName,
                label = { Text(stringResource(Res.string.group_group_name)) },
                placeholder = { Text(stringResource(Res.string.group_no_group_name)) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = viewModel::createGroupChat,
                enabled = viewModel.groupName.isNotBlank() && selectedCount > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.group_create_group))
            }

            Text(
                text = stringResource(Res.string.group_select_users),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SearchBar(
                onValueChanged = viewModel::updateQuery,
                value = viewModel.query
            )
            PullToRefreshBox(
                isRefreshing = users.loadState.refresh is LoadState.Loading,
                onRefresh = { users.refresh() }
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.group_tap_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        items(
                            count = users.itemCount,
                            key = { index -> users[index]?.id ?: index }
                        ) { index ->
                            val user = users[index] ?: return@items
                            SelectableUserEntry(
                                user = user,
                                selected = viewModel.isSelected(user.id),
                                onToggle = { viewModel.toggleUser(user) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectableUserEntry(
    user: UserPreview,
    selected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        tonalElevation = if (selected) 3.dp else 0.dp,
        shape = MaterialTheme.shapes.large,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onToggle)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarEntry(user.avatarUrl, modifier = Modifier.weight(1f)) {
                Text(
                    user.displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.size(4.dp))

                Text(
                    (user.lastSeenAt?.toLastSeenText())
                        ?: stringResource(Res.string.contact_no_last_seen),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Checkbox(
                checked = selected,
                onCheckedChange = { onToggle() }
            )
        }
    }
}
