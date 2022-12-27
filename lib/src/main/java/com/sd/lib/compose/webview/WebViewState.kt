package com.sd.lib.compose.webview

import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sd.lib.compose.webview.core.FWebViewManager
import com.sd.lib.compose.webview.core.logMsg

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
        content?.let { content ->
            when (content) {
                is WebContent.Url -> {
                    val url = content.url
                    if (url.isNotEmpty() && url != view.url) {
                        logMsg { "loadUrl url:$url" }
                        view.loadUrl(url, content.additionalHttpHeaders.toMutableMap())
                    }
                }
                is WebContent.Data -> {
                    view.loadDataWithBaseURL(content.baseUrl, content.data, null, "utf-8", null)
                }
            }
        }
    }

    fun loadUrl(
        url: String,
        additionalHttpHeaders: Map<String, String>? = null,
    ) {
        val content = WebContent.Url(url, additionalHttpHeaders ?: emptyMap())
        loadContent(content)
    }

    fun loadDataWithBaseURL(
        data: String,
        baseUrl: String? = null,
    ) {
        val content = WebContent.Data(data, baseUrl)
        loadContent(content)
    }

    fun loadContent(content: WebContent) {
        if (content is WebContent.Url) {
            FWebViewManager.syncHttpClientCookieToWebView(content.url)
        }
        this.content = content
    }

    protected open fun onUpdate(view: WebView) {}
}

fun WebView.fEvaluateJavascriptFunction(
    function: String,
    callback: ValueCallback<String?>? = null,
) {
    if (function.isEmpty()) return
    val script = "$function()"
    evaluateJavascript(script, callback)
}

fun WebView.fEvaluateJavascriptFunction(
    function: String,
    params: Array<Any?>,
    callback: ValueCallback<String?>? = null,
) {
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
    fEvaluateJavascript(script, callback)
}

fun WebView.fEvaluateJavascript(
    script: String,
    callback: ValueCallback<String?>? = null,
) {
    this.evaluateJavascript(script, callback)
}