package sa.ahmad.routercontrol

import android.app.Activity
import android.os.Bundle
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.app.AlertDialog

class MainActivity : Activity() {
    private val routerUrl = "http://192.168.1.1/"
    private val navy = Color.rgb(18, 39, 70)
    private val blue = Color.rgb(32, 107, 196)
    private val pale = Color.rgb(241, 246, 252)
    private var webView: WebView? = null
    private var pageTitle: TextView? = null
    private var content: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun text(value: String, size: Float, color: Int = navy, bold: Boolean = false): TextView =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            gravity = Gravity.RIGHT
            textDirection = View.TEXT_DIRECTION_RTL
            if (bold) typeface = Typeface.DEFAULT_BOLD
            setPadding(dp(4), dp(5), dp(4), dp(5))
        }

    private fun button(label: String, action: () -> Unit): Button =
        Button(this).apply {
            text = label
            textSize = 15f
            isAllCaps = false
            setOnClickListener { action() }
        }

    private fun baseLayout(title: String): LinearLayout {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setBackgroundColor(pale)
        }
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(8))
            setBackgroundColor(navy)
        }
        bar.addView(TextView(this).apply {
            text = title
            textSize = 19f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        }, LinearLayout.LayoutParams(0, dp(48), 1f))
        bar.addView(button("الرئيسية") { showHome() }.apply {
            textSize = 12f
        }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(44)))
        root.addView(bar)
        return root
    }

    private fun showHome() {
        webView = null
        val root = baseLayout("تحكّم بالمودم")
        val scroll = ScrollView(this)
        val body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setPadding(dp(18), dp(18), dp(18), dp(24))
        }
        body.addView(text("D-Link DVG-M3521U", 24f, navy, true))
        body.addView(text("لوحة إدارة محلية باللغة العربية", 15f, Color.DKGRAY))
        val status = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            setBackgroundColor(Color.WHITE)
        }
        status.addView(text("عنوان المودم", 14f, Color.GRAY))
        status.addView(text("192.168.1.1", 20f, blue, true))
        status.addView(text("الإصدار المعروف: STC_1.00B01 • العتاد A1", 13f, Color.DKGRAY))
        status.addView(text("حالة الاتصال والإعدادات ستُقرأ من المودم بعد فحص واجهته؛ لا نعرض بيانات وهمية.", 13f, Color.DKGRAY))
        body.addView(status, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            bottomMargin = dp(16)
        })
        body.addView(text("أقسام التحكم", 19f, navy, true))
        body.addView(text("افتح لوحة المودم وسجّل الدخول. هذه الأزرار تفتح واجهة المودم الحقيقية؛ نربط عناصر التحكم المباشرة بعد تحديد صفحاتها وطلبات الحفظ الفعلية.", 13f, Color.DKGRAY))
        addSectionCard(body, "📶  إعدادات Wi‑Fi", "اسم الشبكة وكلمة المرور ونطاق الشبكة") { openRouter("إعدادات Wi‑Fi") }
        addSectionCard(body, "👥  الأجهزة المتصلة", "عرض الأجهزة المتصلة من لوحة المودم") { openRouter("الأجهزة المتصلة") }
        addSectionCard(body, "🌐  الإنترنت", "حالة الاتصال وإعدادات WAN") { openRouter("اتصال الإنترنت") }
        addSectionCard(body, "⚙️  إعدادات المودم", "الشبكة المحلية وإدارة الجهاز") { openRouter("إعدادات المودم") }
        body.addView(button("فتح لوحة الإدارة كاملة") { openRouter("لوحة إدارة المودم") })
        body.addView(text("ملاحظة أمان: لن نغيّر إعدادات الشبكة أو كلمة المرور دون إجراء واضح منك.", 13f, Color.rgb(130, 65, 15)))
        scroll.addView(body)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun addSectionCard(parent: LinearLayout, heading: String, detail: String, action: () -> Unit) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(10), dp(14), dp(10))
            setBackgroundColor(Color.WHITE)
        }
        card.addView(text(heading, 17f, navy, true))
        card.addView(text(detail, 13f, Color.DKGRAY))
        card.addView(button("فتح القسم") { action() })
        parent.addView(card, LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(10)
            bottomMargin = dp(2)
        })
    }

    private fun openRouter(section: String) {
        val root = baseLayout(section)
        val tools = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(6), dp(2), dp(6), dp(2))
            setBackgroundColor(Color.WHITE)
        }
        tools.addView(button("رجوع") { if (webView?.canGoBack() == true) webView?.goBack() else showHome() },
            LinearLayout.LayoutParams(0, dp(46), 1f))
        tools.addView(button("تحديث") { webView?.reload() },
            LinearLayout.LayoutParams(0, dp(46), 1f))
        tools.addView(button("فحص Wi-Fi") { inspectPage("wifi") },
            LinearLayout.LayoutParams(0, dp(46), 1f))
        tools.addView(button("فحص الأجهزة") { inspectPage("devices") },
            LinearLayout.LayoutParams(0, dp(46), 1f))
        root.addView(tools)
        val wv = WebView(this)
        webView = wv
        wv.settings.javaScriptEnabled = true
        wv.settings.domStorageEnabled = true
        wv.settings.loadsImagesAutomatically = true
        wv.webChromeClient = WebChromeClient()
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean = false
        }
        root.addView(wv, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
        wv.loadUrl(routerUrl)
    }

    private fun inspectPage(mode: String = "all") {
        val wv = webView ?: return
        val script = """
            (function(){
              function clean(s){return (s||'').replace(/\s+/g,' ').trim();}
              var mode = '${mode}';
              var links=[].slice.call(document.querySelectorAll('a')).map(function(a){
                return {label:clean(a.innerText||a.getAttribute('aria-label')||a.title),url:a.href};
              }).filter(function(a){return a.label.length>0;});
              var relevant=links.filter(function(a){
                return mode==='wifi' ? /wifi|wireless|wlan|ssid|شبك|لاسلك|وا.?فا.?ي/i.test(a.label+' '+a.url) :
                       mode==='devices' ? /device|client|host|station|mac|connected|wireless|شبك|أجهز|متصل|عميل|حظر|block|filter/i.test(a.label+' '+a.url) : true;
              }).slice(0,80).map(function(a){return a.label+' -> '+a.url;});
              var forms=[].slice.call(document.forms).map(function(f,i){
                var fields=[].slice.call(f.querySelectorAll('input,select,textarea,button')).map(function(e){
                  var labels=[].slice.call(e.labels||[]).map(function(l){return clean(l.innerText)}).join('/');
                  var nearby=clean(e.parentElement?e.parentElement.innerText:'').slice(0,100);
                  return e.tagName+' name='+e.name+' id='+e.id+' type='+(e.type||'')+' label='+labels+' value='+(e.type==='password'?'[hidden]':(e.value||''))+' text='+clean(e.innerText)+' context='+nearby;
                });
                return 'FORM '+i+' action='+f.action+' method='+f.method+'\n'+fields.join('\n');
              });
              var inputs=[].slice.call(document.querySelectorAll('input,select,textarea,button')).map(function(e){
                var s=(e.name+' '+e.id+' '+e.type+' '+e.placeholder+' '+e.getAttribute('aria-label')+' '+(e.parentElement?e.parentElement.innerText:''));
                if(mode==='wifi' && /ssid|wireless|wifi|wlan|password|passphrase|wpa|شبك|كلمة المرور|مفتاح/i.test(s)) return e.tagName+' name='+e.name+' id='+e.id+' type='+(e.type||'')+' placeholder='+e.placeholder;
                if(mode==='devices' && /device|client|host|mac|block|filter|disconnect|حظر|جهاز|متصل|عنوان/i.test(s)) return e.tagName+' name='+e.name+' id='+e.id+' type='+(e.type||'')+' placeholder='+e.placeholder;
                return null;
              }).filter(Boolean).slice(0,80);
              return 'MODE: '+mode+'\nTITLE: '+document.title+'\nURL: '+location.href+'\nMATCHING LINKS:\n'+relevant.join('\n')+'\nMATCHING FIELDS:\n'+inputs.join('\n')+'\nFORMS:\n'+forms.join('\n')+'\nVISIBLE TEXT:\n'+clean(document.body?document.body.innerText:'').slice(0,2500);
            })()
        """.trimIndent()
        wv.evaluateJavascript("JSON.stringify($script)") { result ->
            val report = try {
                result?.let { org.json.JSONTokener(it).nextValue().toString() } ?: "لم تُقرأ الصفحة"
            } catch (_: Exception) { result ?: "لم تُقرأ الصفحة" }
            showReport(report)
        }
    }

    private fun showReport(report: String) {
        AlertDialog.Builder(this)
            .setTitle("تقرير صفحة المودم")
            .setMessage(report)
            .setPositiveButton("نسخ التقرير") { _, _ ->
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("تقرير صفحة المودم", report))
            }
            .setNegativeButton("إغلاق", null)
            .show()
    }

    @Deprecated("Deprecated in Android API")
    override fun onBackPressed() {
        if (webView?.canGoBack() == true) webView?.goBack() else showHome()
    }
}
