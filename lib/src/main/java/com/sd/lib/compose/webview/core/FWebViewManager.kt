package com.sd.lib.compose.webview.core

import android.util.Log
import android.webkit.WebView

object FWebViewManager {
    private var _webViewHandler: FWebViewHandler? = null

    @JvmStatic
    var isDebug = false

    @JvmStatic
    fun setWebViewHandler(webViewHandler: FWebViewHandler?) {
        _webViewHandler = webViewHandler
    }

    @JvmStatic
    fun initWebView(webView: WebView) {
        _webViewHandler?.initWebView(webView)
    }

    /**
     * 把Http客户端对应[url]的Cookie同步到WebView
     */
    @JvmStatic
    fun syncHttpClientCookieToWebView(url: String?) {
        if (url.isNullOrEmpty()) return
        _webViewHandler?.syncHttpClientCookieToWebView(url)
    }

    /**
     * 把WebView对应[url]的Cookie同步到Http客户端
     */
    @JvmStatic
    fun syncWebViewCookieToHttpClient(url: String?) {
        if (url.isNullOrEmpty()) return
        _webViewHandler?.let { handler ->
            val listCookie = FWebViewCookie.getCookieAsList(url)
            if (listCookie.isNotEmpty()) {
                handler.syncWebViewCookieToHttpClient(url, listCookie)
            }
        }
    }
}

internal inline fun logMsg(block: () -> String) {
    if (FWebViewManager.isDebug) {
        Log.i("FWebView", block())
    }
}