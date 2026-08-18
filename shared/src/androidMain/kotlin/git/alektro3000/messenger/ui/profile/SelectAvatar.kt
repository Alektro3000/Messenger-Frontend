package git.alektro3000.messenger.ui.profile

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import git.alektro3000.messenger.actions.PickedAvatar

class SelectAvatar(val context: Context) {
    private fun readBytes(uri: Uri): ByteArray {
        return context.contentResolver.openInputStream(uri)!!.use {
            it.readBytes()
        }
    }
    private fun getFileName(uri: Uri): String? {
        val cursor = context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    return it.getString(index)
                }
            }
        }

        return null
    }
    fun getData(uri: Uri): PickedAvatar
    {
        val mimeType = context.contentResolver.getType(uri)
            ?: throw IllegalArgumentException("Cannot determine file type")

        val fileName = getFileName(uri)  ?: when (mimeType) {
            "image/jpeg" -> "avatar.jpg"
            "image/png" -> "avatar.png"
            "image/webp" -> "avatar.webp"
            else -> "avatar"
        }
        val bytes = readBytes(uri)

        return PickedAvatar(bytes, mimeType, fileName)
    }
}