package dtinth.tools

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView

class WebViewActivity : AppCompatActivity() {
    companion object {
        var contentToShow: String? = null
        fun show(context: Context, content: String) {
            contentToShow = content
            context.startActivity(Intent(context, WebViewActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myWebView = WebView(this)
        val html = StringBuilder()
            .append("<html><head><style>body { background: #353433; color: #e9e8e7; font: 14px sans-serif; }</style></head>")
            .append("<body>")
            .append(contentToShow ?: "-")
            .append("</body>")
        setContentView(myWebView)
        Log.d("WebViewActivity", html.toString())
        myWebView.loadDataWithBaseURL("", html.toString(), "text/html", "utf-8", "")
    }
}