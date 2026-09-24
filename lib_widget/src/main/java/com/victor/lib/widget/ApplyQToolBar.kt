package com.victor.lib.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.victor.lib.widget.util.ResUtils
import com.victor.lib.widget.util.StatusBarUtil

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ApplyQToolBar
 * Author: Victor
 * Date: 2026/1/7 9:41
 * Description: 
 * -----------------------------------------------------------------
 */

class ApplyQToolBar: Toolbar {
    private val TAG = "ApplyQToolBar"

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post{
            setupToolbarWithInsets()
        }
    }

    fun setupToolbarWithInsets() {
        try {
            Log.i(TAG,"setupToolbarWithInsets()......SDK_INT = ${Build.VERSION.SDK_INT}")
            val statusBarHeight = StatusBarUtil.getStatusBarHeight(context)
            Log.i(TAG,"setupToolbarWithInsets()......getStatusBarHeight = $statusBarHeight")
            val params = layoutParams as? MarginLayoutParams ?: return
            Log.i(TAG,"setupToolbarWithInsets()......old-topMargin = ${params.topMargin}")
            // Android 10、7.1.1 才设置 dp_88 的 marginTop
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q ||
                Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                var marginTop = ResUtils.getDimenPixRes(context,com.victor.screen.match.library.R.dimen.dp_84)
                val location = IntArray(2)
                getLocationOnScreen(location)
                val contentAreaTopDistance = location[1]
                if (contentAreaTopDistance < marginTop) {
                    marginTop -= contentAreaTopDistance
                }

                params.topMargin = marginTop
                layoutParams = params
                Log.i(TAG,"setupToolbarWithInsets()......new topMargin = $marginTop")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getActionBarSize(): Int {
        var actionBarSize = ResUtils.getDimenPixRes(context,com.victor.screen.match.library.R.dimen.dp_84)
        try {
            actionBarSize = with(context) {
                // 从当前主题中读取 actionBarSize 属性的值
                val styledAttributes = obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
                val size = styledAttributes.getDimensionPixelSize(0, 0)
                styledAttributes.recycle()
                size // 这个值通常是 56dp 在手机上的像素值
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return actionBarSize
    }

}