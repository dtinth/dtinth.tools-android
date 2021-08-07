package dtinth.tools

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

class NotificationExfiltrator : NotificationProcessor {
    override fun getName(): String {
        return "exfiltrate"
    }

    override fun process(notificationData: NotificationData, context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val publicKey = sharedPreferences.getString("exfiltrate_public_key", null)
            ?: return "[public key not set up]"

        if (publicKey.length != 44) {
            return "[wrong public key length]"
        }

        val data = notificationData
            .let { Json.encodeToString(it) }
            .let { Sealer().seal(it, publicKey) }
        Log.d("NotificationExfiltrator", data)

        val request = OneTimeWorkRequestBuilder<ExfiltrateWork>()
            .setInputData(workDataOf("data" to data))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag("exfiltrate")
            .build()
        WorkManager.getInstance(context).enqueue(request)
        return request.id.toString()
    }
}

class ExfiltrateWork(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val data =
            inputData.getString("data") ?: return Result.failure()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val endpoint =
            sharedPreferences.getString("exfiltrate_endpoint", null) ?: return Result.failure()
        try {
            val url = URL(endpoint)
            Log.d("ExfiltrateWork", "Send to $endpoint")
            val connection = url.openConnection() as HttpURLConnection

            try {
                val bytes = data.encodeToByteArray()
                Log.d("ExfiltrateWork", "Conneection opened, sending ${bytes.size}")
                connection.doOutput = true
                connection.setFixedLengthStreamingMode(bytes.size)
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "text/plain")
                connection.outputStream.write(bytes)
                val responseCode = connection.responseCode
                Log.d("ExfiltrateWork", "Response code $responseCode")
                if (responseCode >= 500) {
                    return Result.retry()
                }
            } finally {
                connection.disconnect()
            }
        } catch (error: Exception) {
            Log.d("ExfiltrateWork", "Cannot exfiltrate", error)
            return Result.failure()
        }

        return Result.success()
    }
}
