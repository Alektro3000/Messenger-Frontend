package git.alektro3000.messenger.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import git.alektro3000.messenger.actions.PickedAvatar
import git.alektro3000.messenger.ui.profile.AvatarConfirmDialog
import git.alektro3000.messenger.ui.profile.AvatarEditStateless
import git.alektro3000.messenger.viewModel.ChatViewModel
import io.ktor.http.ContentType
import io.ktor.http.defaultForFilePath
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
actual fun ChatAvatarPickerUi(
    avatarUrl: String?,
    modifier: Modifier,
    viewModel: ChatViewModel
) {
    var selectedImageFile by remember {
        mutableStateOf<File?>(null)
    }

    val editState by viewModel.chatEditState.collectAsState()

    LaunchedEffect(editState) {
        if (editState is ChatViewModel.ChatEditUiState.Success) {
            selectedImageFile = null
        }
    }

    AvatarEditStateless(
        avatarUrl = avatarUrl,
        modifier = modifier
    ) {
        selectedImageFile = openImagePicker()
    }

    selectedImageFile?.let { file ->
        AvatarConfirmDialog(
            file = file,
            dismiss = { selectedImageFile = null },
            onUpload = {
                val fileName = file.name
                val mimeType = ContentType.defaultForFilePath(file.name).toString()
                viewModel.uploadChatAvatar(PickedAvatar(file.readBytes(), mimeType, fileName))
            }
        )
    }
}

private fun openImagePicker(): File? {
    val dialog = FileDialog(null as Frame?, "Select chat avatar", FileDialog.LOAD)

    dialog.setFilenameFilter { _, name ->
        name.endsWith(".png", ignoreCase = true) ||
            name.endsWith(".jpg", ignoreCase = true) ||
            name.endsWith(".jpeg", ignoreCase = true) ||
            name.endsWith(".webp", ignoreCase = true)
    }

    dialog.isVisible = true

    val file = dialog.file ?: return null
    val directory = dialog.directory ?: return null

    return File(directory, file)
}
