package git.alektro3000.messenger.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import git.alektro3000.messenger.AppConfig
import git.alektro3000.messenger.actions.PickedAvatar
import git.alektro3000.messenger.ui.LocalImageLoader
import git.alektro3000.messenger.viewModel.ChatsViewModel
import git.alektro3000.messenger.viewModel.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@Composable
actual fun AvatarPickerUi(
    avatarUrl: String?,
    modifier: Modifier,
    viewModel: ProfileViewModel
) {

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    val avatarUiState by viewModel.avatarUiState.collectAsState()

    LaunchedEffect(avatarUiState) {
        if (avatarUiState is ProfileViewModel.AvatarUiState.Success) {
            selectedImageUri = null
        }
    }

    AvatarEditStateless(avatarUrl, modifier)
    {
        pickerLauncher.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }
    val context = LocalContext.current

    selectedImageUri?.let { uri ->
        AvatarConfirmDialog(uri,
            {selectedImageUri = null},
            {viewModel.uploadAvatar(SelectAvatar(context).getData(selectedImageUri!!))})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarConfirmDialog(
    uri: Uri?,
    dismiss: () -> Unit,
    onUpload: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = dismiss,
        sheetState = sheetState
    ){
        Card(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)) {
            Column {
                Box(modifier = Modifier.padding(4.dp))
                {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        colorFilter = ColorFilter.tint(
                            Color.Black.copy(alpha = 0.5f),
                            BlendMode.Darken
                        )
                    )
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                            .clip(CircleShape)
                    )
                }
                Row(Modifier.align(Alignment.End)) {
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


@OptIn(ExperimentalTime::class)
@Composable
@Preview(showBackground = true)
fun PreviewAvatarEditAlarm()
{
    AvatarConfirmDialog(
        null,
        {},{}
    )
}