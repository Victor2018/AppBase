package com.victor.lib.widget

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NoSpaceTextView
 * Author: Victor
 * Date: 2023/07/28 15:36
 * Description: 
 * -----------------------------------------------------------------
 */

class NoSpaceTextView : AppCompatTextView {
    /**
     * 控制measure()方法 刷新测量
     */
    private var refreshMeasure = false

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        removeSpace(widthMeasureSpec, heightMeasureSpec)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        // 每次文本内容改变时，需要测量两次，确保计算的高度没有问题
        refreshMeasure = true
    }

    /**
     * 这里处理文本的上下留白问题
     */
    private fun removeSpace(widthspc: Int, heightspc: Int) {
        var paddingTop = 0
        val linesText = getLinesText()
        val paint = paint
        val rect = Rect()
        val text = linesText[0]
        paint.getTextBounds(text, 0, text!!.length, rect)
        val fontMetricsInt = Paint.FontMetricsInt()
        paint.getFontMetricsInt(fontMetricsInt)
        paddingTop = fontMetricsInt.top - rect.top

        // 设置TextView向上的padding (小于0, 即把TextView文本内容向上移动)
        setPadding(
            leftPaddingOffset,
            paddingTop + topPaddingOffset,
            rightPaddingOffset,
            bottomPaddingOffset
        )
        val endText = linesText[linesText.size - 1]
        paint.getTextBounds(endText, 0, endText!!.length, rect)

        // 再减去最后一行文本的底部空白，得到的就是TextView内容上线贴边的的高度，到达消除文本上下留白的问题
        setMeasuredDimension(
            measuredWidth, measuredHeight - (fontMetricsInt.bottom - rect.bottom)
        )
        if (refreshMeasure) {
            refreshMeasure = false
            measure(widthspc, heightspc)
        }
    }

    /**
     * 获取每一行的文本内容
     */
    private fun getLinesText(): Array<String?> {
        var start = 0
        var end = 0
        val texts = arrayOfNulls<String>(lineCount)
        val text = text.toString()
        for (i in 0 until lineCount) {
            end = layout.getLineEnd(i)
            val line = text.substring(start, end) //指定行的内容
            start = end
            texts[i] = line
        }
        return texts
    }
}