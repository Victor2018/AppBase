package com.victor.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.core.widget.NestedScrollView

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NoHorizontalNestedScrollView
 * Author: Victor
 * Date: 2026/1/30 17:21
 * Description: 
 * -----------------------------------------------------------------
 */

class NoHorizontalNestedScrollView : NestedScrollView {

    private var downX = 0
    private var downY = 0
    private var mTouchSlop = 0

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.rawX.toInt()
                downY = ev.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = ev.rawX.toInt()
                val moveY = ev.rawY.toInt()
                // 如果水平滑动距离大于垂直滑动距离，并且大于最小滑动距离，不拦截
                if (Math.abs(moveX - downX) > mTouchSlop &&
                    Math.abs(moveX - downX) > Math.abs(moveY - downY)) {
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}