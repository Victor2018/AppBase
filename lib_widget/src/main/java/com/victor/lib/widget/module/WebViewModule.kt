package com.ydj.lib.common.module

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import com.victor.lib.widget.ProgressWebView
import com.victor.lib.widget.data.WebConfigParm
import java.util.UUID

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WebViewModule
 * Author: Victor
 * Date: 2026/2/24 14:43
 * Description: webview 管理模块
 * -----------------------------------------------------------------
 */

object WebViewModule {
    private const val TAG = "WebViewModule"
    const val NAME = "YdjSDK"

    private val webViewMap = mutableMapOf<String, ProgressWebView>()
    private var appContext: Context? = null

    // 初始化单例对象
    fun initialize(context: Context) {
        if (appContext == null) {
            appContext = context.applicationContext
            Log.d(TAG, "WebViewManage initialized with context: $appContext")
        } else {
            Log.w(TAG, "WebViewManage is already initialized.")
        }
    }

    // 创建 WebView 实例并返回唯一的 ID，加载指定 URL
    fun createWebView(url: String, config: WebConfigParm? = null): String {
        val context = appContext
            ?: throw IllegalStateException("WebViewManage is not initialized. Call initialize() first.")

        val id = generateUniqueId()
        config?.uuid = id
        config?.url = url

        Log.d(TAG, "Creating WebView with ID: $id, URL: $url")

        val webView = ProgressWebView(context).apply {
            setUserAgent(config?.userAgent)
//            addJavascriptInterface(YdjJSInterface(context, this), NAME)
            requestFocus(View.FOCUS_DOWN)
            isFocusableInTouchMode = true
            isFocusable = true
            loadUrl(url)
        }

        webViewMap[id] = webView
        Log.d(TAG, "WebView created and added to map with ID: $id")
        return id
    }

    // 通过 ID 获取 WebView 实例
    fun getWebView(id: String): ProgressWebView? {
        val webView = webViewMap[id]
        Log.d(TAG, "Getting WebView with ID: $id, Found: ${webView != null}")
        return webView
    }

    // 通过 ID 移除 WebView 实例
    fun removeWebView(id: String) {
        val webView = webViewMap[id]
        if (webView != null) {
            Log.d(TAG, "Removing WebView with ID: $id")

            // 将 WebView 隐藏
            webView.visibility = View.GONE

            // 销毁 WebView 实例
            webView.onDestroy()

            // 从 map 中移除 WebView
            webViewMap.remove(id)
            Log.d(TAG, "WebView with ID: $id removed and destroyed")
        } else {
            Log.w(TAG, "WebView with ID: $id not found")
        }
    }

    // 移除所有 WebView 实例
    fun removeAllWebViews() {
        Log.d(TAG, "Removing all WebViews")
        for (webView in webViewMap.values) {
            webView.onDestroy()
        }
        webViewMap.clear()
        Log.d(TAG, "All WebViews removed and destroyed")
    }

    // 生成唯一的 ID
    fun generateUniqueId(): String {
        val id = UUID.randomUUID().toString()
        Log.d(TAG, "Generated unique ID: $id")
        return id
    }

    // 通过 ID 截取 WebView 的截图并返回 Bitmap
    fun captureWebView(id: String): Bitmap? {
        val webView = webViewMap[id]
        if (webView != null) {
            Log.d(TAG, "Capturing WebView with ID: $id")

            webView.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(webView.drawingCache)
            webView.isDrawingCacheEnabled = false

            Log.d(TAG, "WebView with ID: $id captured successfully")
            return bitmap
        } else {
            Log.w(TAG, "WebView with ID: $id not found for capture")
            return null
        }
    }

    // 获取所有 GoodsWebView 的 webViewConfig 并返回一个集合
    fun getAllWebViewConfigs(): List<WebConfigParm> {
        val configs = webViewMap.values.mapNotNull { it.mWebConfigParm }
        Log.d(TAG, "Retrieved WebViewConfigs: ${configs.size}")
        return configs
    }

    // 添加已有的 WebView 实例到管理类中
    fun addWebView(id: String, webView: ProgressWebView) {
        if (webViewMap.containsKey(id)) {
            Log.w(TAG, "WebView with ID: $id already exists.")
            return
        }
        webViewMap[id] = webView
        Log.d(TAG, "WebView added to map with ID: $id")
    }
}