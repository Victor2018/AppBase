package com.victor.lib.widget

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.victor.lib.widget.data.WebConfigParm
import com.victor.lib.widget.databinding.WebProgressViewBinding
import com.victor.lib.widget.interfaces.OnWebViewStatusListener
import com.victor.lib.widget.util.StatusBarUtil.scanForActivity
import com.ydj.lib.common.module.WebViewModule

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ProgressWebView
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

open class ProgressWebView: FrameLayout,DownloadListener {
    private val TAG = "ProgressWebView"
    private lateinit var binding: WebProgressViewBinding

    var mOnWebViewStatusListener: OnWebViewStatusListener? = null
    var mWebConfigParm: WebConfigParm? = null

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        mWebConfigParm = WebConfigParm()
        mWebConfigParm?.uuid = WebViewModule.generateUniqueId()
        binding = WebProgressViewBinding.inflate(LayoutInflater.from(context),this,true)

        binding.mWebView.webChromeClient = MyWebChromeClient()
        binding.mWebView.webViewClient = MyWebViewClient()
        binding.mWebView.settings.javaScriptEnabled = true
        // 支持通过js打开新的窗口
        binding.mWebView.settings.javaScriptCanOpenWindowsAutomatically = true
        // 设置加载进来的页面自适应手机屏幕
        binding.mWebView.settings.useWideViewPort = true
        binding.mWebView.settings.loadWithOverviewMode = true

        // 启用或禁用 WebView 中的内容 URL 访问
//        binding.mWebView.settings.allowContentAccess = true

        // 支持自动加载图片
        binding.mWebView.settings.blockNetworkImage = false
        binding.mWebView.settings.loadsImagesAutomatically = true

        // 支持保存密码和表单数据
        binding.mWebView.settings.savePassword = true
        binding.mWebView.settings.saveFormData = true

        // 设置编码格式
        binding.mWebView.settings.defaultTextEncodingName = "utf-8"

        // 开启DOM storage API 功能
        binding.mWebView.settings.domStorageEnabled = true
        binding.mWebView.settings.databaseEnabled = true

        // 是否需要用户手势来播放媒体
        binding.mWebView.settings.mediaPlaybackRequiresUserGesture = false

        // 禁用使用其屏幕缩放进行缩放控制和手势
        binding.mWebView.settings.setSupportZoom(false)
        binding.mWebView.settings.builtInZoomControls = false
        binding.mWebView.settings.displayZoomControls = false

        // 告诉 WebView 按需启用、禁用或安装插件
        binding.mWebView.settings.pluginState = WebSettings.PluginState.ON

        // 允许跨域访问本地文件
        binding.mWebView.settings.allowFileAccess = true
        binding.mWebView.settings.allowUniversalAccessFromFileURLs = true
        binding.mWebView.settings.allowFileAccessFromFileURLs = true

        // 支持同时加载Https和Http混合模式
        binding.mWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

        binding.mWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE

        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.mWebView, true)
        CookieManager.getInstance().setAcceptCookie(true)

        binding.mWebView.setDownloadListener(this)

//        addJavascriptInterface(YdjJSInterface(context, this), NAME)
    }

    override fun onDownloadStart(url: String?, userAgent: String?, contentDisposition: String?, mimetype: String?, contentLength: Long) {
        Log.e(TAG,"onDownloadStart()......url = $url")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)

//        downloadFile(url,contentDisposition,mimeType)
    }

    fun setProgress(newProgress: Int) {
        if (newProgress == 100) {
            binding.mPbLoading.visibility = GONE
        } else {
            binding.mPbLoading.progress = newProgress
        }
    }

    fun setUserAgent(userAgent: String?) {
        // 设置 User-Agent
        if (!TextUtils.isEmpty(userAgent)) {
            binding.mWebView.settings.userAgentString = userAgent
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            setProgress(newProgress)
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            Log.i(TAG,"onProgressChanged()......title = $title")
            if (title.contains("html")) {
                return
            }
        }
    }

    private inner class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url?.toString() ?: ""
            Log.i(TAG,"shouldOverrideUrlLoading()......url = $url")
            if (!url.startsWith("http")) {
                return deepLink(view, url)
            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            // 注入 JS 代码
            Log.d(TAG, "JS Injection Start: ${mWebConfigParm?.preJs}")
            mWebConfigParm?.preJs?.let {
                view.evaluateJavascript(it) { result ->
                    Log.d(TAG, "JS Injection End: $result")
                }
            }
            mOnWebViewStatusListener?.onPageFinished(view,url)
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            super.onReceivedError(view, request, error)
            //在访问失败的时候会首先回调onReceivedError，然后再回调onPageFinished。
            val url = request.url?.toString() ?: ""
            Log.e(TAG, "onReceivedError()-url = $url")
            mOnWebViewStatusListener?.onReceivedError(view,request,error)
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {

            val url = request?.url.toString()
            Log.i(TAG, "shouldInterceptRequest-url = $url")

            mOnWebViewStatusListener?.shouldInterceptRequest(view, request)

            return super.shouldInterceptRequest(view, request)
        }
    }

    private fun resetWebViewState() {
        Log.i(TAG,"resetWebViewState()......")
        // 显示进度条
        binding.mPbLoading.visibility = VISIBLE
        binding.mPbLoading.progress = 0

        binding.mWebView.clearHistory()
        binding.mWebView.clearCache(true)
        binding.mWebView.clearFormData()
    }

    fun loadUrl(url: String) {
        Log.i(TAG,"loadUrl()......url = $url")
        try {
            resetWebViewState()
            binding.mWebView.loadUrl(url)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadData (htmlData: String) {
        if (TextUtils.isEmpty(htmlData)) return
        binding.mWebView.loadData(htmlData,"text/html","utf-8")
    }

    fun reload() {
        binding.mWebView.reload()
    }

    /**
     * 千万不要更改这个 "name"  注意！！！！！
     */
    @SuppressLint("JavascriptInterface")
    fun addJavascriptInterface(jsInterface: Any, name: String) {
        binding.mWebView.addJavascriptInterface(jsInterface, name)
    }

    fun evaluateJavascript(script: String?) {
        script?.let {
            binding.mWebView.evaluateJavascript(it){result ->
                Log.i(TAG, "evaluateJavascript-result =  $result")
            }
        }
    }

    fun downloadFile(url: String?,contentDisposition: String?,mimeType: String?) {
        val request = DownloadManager.Request(Uri.parse(url))
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner()
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        // 设置通知栏的标题，如果不设置，默认使用文件名
        request.setTitle("下载完成")
        // 设置通知栏的描述
//                    request.setDescription("This is description");
        // 允许在计费流量下下载
        request.setAllowedOverMetered(true)
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(true)
        // 允许漫游时下载
        request.setAllowedOverRoaming(true)

        val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
        Log.e(TAG, "downloadFile()-fileName = $fileName")
        request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().toString() + "/Download/", fileName)


        val downloadManager = binding.mWebView.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        // 添加一个下载任务
        val downloadId = downloadManager.enqueue(request)
    }

    fun deepLink(view: WebView,url: String): Boolean {
        Log.e(TAG, "deepLink()-url = $url")
        if (TextUtils.isEmpty(url)) return false
        return try {
            //不是http 开始就是 scheme URL 使用系统拉起scheme deeplink
            launchWeb(view.context,url,false)
            true
        } catch (e: Exception) {//防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
            e.printStackTrace()
            true//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
        }
    }

    fun launchWeb (context: Context?,url: String,finishAct: Boolean) {
        try {
            val intent = Intent(Intent.ACTION_VIEW,Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
            if (finishAct) {
                val activity = scanForActivity(context)
                activity?.finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun canGoBack(): Boolean {
        val canGoBack = binding.mWebView.canGoBack()
        if (canGoBack) {
            binding.mWebView.goBack()
        }
        return canGoBack
    }

    fun onPause () {
        binding.mWebView.pauseTimers()
    }

    fun onResume () {
        binding.mWebView.resumeTimers()
    }

    /**
     * must be called on the main thread
     */
    fun onDestroy() {
        try {
            binding.mWebView.clearHistory()
            binding.mWebView.clearCache(true)
            // 删除所有 WebStorage 数据
            WebStorage.getInstance().deleteAllData()
            binding.mWebView.webChromeClient = null
            binding.mWebView.loadUrl("about:blank") // clearView() should be changed to loadUrl("about:blank"), since clearView() is deprecated now
            binding.mWebView.settings.javaScriptEnabled = false
            binding.mWebView.parent.let {
                if (it is ViewGroup) {
                    it.removeView(binding.mWebView)
                }
            }
            binding.mWebView.removeAllViewsInLayout()
            binding.mWebView.removeAllViews()
            binding.mWebView.freeMemory()
            binding.mWebView.pauseTimers()
            binding.mWebView.destroy() // Note that mWebView.destroy() and mWebView = null do the exact same thing
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setWebViewBackgroundColor(isBlack: Boolean) {
        if (isBlack) {
            //防止加载html白屏(针对播放视频)
            setBackgroundColor(Color.BLACK)
        }
    }

    fun getWebView(): WebView {
        return binding.mWebView
    }
}