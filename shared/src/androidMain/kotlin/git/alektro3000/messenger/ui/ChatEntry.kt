package git.alektro3000.messenger.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import git.alektro3000.messenger.local.dao.ChatType
import git.alektro3000.messenger.model.chats.ChatEntryUI
import git.alektro3000.messenger.model.chats.MessagePreview
import git.alektro3000.messenger.model.chats.MessagePreviewResponse
import git.alektro3000.messenger.model.chats.MessageUserPreview
import git.alektro3000.messenger.ui.chats.ChatEntry
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@androidx.compose.runtime.Composable
@Preview(showBackground = true)
fun ChatEntryPreview(duration: Duration = 0.days , message: String = "Hi\nHi", unreadAmount: Int = 1)
{
    ChatEntry(
        ChatEntryUI(
            0,
            displayName = "marat",
            avatarUrl = "/uploads/avatars/user-6-250e2cc1-b1e2-4dc0-87ac-36b6706e073e.png",
            unreadMessageCount = unreadAmount,
            lastMessage =
                MessagePreviewResponse(
                    MessagePreview(
                        0, message, "Text",
                        Clock.System.now() - duration,
                        null,
                        null
                    ),
                    MessageUserPreview(
                        0,
                        "marat",
                        "/uploads/avatars/user-6-250e2cc1-b1e2-4dc0-87ac-36b6706e073e.png"
                    )
                ),
            type = ChatType.Direct,
            createdAt = Clock.System.now()
        ),
    )
}

@androidx.compose.runtime.Composable
@Preview(showBackground = true)
fun ChatEntryPreviewNoUnread() {
    ChatEntryPreview(0.hours, "Hi", 0)
}
@androidx.compose.runtime.Composable
@Preview(showBackground = true)
fun ChatEntryPreviewOneLine() {
    ChatEntryPreview(0.hours, "Hi")
}
@androidx.compose.runtime.Composable
@Preview(showBackground = true)
fun ChatEntryPreview1Hours() {
    ChatEntryPreview(1.hours)
}
@androidx.compose.runtime.Composable
@Preview(showBackground = true)
fun ChatEntryPreview24Hours() {
    ChatEntryPreview(24.hours)
}
@Composable
@Preview(showBackground = true)
fun ChatEntryPreview2Days() {
    ChatEntryPreview(2.days)
}
@Composable
@Preview(showBackground = true)
fun ChatEntryPreview3Days() {
    ChatEntryPreview(3.days)
}
@androidx.compose.runtime.Composable
@Preview(showBackground = true)
fun ChatEntryPreview4Days() {
    ChatEntryPreview(4.days)
}
@androidx.compose.runtime.Composable
@Preview(showBackground = true)
fun ChatEntryPreview304Days() {
    ChatEntryPreview(304.days)
}