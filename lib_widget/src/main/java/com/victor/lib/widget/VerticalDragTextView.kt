package com.victor.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.victor.lib.widget.util.ResUtils
import java.lang.Math.sqrt

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DragTextView
 * Author: Victor
 * Date: 2026/3/23 20:36
 * Description: 
 * -----------------------------------------------------------------
 */

class VerticalDragTextView: AppCompatTextView {
    private var isDrag = false
    private var lastY = 0F

    private var mParent: ViewGroup? = null
    private var mParentWidth = 0
    private var mParentHeight = 0
    private var mStatusBarHeight = 0

    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mStatusBarHeight = ResUtils.getDimenPixRes(context,com.victor.screen.match.library.R.dimen.dp_188)
    }

    fun attachToSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {
        mSwipeRefreshLayout = swipeRefreshLayout
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val rawX = ev.rawX
        val rawY = ev.rawY
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mSwipeRefreshLayout?.isEnabled = false
                isDrag = false
                parent.requestDisallowInterceptTouchEvent(true)
                lastY = rawY

                if (parent != null) {
                    mParent = parent as ViewGroup
                    mParentWidth = mParent!!.width
                    mParentHeight = mParent!!.height
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (mParentWidth < 0.2 || mParentHeight < 0.2) {
                    return super.onTouchEvent(ev)
                }
                val dy = rawY - lastY
                val distance = sqrt((dy * dy).toDouble())
                val isMove = distance >= 3.0
                if (!isMove) {
                    return super.onTouchEvent(ev)
                }
                isDrag = true
                var y = y + dy
                //检测是否到达边缘 左上右下
                y = when {
                    y < 0 -> {
                        0F
                    }
                    y < mStatusBarHeight -> {
                        mStatusBarHeight.toFloat()
                    }
                    y > mParentHeight - height * 2 -> {
                        (mParentHeight - height * 2).toFloat()
                    }
                    else -> {
                        y
                    }
                }

                setY(y)

                lastY = rawY
            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL -> {
                mSwipeRefreshLayout?.isEnabled = true
                if (isDrag) {
                    isDrag = false
                    //恢复按压效果
                    isPressed = false
                } else {
                    performClick()
                }
            }
        }

        return !isDrag || super.onTouchEvent(ev)
    }
}