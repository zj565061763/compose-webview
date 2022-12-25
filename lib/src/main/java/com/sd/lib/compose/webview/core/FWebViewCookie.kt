package com.sd.lib.compose.webview.core

import android.webkit.CookieManager
import java.net.HttpCookie
import java.net.URI
import java.net.URISyntaxException

object FWebViewCookie {

    //---------- get ----------

    @JvmStatic
    fun getCookie(url: String?): String {
        if (url.isNullOrEmpty()) return ""
        return CookieManager.getInstance().getCookie(url) ?: ""
    }

    @JvmStatic
    fun getCookieAsList(url: String?): List<HttpCookie> {
        val cookie = getCookie(url)
        if (cookie.isEmpty()) return listOf()

        val listSplit = cookie.split(";")
        if (listSplit.isEmpty()) return listOf()

        val listResult = mutableListOf<HttpCookie>()
        for (item in listSplit) {
            item.toHttpCookie()?.let {
                listResult.add(it)
            }
        }
        return listResult
    }

    //---------- set ----------

    @JvmStatic
    fun setCookie(url: String?, listCookie: List<HttpCookie>?): Boolean {
        val uri = url.toURI() ?: return false
        if (listCookie.isNullOrEmpty()) return false
        for (cookie in listCookie) {
            if (!setCookieInternal(uri, cookie.toCookieString())) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun setCookie(url: String?, cookie: HttpCookie?): Boolean {
        return setCookie(url, cookie.toCookieString())
    }

    @JvmStatic
    fun setCookie(url: String?, cookie: String?): Boolean {
        val uri = url.toURI() ?: return false
        return setCookieInternal(uri, cookie)
    }

    private fun setCookieInternal(uri: URI, cookie: String?): Boolean {
        if (cookie.isNullOrEmpty()) return false
        val url = "${uri.scheme}://${uri.host}"
        CookieManager.getInstance().setCookie(url, cookie)
        return true
    }
}

private fun String.toHttpCookie(): HttpCookie? {
    val listPair = this.split("=")
    if (listPair.size != 2) return null
    return try {
        HttpCookie(listPair[0], listPair[1])
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun String?.toURI(): URI? {
    if (this.isNullOrEmpty()) return null
    return try {
        URI(this)
    } catch (e: URISyntaxException) {
        e.printStackTrace()
        null
    }
}

private fun HttpCookie?.toCookieString(): String? {
    if (this == null) return null
    return this.toString()
}