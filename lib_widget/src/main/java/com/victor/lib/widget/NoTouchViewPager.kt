package com.victor.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NoTouchViewPager
 * Author: Victor
 * Date: 2022/10/20 11:42
 * Description: 
 * -----------------------------------------------------------------
 */

class NoTouchViewPager : ViewPager {
    var canScroll = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return canScroll && super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return canScroll && super.onTouchEvent(ev)
    }

}