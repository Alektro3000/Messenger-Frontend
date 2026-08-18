package git.alektro3000.messenger.extensions

import androidx.compose.runtime.Composable
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import messenger.shared.generated.resources.Res
import messenger.shared.generated.resources.chat_day_of_week
import messenger.shared.generated.resources.chat_yesterday
import messenger.shared.generated.resources.contact_last_seen
import messenger.shared.generated.resources.contact_last_seen_alt
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.Instant


sealed interface ChatTimeFormat {
    data class Time(val text: String) : ChatTimeFormat
    data object Yesterday : ChatTimeFormat
    data class Weekday(val day: Int) : ChatTimeFormat
    data class Date(val text: String) : ChatTimeFormat
    data class FullDate(val text: String) : ChatTimeFormat
}

fun Instant.toChatTimeFormat(): ChatTimeFormat {
    val current = Clock.System.now()
    val local = toLocalDateTime(TimeZone.currentSystemDefault())
    val currentLocal = current.toLocalDateTime(TimeZone.currentSystemDefault())

    return when {
        local.date == currentLocal.date ->
            ChatTimeFormat.Time("%02d:%02d".format(local.hour, local.minute))

        local.date == currentLocal.date.minus(1, DateTimeUnit.DAY) ->
            ChatTimeFormat.Yesterday

        local.date.startOfWeek() == currentLocal.date.startOfWeek() ->
            ChatTimeFormat.Weekday(local.dayOfWeek.ordinal)

        local.year == currentLocal.year ->
            ChatTimeFormat.Date("%02d.%02d".format(local.day, local.month.number))

        else ->
            ChatTimeFormat.FullDate(
                "%02d.%02d.%d".format(
                    local.day,
                    local.month.number,
                    local.year
                )
            )
    }
}

@Composable
fun Instant.toChatTimeText(): String {
    return when (val format = toChatTimeFormat()) {
        is ChatTimeFormat.Time -> format.text
        is ChatTimeFormat.Yesterday -> stringResource(Res.string.chat_yesterday)
        is ChatTimeFormat.Weekday -> stringArrayResource(Res.array.chat_day_of_week)[format.day]
        is ChatTimeFormat.Date -> format.text
        is ChatTimeFormat.FullDate -> format.text
    }
}

@Composable
fun Instant.toLastSeenText(): String {
    return when (val format = toChatTimeFormat()) {
        is ChatTimeFormat.Time -> stringResource(Res.string.contact_last_seen) + format.text
        is ChatTimeFormat.Yesterday -> stringResource(Res.string.contact_last_seen_alt) + stringResource(Res.string.chat_yesterday)
        is ChatTimeFormat.Weekday -> stringResource(Res.string.contact_last_seen) + stringArrayResource(Res.array.chat_day_of_week)[format.day]
        is ChatTimeFormat.Date -> stringResource(Res.string.contact_last_seen_alt) + format.text
        is ChatTimeFormat.FullDate -> stringResource(Res.string.contact_last_seen_alt) + format.text
    }
}

fun Instant.toMessageTime(): String {
    val local =
        toLocalDateTime(TimeZone.currentSystemDefault())

    return "%02d:%02d".format(local.hour, local.minute)
}

@Composable
fun Instant.toCreateTime(): String {
    return toChatTimeText()
}

fun LocalDate.startOfWeek(): LocalDate {
    return this.minus(
        this.dayOfWeek.ordinal,
        DateTimeUnit.DAY
    )
}

inline fun <reified T : Enum<T>> enumValueOf(
    value: String
): T {
    return enumValues<T>()
        .first { it.name == value }
}