package com.sd.lib.compose.webview

import android.webkit.ValueCallback
import android.webkit.WebView

@JvmOverloads
fun WebView.fJsFunction(
    function: String,
    params: Array<Any?> = arrayOf(),
    callback: ValueCallback<String?>? = null,
) {
    if (function.isEmpty()) return
    val paramsString = if (params.isEmpty()) "" else {
        params.joinToString(
            separator = ",",
            transform = {
                if (it is String) {
                    "'${it}'"
                } else {
                    it.toString()
                }
            }
        )
    }
    val script = "$function($paramsString)"
    evaluateJavascript(script, callback)
}