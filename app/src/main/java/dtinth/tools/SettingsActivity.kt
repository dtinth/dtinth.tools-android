package dtinth.tools

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.htmlEncode
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.work.WorkInfo
import androidx.work.WorkManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            this.findPreference<Preference>("noti_recent")
                ?.setOnPreferenceClickListener(fun(it: Preference): Boolean {
                    val html = StringBuilder()
                    html.append("Message history<ul>")
                    for (item in (it.sharedPreferences.getString("noti_recent", "-") ?: "").split("\n")) {
                        html.append("<li><code>").append(item.htmlEncode()).append("</code></li>")
                    }
                    html.append("</ul>")
                    WebViewActivity.show(this.requireContext(), html.toString())
                    return true
                })


            this.findPreference<Preference>("exfiltrate_inspect")
                ?.setOnPreferenceClickListener(fun(it: Preference): Boolean {
                    val workManager = WorkManager.getInstance(this.requireContext())
                    val workInfos = workManager.getWorkInfosByTag("exfiltrate").get()
                    var enqueued = 0
                    var failed = 0
                    var running = 0
                    var succeeded = 0
                    var others = 0
                    for (item in workInfos) {
                        when (item.state) {
                            WorkInfo.State.ENQUEUED -> enqueued += 1
                            WorkInfo.State.FAILED -> failed += 1
                            WorkInfo.State.RUNNING -> running += 1
                            WorkInfo.State.SUCCEEDED -> succeeded += 1
                            else -> others += 1
                        }
                    }
                    val html = StringBuilder()
                    html.append("enqueued = $enqueued<br>")
                    html.append("failed = $failed<br>")
                    html.append("running = $running<br>")
                    html.append("succeeded = $succeeded<br>")
                    html.append("others = $others<br>")
                    WebViewActivity.show(this.requireContext(), html.toString())
                    return true
                })

            this.findPreference<Preference>("exfiltrate_cancel")
                ?.setOnPreferenceClickListener(fun(it: Preference): Boolean {
                    val workManager = WorkManager.getInstance(this.requireContext())
                    val workInfos = workManager.getWorkInfosByTag("exfiltrate").get()
                    val html = StringBuilder()
                    html.append("Work infos canceled<ul>")
                    for (item in workInfos) {
                        html.append("<li><code>").append(item.id.toString().htmlEncode()).append("</code></li>")
                    }
                    html.append("</ul>")
                    workManager.cancelAllWorkByTag("exfiltrate").result.get()
                    workManager.pruneWork().result.get()
                    WebViewActivity.show(this.requireContext(), html.toString())
                    return true
                })
        }
    }
}