package dtinth.tools

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.htmlEncode
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

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
        }
    }
}