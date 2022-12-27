package com.sd.lib.compose.webview

import android.webkit.ValueCallback
import android.webkit.WebView

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