package com.victor.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.Switch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ResizableSwitch
 * Author: Victor
 * Date: 2026/3/10 20:44
 * Description: 
 * -----------------------------------------------------------------
 */

class ResizableSwitch: Switch {
    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 先让父类测量一次（得到默认的测量值）
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // 获取 XML 中设置的尺寸模式和建议值
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        // 默认的测量值（父类计算出的理想大小）
        val defaultWidth = measuredWidth
        val defaultHeight = measuredHeight

        val finalWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize  // XML 中设置了具体数值或 match_parent
            MeasureSpec.AT_MOST -> minOf(defaultWidth, widthSize)  // wrap_content，不能超过最大值
            else -> defaultWidth  // UNSPECIFIED
        }

        val finalHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> minOf(defaultHeight, heightSize)
            else -> defaultHeight
        }

        // 设置最终尺寸
        setMeasuredDimension(finalWidth, finalHeight)
    }
}