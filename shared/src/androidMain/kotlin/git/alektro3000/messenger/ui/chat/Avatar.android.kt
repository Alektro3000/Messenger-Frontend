package git.alektro3000.messenger.ui.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.ktor.http.ContentType
import git.alektro3000.messenger.actions.PickedAvatar
import git.alektro3000.messenger.ui.profile.AvatarConfirmDialog
import git.alektro3000.messenger.ui.profile.AvatarEditStateless
import git.alektro3000.messenger.ui.profile.SelectAvatar
import git.alektro3000.messenger.viewModel.ChatViewModel

@Composable
actual fun ChatAvatarPickerUi(
    avatarUrl: String?,
    modifier: androidx.compose.ui.Modifier,
    viewModel: ChatViewModel
) {
    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    val editState by viewModel.chatEditState.collectAsState()

    LaunchedEffect(editState) {
        if (editState is ChatViewModel.ChatEditUiState.Success) {
            selectedImageUri = null
        }
    }

    AvatarEditStateless(avatarUrl, modifier) {
        pickerLauncher.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    val context = LocalContext.current

    selectedImageUri?.let { uri ->
        AvatarConfirmDialog(
            uri,
            { selectedImageUri = null },
            { viewModel.uploadChatAvatar(SelectAvatar(context).getData(selectedImageUri!!)) }
        )
    }
}
