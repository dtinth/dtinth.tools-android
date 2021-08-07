package dtinth.tools

import android.content.Context
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NotificationExfiltrator : NotificationProcessor {
    override fun getName(): String {
        return "exfiltrate"
    }

    override fun process(notificationData: NotificationData, context: Context): String {
        val data = notificationData
            .let { Json.encodeToString(it) }
            .let { Sealer().seal(it) }
        Log.d("exfiltrate", data)
        return "OK"
    }
}