package dtinth.tools

import android.content.Context

class MorseCodeNotifier : NotificationProcessor {
    override fun getName(): String {
        return "morse"
    }

    override fun process(notificationData: NotificationData, context: Context): String {
        val blocklist = NotificationBlocklist("morse_blocklist")
        val pattern = blocklist.getBlockingPattern(notificationData, context)
        if (pattern != null) {
            return "[blocked by pattern `$pattern`]"
        }
        val morseCodeVibrator = MorseCodeVibrator(context, notificationData.title)
        return morseCodeVibrator.vibrate()
    }
}