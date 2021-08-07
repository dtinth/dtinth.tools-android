package dtinth.tools

import kotlinx.serialization.Serializable
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class NotificationData(
    val packageName: String,
    val title: String,
    val text: String,
    val time: String = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
)