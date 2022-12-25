package com.sd.lib.compose.webview

import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sd.lib.compose.webview.core.FWebViewManager

@Composable
fun rememberFWebViewState(): FWebViewState {
    return remember { FWebViewState() }
}

open class FWebViewState : WebViewState() {
    var webView: WebView? = null
        internal set

    internal fun update(view: WebView) {
        webView = view
        onUpdate(view)
        loadContent(view)
    }

    private fun loadContent(view: WebView) {
        val content = content ?: return
        when (content) {
            is WebContent.Url -> {
                val url = content.url
                if (url.isNotEmpty() && url != view.url) {
                    view.loadUrl(url, content.additionalHttpHeaders.toMutableMap())
                }
            }
            is WebContent.Data -> {
                view.loadDataWithBaseURL(content.baseUrl, content.data, null, "utf-8", null)
            }
        }
    }

    fun loadUrl(
        url: String,
        additionalHttpHeaders: Map<String, String>? = null,
    ) {
        FWebViewManager.syncHttpClientCookieToWebView(url)
        content = WebContent.Url(url, additionalHttpHeaders ?: emptyMap())
    }

    fun loadDataWithBaseURL(
        data: String,
        baseUrl: String? = null,
    ) {
        content = WebContent.Data(data, baseUrl)
    }

    fun evaluateJavascriptFunction(function: String, callback: ValueCallback<String?>? = null) {
        if (function.isEmpty()) return
        val script = "$function()"
        evaluateJavascript(script, callback)
    }

    fun evaluateJavascriptFunction(function: String, params: Array<Any>, callback: ValueCallback<String?>? = null) {
        if (function.isEmpty()) return
        val paramsString = params.joinToString(
            separator = ",",
            transform = {
                if (it is String) {
                    "'${it}'"
                } else {
                    it.toString()
                }
            }
        )
        val script = "$function($paramsString)"
        evaluateJavascript(script, callback)
    }

    fun evaluateJavascript(script: String, callback: ValueCallback<String?>? = null) {
        webView?.evaluateJavascript(script, callback)
    }

    protected open fun onUpdate(view: WebView) {}
}