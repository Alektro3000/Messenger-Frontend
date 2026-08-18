package git.alektro3000.messenger.local


import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import git.alektro3000.messenger.local.dao.ChatDao
import git.alektro3000.messenger.local.dao.ChatEntity
import git.alektro3000.messenger.local.dao.ChatMemberDao
import git.alektro3000.messenger.local.dao.ChatMemberEntity
import git.alektro3000.messenger.local.dao.Converters
import git.alektro3000.messenger.local.dao.MessageDao
import git.alektro3000.messenger.local.dao.MessageEntity
import git.alektro3000.messenger.local.dao.UserDao
import git.alektro3000.messenger.local.dao.UserEntity

@Database(entities = [
    MessageEntity::class,
    UserEntity::class,
    ChatEntity::class,
    ChatMemberEntity::class],
    version = 11,
    exportSchema = false)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun chatMemberDao(): ChatMemberDao

    suspend fun clearData()
    {
        messageDao().clear()
        chatMemberDao().clear()
        chatDao().clear()
        userDao().clear()
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
