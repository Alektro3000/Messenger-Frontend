package git.alektro3000.messenger.local.dao

import androidx.room.TypeConverter
import kotlin.time.Instant


class Converters {
    @TypeConverter
    fun messageStatusToString(value: MessageStatus): String = value.name

    @TypeConverter
    fun stringToMessageStatus(value: String): MessageStatus =
        MessageStatus.valueOf(value)



    @TypeConverter
    fun chatTypeToString(value: ChatType): String = value.name

    @TypeConverter
    fun stringToChatType(value: String): ChatType =
        ChatType.valueOf(value)


    @TypeConverter
    fun instantToLong(value: Instant?): Long? =
        value?.toEpochMilliseconds()

    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let { Instant.fromEpochMilliseconds(it) }
}