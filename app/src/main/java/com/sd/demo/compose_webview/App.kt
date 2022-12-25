package com.sd.demo.compose_webview

import android.app.Application
import android.webkit.WebView
import com.sd.lib.compose.webview.core.FWebViewHandler
import com.sd.lib.compose.webview.core.FWebViewManager
import java.net.HttpCookie

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FWebViewManager.isDebug = true
        FWebViewManager.setWebViewHandler(_webViewHandler)
    }

    private val _webViewHandler = object : FWebViewHandler() {
        override fun initWebView(webView: WebView) {
            super.initWebView(webView)
            logMsg { "FWebViewHandler initWebView webView:$webView" }
        }

        override fun syncHttpClientCookieToWebView(url: String) {
            logMsg { "FWebViewHandler syncHttpClientCookieToWebView url:$url" }
        }

        override fun syncWebViewCookieToHttpClient(url: String, cookies: List<HttpCookie>) {
            logMsg { "FWebViewHandler syncWebViewCookieToHttpClient url:$url cookies:$cookies" }
        }
    }
}