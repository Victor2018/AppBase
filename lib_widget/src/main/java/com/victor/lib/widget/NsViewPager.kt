package com.victor.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NsViewPager
 * Author: Victor
 * Date: 2022/6/9 17:21
 * Description: 
 * -----------------------------------------------------------------
 */

class NsViewPager : ViewPager {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        for (i in 0 until childCount) {
            val child: View = getChildAt(i)
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val h: Int = child.measuredHeight
            if (h > height) height = h
        }

        var measureSpecHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, measureSpecHeight)
    }

}