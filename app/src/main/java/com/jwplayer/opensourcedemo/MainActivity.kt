package com.jwplayer.opensourcedemo

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build.VERSION
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.webkit.*
import android.webkit.WebSettings.LayoutAlgorithm
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.InputStream
import java.net.URISyntaxException
import java.util.*
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    var pendingPassword: String? = null
    var pendingUsername: String? = null

    val NETFLIX_WEB_URL_PATTERN =
        Pattern.compile("netflix.com\\/watch\\/(.*)\\?preventIntent=true")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPlayer.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }

        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.setSupportZoom(true)
        settings.builtInZoomControls = false
        settings.layoutAlgorithm = LayoutAlgorithm.SINGLE_COLUMN
        settings.domStorageEnabled = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.96 Mobile Safari/537.36")
        webView.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        webView.isScrollbarFadingEnabled = true
        if (VERSION.SDK_INT >= 19) {
            webView.setLayerType(2, null)
        } else {
            webView.setLayerType(1, null)
        }

        val hashMap: HashMap<String, String> = HashMap<String, String>()
        hashMap["User-Agent"] = "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Mobile Safari/537.36"

        CookieManager.getInstance().setCookie(".netflix.com", "forceWebsite=true; path=/")

        webView.addJavascriptInterface(JavaScriptInterface(), "JSInterface")

        loadNetflix("https://netflix.com/login");
    }

    private fun loadNetflix(url: String) {
        val hashMap: HashMap<String, String> = HashMap<String, String>()
        hashMap["User-Agent"] =
            "Mozilla/5.0 (Linux; Android 8.0; " +
                    "Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Mobile Safari/537.36"

        webView.loadUrl(url, hashMap)

        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                if (message != "netflix_phone_number_error") {
                    return super.onJsAlert(webView, url, message, result)
                }
                result?.confirm()
                return true
            }
        }


        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(webView: WebView?, str: String?) {
                super.onPageFinished(webView, str)
                injectJavascriptOnly()
            }

            override fun shouldOverrideUrlLoading(webView: WebView, str: String): Boolean {
                if (str.startsWith("intent://")) {
                    try {
                        val parseUri = Intent.parseUri(str, Intent.URI_INTENT_SCHEME)
                        if (parseUri != null) {
                            webView.stopLoading()
                            webView.loadUrl(parseUri.getStringExtra("browser_fallback_url"))
                            return true
                        }
                    } catch (exception: URISyntaxException) {
                        exception.printStackTrace()
                    }
                }
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                url?.run {
                    if ((this.contains("/browse"))) {
                        MslNativeSession.init();
                    }

                    if (this.contains("/watch")) {
                        startVideo(this)
                    }
                }
            }
        }

        injectJavascriptOnly()
    }

    private fun startVideo(url: String) {
        Log.d(TAG, "startVideo() = $url")
        if (url.isNotEmpty()) {
            val matcher =  NETFLIX_WEB_URL_PATTERN.matcher(url)
            if (matcher.find()) {
                val videoID = matcher.group(1)
                loadNetflixManifest(videoID)
            }
        }
    }

    private fun loadNetflixManifest(videoID: String?) {
        Log.d(TAG, "loadNetflixManifest() videoID = $videoID")

        MslNativeSession.getInstance().getManifest(videoID, object : MslNativeSession.MslCallback<NetflixManifest?> {
            override fun onSuccess(netflixManifest: NetflixManifest?) {
                netflixManifest?.run {
                    if (this.error != null) {
                        Log.e(TAG, "onSuccess: manifest error")
                    } else {

                        var hasWrittenManifest = NetflixManifestGenerator.writeJSONManifest(this)

                        val dashManifestPath = NetflixManifestGenerator.getDashManifestPath()



                    }
                }
            }

            override fun onFailure() {

            }

            override fun onFailure(mslErrorResponse: MslErrorResponse?) {

            }

            override fun onFailure(netflixError: NetflixError?) {

            }
        })
    }

    override fun onBackPressed() {
       if (!canGoBack()) super.onBackPressed()
    }

    fun canGoBack(): Boolean {
        if (!webView.canGoBack()) {
            return false
        }
        webView.goBack()
        return true
    }

    private fun injectJavascriptOnly() {
        runOnUiThread {
            injectJs()
        }
    }

    private fun injectJs() {
        try {
            val open: InputStream = assets.open("netflix.js")
            val bArr = ByteArray(open.available())
            open.read(bArr)
            open.close()
            val sb = StringBuilder()
            sb.append("javascript:(function() {")
            sb.append("var parent = document.getElementsByTagName('head').item(0);")
            sb.append("var script = document.createElement('script');")
            sb.append("script.type = 'text/javascript';")
            sb.append("script.innerHTML = window.atob('")
            sb.append(Base64.encodeToString(bArr, 2))
            sb.append("');")
            sb.append("document.head.appendChild(script);")
            sb.append("})()")
            webView.evaluateJavascript(sb.toString()) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        Log.d(TAG, "injectJs callback: $it")
                    }
                }, 200)
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    class JavaScriptInterface {
        @JavascriptInterface
        fun saveLogin(username: String?, password: String?) {
            Log.d(TAG, "Credentials - $username : $password")
        }
    }
}