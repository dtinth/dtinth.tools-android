package dtinth.tools

import android.content.Context
import android.os.Vibrator
import android.service.notification.NotificationListenerService
import androidx.preference.PreferenceManager
import java.lang.StringBuilder

class MorseCodeVibrator(private val context: Context, private val text: String) {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val period = sharedPreferences.getString("morse_period", "")?.toIntOrNull() ?: 80
    private val builder = MorseCodeBuilder(period, 3)
    private val vibrator = context.getSystemService(NotificationListenerService.VIBRATOR_SERVICE) as Vibrator?

    fun vibrate(): String {
        if (vibrator == null) {
            return "[no vibrator]"
        }
        val initials = getInitials(text)
        builder.add(initials)
        val result: MutableList<Long> = builder.build()
        return if (result.size > 1) {
            result.set(0, result[0] + 1000)
            result.addAll(0, listOf(0, 16, 32, 16, 32, 16, 32, 16))
            vibrator.vibrate(result.toLongArray(), -1)
            "=> $initials"
        } else {
            "[no result]"
        }
    }

    private fun getInitials(text: String): String {
        var state = false
        val out = StringBuilder()
        for (i in text.indices) {
            val status = Character.isAlphabetic(text[i].toInt())
            if (state != status) {
                if (status) out.append(text[i])
                state = status
            }
        }
        return out.toString()
    }
}