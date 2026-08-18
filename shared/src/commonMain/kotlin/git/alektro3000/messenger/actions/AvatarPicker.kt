package git.alektro3000.messenger.actions

import kotlinx.serialization.Serializable

interface AvatarPicker {
    suspend fun pickAvatar(): PickedAvatar?
}

data class PickedAvatar(
    val bytes: ByteArray,
    val mimeType: String,
    val fileName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PickedAvatar

        if (!bytes.contentEquals(other.bytes)) return false
        if (fileName != other.fileName) return false
        if (mimeType != other.mimeType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + (fileName.hashCode())
        result = 31 * result + (mimeType.hashCode())
        return result
    }
}
