package dtinth.tools

import android.app.Notification
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.preference.PreferenceManager

class NotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(statusBarNotification: StatusBarNotification) {
        val notification = statusBarNotification.notification
        val title = notification.extras.get(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = notification.extras.get(Notification.EXTRA_TEXT)?.toString() ?: ""
        val packageName = statusBarNotification.packageName

        val notificationData = NotificationData(packageName, title, text)

        val blocklist = NotificationBlocklist("pipeline_blocklist")
        val pattern = blocklist.getBlockingPattern(notificationData, this)
        if (pattern != null) {
            return
        }

        val notificationProcessors = listOf<NotificationProcessor>(
            MorseCodeNotifier(),
            NotificationExfiltrator()
        )

        val result = StringBuilder("[$packageName] $title")
        for (p in notificationProcessors) {
            result.append(" [${p.getName()}: ")
            try {
                result.append(p.process(notificationData, this))
            } catch (e: Exception) {
                result.append("Error: ").append(e.message)
                Log.d("NotificationListener", e.message, e)
            }
            result.append("]")
        }
        log("$result")
    }

    private fun log(text: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val recent = (sharedPreferences.getString("noti_recent", "") ?: "").split("\n")
        val out = recent.takeLast(8).joinToString("\n") + "\n$text"
        Log.d("dtinth/NotificationListener", text)
        sharedPreferences.edit().putString("noti_recent", out).commit()
    }
}

class NotificationBlocklist(val name: String) {
    fun getBlockingPattern(notificationData: NotificationData, context: Context): String? {
        val textBasis = "[${notificationData.packageName}] ${notificationData.title}"
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val blocklist = sharedPreferences.getString(name, "")?.split("\n")
        if (blocklist != null) {
            for (c in blocklist) {
                val item = c.trim()
                if (item != "" && textBasis.contains(item)) {
                    return item
                }
            }
        }
        return null
    }
}

