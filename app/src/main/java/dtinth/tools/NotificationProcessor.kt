package dtinth.tools

import android.content.Context

interface NotificationProcessor {
    fun getName(): String
    fun process(notificationData: NotificationData, context: Context): String
}