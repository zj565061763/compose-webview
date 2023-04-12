package com.sd.lib.compose.webview.core

import android.content.Intent
import android.net.Uri
import android.webkit.WebSettings
import android.webkit.WebView
import java.net.HttpCookie

abstract class FWebViewHandler {
    /**
     * 初始化[webView]
     */
    open fun initWebView(webView: WebView) {
        webView.initDefault()
    }

    /**
     * 把Http客户端对应[url]的Cookie同步到WebView
     */
    abstract fun syncHttpClientCookieToWebView(url: String)

    /**
     * 把WebView对应[url]的[cookies]同步到Http客户端
     */
    abstract fun syncWebViewCookieToHttpClient(url: String, cookies: List<HttpCookie>)
}

/**
 * 初始化默认配置
 */
private fun WebView.initDefault() {
    setDownloadListener { url, _, _, _, _ ->
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    settings.run {
        // 设置是否把网页按比例缩放到刚好全部展示
        useWideViewPort = true
        loadWithOverviewMode = true

        setSupportZoom(true)
        builtInZoomControls = true
        displayZoomControls = false

        javaScriptEnabled = true
        domStorageEnabled = true

        databaseEnabled = true
        cacheMode = WebSettings.LOAD_NO_CACHE
    }
}