package git.alektro3000.messenger.model.message

sealed interface MessageAction
{
    object Delete: MessageAction
    object Resend: MessageAction
    object Cancel: MessageAction
    class EditText(
        val newTextMessage: String
    ) : MessageAction
}