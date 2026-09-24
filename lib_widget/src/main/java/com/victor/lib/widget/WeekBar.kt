package com.victor.lib.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.victor.lib.widget.util.ResUtils

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WeekBar
 * Author: Victor
 * Date: 2022/10/12 18:52
 * Description: 
 * -----------------------------------------------------------------
 */

class WeekBar : AppCompatTextView {

    var days: Array<String>? = null

    private val type = 0 //一周的第一天是周几 0，周日；1，周一

    private var textPaint: TextPaint? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        textPaint = paint
        textPaint?.textAlign = Paint.Align.CENTER
        days = ResUtils.getStringArrayRes(context,R.array.week)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom
        val width = measuredWidth - paddingRight - paddingLeft
        val height = measuredHeight - paddingTop - paddingBottom
        for (i in days!!.indices) {
            val rect = Rect(
                paddingLeft + i * width / days!!.size,
                paddingTop,
                paddingLeft + (i + 1) * width / days!!.size,
                paddingTop + height
            )
            val fontMetrics = textPaint!!.fontMetrics
            val top = fontMetrics.top
            val bottom = fontMetrics.bottom
            val baseLineY = (rect.centerY() - top / 2 - bottom / 2).toInt()
            var day: String
            day = if (type == 1) {
                val j = i + 1
                days!![if (j > days!!.size - 1) 0 else j]
            } else {
                days!![i]
            }
            canvas.drawText(day, rect.centerX().toFloat(), baseLineY.toFloat(), textPaint!!)
        }
    }

}