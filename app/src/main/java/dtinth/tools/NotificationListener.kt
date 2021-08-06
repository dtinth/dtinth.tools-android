package dtinth.tools

import android.app.Notification
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
        val textBasis = "[$packageName] $title"

        val vibrateOutcome = vibrate(textBasis, title)
        log("$textBasis $vibrateOutcome")
    }

    private fun vibrate(textBasis: String, title: String): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val blocklist = sharedPreferences.getString("morse_blocklist", "")?.split("\n")
        if (blocklist != null) {
            for (c in blocklist) {
                val item = c.trim()
                if (item != "" && textBasis.contains(item)) {
                    return "[blocked by pattern `$item`]"
                }
            }
        }
        val morseCodeVibrator = MorseCodeVibrator(this, title)
        return morseCodeVibrator.vibrate()
    }

    private fun log(text: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val recent = (sharedPreferences.getString("noti_recent", "") ?: "").split("\n")
        val out = recent.takeLast(8).joinToString("\n") + "\n$text"
        Log.d("dtinth/NotificationListener", text)
        sharedPreferences.edit().putString("noti_recent", out).commit()
    }
}