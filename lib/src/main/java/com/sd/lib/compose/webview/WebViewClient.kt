package com.sd.lib.compose.webview

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.sd.lib.compose.webview.core.FWebViewManager
import com.sd.lib.compose.webview.core.logMsg
import java.lang.ref.WeakReference

open class FWebViewClient : AccompanistWebViewClient() {
    private var _contextRef: WeakReference<Context>? = null
    private val context: Context? get() = _contextRef?.get()

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        saveContext(view.context)

        val uri = request.url ?: return false
        val url = uri.toString()
        if (url.isEmpty()) return false
        if (url == view.url) return false

        logMsg { "shouldOverrideUrlLoading url:$url" }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return super.shouldOverrideUrlLoading(view, request)
        }

        return startUrl(url)
    }

    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        logMsg { "onPageStarted url:$url" }
    }

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        logMsg { "onPageFinished url:$url" }
        FWebViewManager.syncWebViewCookieToHttpClient(url)
    }

    override fun doUpdateVisitedHistory(view: WebView, url: String?, isReload: Boolean) {
        logMsg { "doUpdateVisitedHistory url:$url" }
        super.doUpdateVisitedHistory(view, url, isReload)
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        if (view != null && handler != null && error != null) {
            showTipsReceivedSslError(view, handler, error)
        } else {
            super.onReceivedSslError(view, handler, error)
        }
    }

    private fun saveContext(context: Context?) {
        if (context == null) return
        if (_contextRef?.get() !== context) {
            _contextRef = WeakReference(context)
        }
    }

    protected open fun startUrl(url: String): Boolean {
        val context = context ?: return false

        if (url.startsWith("intent://")) {
            return try {
                val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME).apply {
                    this.addCategory(Intent.CATEGORY_BROWSABLE)
                    this.component = null
                }
                context.startActivity(intent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        return try {
            val intent = Intent().apply {
                this.action = Intent.ACTION_VIEW
                this.data = Uri.parse(url)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    protected open fun showTipsReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        AlertDialog.Builder(view.context)
            .setMessage(R.string.lib_compose_webview_tips_onReceivedSslError)
            .setPositiveButton(R.string.lib_compose_webview_continue) { _, _ ->
                handler.proceed()
            }
            .setNegativeButton(R.string.lib_compose_webview_cancel) { _, _ ->
                handler.cancel()
            }
            .create()
            .show()
    }
}

open class FWebChromeClient : AccompanistWebChromeClient() {

    override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
        if (webView == null || filePathCallback == null || fileChooserParams == null) {
            return false
        }

        val context = webView.context
        if (context !is ComponentActivity) return false

        context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val value = FileChooserParams.parseResult(result.resultCode, result.data)
            filePathCallback.onReceiveValue(value)
        }.launch(fileChooserParams.createIntent())
        return true
    }
}