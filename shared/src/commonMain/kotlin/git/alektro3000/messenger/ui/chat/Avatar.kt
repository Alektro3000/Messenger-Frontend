package git.alektro3000.messenger.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import git.alektro3000.messenger.viewModel.ChatViewModel

@Composable
expect fun ChatAvatarPickerUi(
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel
)
