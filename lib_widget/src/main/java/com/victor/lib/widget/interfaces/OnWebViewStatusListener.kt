package com.victor.lib.widget.interfaces

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnWebViewStatusListener
 * Author: Victor
 * Date: 2026/2/24 13:33
 * Description: 
 * -----------------------------------------------------------------
 */

interface OnWebViewStatusListener {
    fun onPageFinished(view: WebView, url: String)
    fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?)
    fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?)
}