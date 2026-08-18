package git.alektro3000.messenger.ui

import androidx.compose.ui.tooling.preview.Preview
import git.alektro3000.messenger.local.dao.MessageStatus
import git.alektro3000.messenger.model.chats.MessagePreview
import git.alektro3000.messenger.model.chats.MessageUi
import git.alektro3000.messenger.model.chats.MessageUserPreview
import git.alektro3000.messenger.ui.message.MessageDisplay
import kotlin.time.Clock

private val sampleUser = MessageUserPreview(
    id = 0,
    displayName = "Someone",
    avatarUrl = null
)

fun sampleMessage(
    text: String,
    isMine: Boolean,
    status: MessageStatus
) = MessageUi(
    message = MessagePreview(
        0, text, "",
        Clock.System.now(),
        null,
        null
    ),
    sender = sampleUser,
    isMine = isMine,
    showOtherNames = true,
    status = status
)

@androidx.compose.runtime.Composable
@Preview
private fun MessageUIPreview() {
    MessageDisplay(
        sampleMessage(
            text = "HI",
            isMine = false,
            status = MessageStatus.Read
        )
    ){}
}

@androidx.compose.runtime.Composable
@Preview
private fun MessageUIPreviewLong() {
    MessageDisplay(
        sampleMessage(
            text = "HI Someone I did not know before. Like I don't want to know you at all",
            isMine = false,
            status = MessageStatus.Read
        )
    ){}
}
@androidx.compose.runtime.Composable
@Preview
private fun MessageUIPreviewMeSend() {
    MessageDisplay(
        sampleMessage(
            text = "HI",
            isMine = true,
            status = MessageStatus.Pending
        )
    ){}
}
@androidx.compose.runtime.Composable
@Preview
private fun MessageUIPreviewMe() {
    MessageDisplay(
        sampleMessage(
            text = "HI",
            isMine = true,
            status = MessageStatus.Sent
        )
    ){}
}
@androidx.compose.runtime.Composable
@Preview
private fun MessageUIPreviewMeRead() {
    MessageDisplay(
        sampleMessage(
            text = "HI",
            isMine = true,
            status = MessageStatus.Read
        )
    ){}
}
@androidx.compose.runtime.Composable
@Preview
private fun MessageUIPreviewMeError() {
    MessageDisplay(
        sampleMessage(
            text = "HI",
            isMine = true,
            status = MessageStatus.Error
        )
    ){}
}
@androidx.compose.runtime.Composable
@Preview
private fun MessageUIPreviewMeLong() {
    MessageDisplay(
        sampleMessage(
            text = "HI Someone I did not know before. Like I don't want to know you at all",
            isMine = true,
            status = MessageStatus.Read
        )
    ){}
}