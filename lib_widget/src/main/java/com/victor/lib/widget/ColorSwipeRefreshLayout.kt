package com.victor.lib.widget

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ColorSwipeRefreshLayout
 * Author: Victor
 * Date: 2022/10/17 12:13
 * Description: 
 * -----------------------------------------------------------------
 */

open class ColorSwipeRefreshLayout : SwipeRefreshLayout {
//    private var srlProgressBackgroundColorScheme: Int = Color.BLUE
    private var srlColorSchemeResourceId: Int = 0
    private var srlProgressViewStartOffset = 0
    private var srlProgressViewEndOffset = 100
    private var srlProgressViewScale = false

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorSwipeRefreshLayout)
//        srlProgressBackgroundColorScheme = typedArray.getColor(
//            R.styleable.ColorSwipeRefreshLayout_srlProgressBackgroundColorScheme,
//            ResUtils.getColorRes(R.color.colorAccent))
        srlColorSchemeResourceId = typedArray.getResourceId(
            R.styleable.ColorSwipeRefreshLayout_srlColorSchemeResourceId,
            R.color.colorAccent)
        srlProgressViewStartOffset = typedArray.getDimensionPixelSize(R.styleable.ColorSwipeRefreshLayout_srlProgressViewStartOffset, 0)
        srlProgressViewEndOffset = typedArray.getDimensionPixelSize(R.styleable.ColorSwipeRefreshLayout_srlProgressViewEndOffset, 100)
        srlProgressViewScale = typedArray.getBoolean(R.styleable.ColorSwipeRefreshLayout_srlProgressViewScale, srlProgressViewScale)
        typedArray.recycle()

        //这种方式xml中不能预览布局
//        setColorSchemeColors(srlProgressBackgroundColorScheme)
        setColorSchemeResources(srlColorSchemeResourceId)
        setProgressViewOffset(srlProgressViewScale,srlProgressViewStartOffset, srlProgressViewEndOffset)
    }
}