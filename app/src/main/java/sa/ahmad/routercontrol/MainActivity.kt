package sa.ahmad.routercontrol

import android.app.Activity
import android.os.Bundle
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private val routerUrl = "http://192.168.1.1/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun showHome() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            textDirection = android.view.View.TEXT_DIRECTION_RTL
            layoutDirection = android.view.View.LAYOUT_DIRECTION_RTL
            setPadding(32, 32, 32, 32)
        }
        val title = TextView(this).apply {
            text = "تحكّم بالمودم"
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        val subtitle = TextView(this).apply {
            text = "D-Link DVG-M3521U\nاتصل بشبكة المودم ثم افتح لوحة الإدارة."
            textSize = 17f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 24)
        }
        val open = Button(this).apply {
            text = "فتح لوحة إدارة المودم"
            textSize = 18f
            setOnClickListener { openRouter() }
        }
        val note = TextView(this).apply {
            text = "النسخة الأولية: لا تغيّر إعدادات المودم تلقائيًا. سجّل الدخول في صفحة الإدارة بنفسك."
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 0)
        }
        layout.addView(title, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        layout.addView(subtitle, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        layout.addView(open, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        layout.addView(note, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        setContentView(layout)
    }

    private fun openRouter() {
        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl(routerUrl)
        setContentView(webView)
    }

    @Deprecated("Deprecated in Android API")
    override fun onBackPressed() {
        val view = window.decorView.findViewById<WebView>(android.R.id.content)
        if (view is WebView && view.canGoBack()) view.goBack() else showHome()
    }
}