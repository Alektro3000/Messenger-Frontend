package git.alektro3000.messenger.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import git.alektro3000.messenger.actions.PickedAvatar
import git.alektro3000.messenger.viewModel.ChatsViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import git.alektro3000.messenger.viewModel.ProfileViewModel
import io.ktor.http.ContentType
import io.ktor.http.defaultForFilePath
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
actual fun AvatarPickerUi(
    avatarUrl: String?,
    modifier: Modifier,
    viewModel: ProfileViewModel
) {
    var selectedImageFile by remember {
        mutableStateOf<File?>(null)
    }

    val avatarUiState by viewModel.avatarUiState.collectAsState()

    LaunchedEffect(avatarUiState) {
        if (avatarUiState is ProfileViewModel.AvatarUiState.Success) {
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
                viewModel.uploadAvatar(PickedAvatar(file.readBytes(), mimeType, fileName))
            }
        )
    }
}

private fun openImagePicker(): File? {
    val dialog = FileDialog(null as Frame?, "Select avatar", FileDialog.LOAD)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarConfirmDialog(
    file: File?,
    dismiss: () -> Unit,
    onUpload: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = dismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column {
                Box(modifier = Modifier.padding(4.dp)) {
                    AsyncImage(
                        model = file,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        colorFilter = ColorFilter.tint(
                            Color.Black.copy(alpha = 0.5f),
                            BlendMode.Darken
                        )
                    )

                    AsyncImage(
                        model = file,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                    )
                }

                Row(
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Button(onClick = onUpload) {
                        Text("Save")
                    }

                    TextButton(onClick = dismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}