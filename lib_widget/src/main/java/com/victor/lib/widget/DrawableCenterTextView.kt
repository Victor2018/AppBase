package com.victor.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.withStyledAttributes


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DrawableCenterTextView
 * Author: Victor
 * Date: 2022/3/30 10:24
 * Description: 
 * -----------------------------------------------------------------
 */

class DrawableCenterTextView : AppCompatTextView {

    companion object {
        const val START = 1
        const val CENTER = 2
        const val END = 3
    }

    var mCtvGravity = CENTER

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(attrs)
    }

    fun initAttrs(attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.DrawableCenterTextView) {
            mCtvGravity = getInt(R.styleable.DrawableCenterTextView_ctv_gravity, CENTER)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        //All these logic should happen before onDraw()
        val drawables = compoundDrawables //drawables always not null;
        val drawableLeft = drawables[0]
        val drawableTop = drawables[1]
        val drawableRight = drawables[2]
        val drawableBottom = drawables[3]
        val text = text.toString()
        val textWidth = paint.measureText(text, 0, text.length)
        val textHeight = (lineHeight * lineCount).toDouble()

        var totalDrawablePaddingH = 0 //the total horizontal padding of drawableLeft and drawableRight
        var totalDrawablePaddingV = 0 //the total vertical padding of drawableTop and drawableBottom
        var totalDrawableWidth = 0 //the total width of drawableLeft and drawableRight
        var totalDrawableHeight = 0 //the total height of drawableTop and drawableBottom
        var drawableVHeight = 0 //the height of drawableLeft or drawableRight
        var totalWidth = 0f //the total width of drawableLeft , drawableRight and text
        var totalHeight = 0f //the total height of drawableTop , drawableBottom and text
        var paddingH = 0 //the horizontal padding,used both left and right
        var paddingV = 0 //the vertical padding,used both top and bottom

        // measure width
        if (drawableLeft != null) {
            totalDrawableWidth += drawableLeft.intrinsicWidth
            totalDrawablePaddingH += compoundDrawablePadding //drawablePadding

            drawableVHeight = drawableLeft.intrinsicHeight

            paddingV = 0

            totalWidth = textWidth + totalDrawableWidth + totalDrawablePaddingH
            paddingH = (width - totalWidth).toInt() / 2
        }
        if (drawableRight != null) {
            totalDrawableWidth += drawableRight.intrinsicWidth
            totalDrawablePaddingH += compoundDrawablePadding

            drawableVHeight = drawableRight.intrinsicHeight

            paddingV = 0

            totalWidth = textWidth + totalDrawableWidth + totalDrawablePaddingH
            paddingH = (width - totalWidth).toInt() / 2
        }

        // measure height
        if (drawableTop != null) {
            totalDrawableHeight += drawableTop.intrinsicHeight
            totalDrawablePaddingV += compoundDrawablePadding

            paddingH = 0

            totalHeight = (Math.max(drawableVHeight.toFloat(),textHeight.toFloat()) + totalDrawableHeight + totalDrawablePaddingV)
            paddingV = (height - totalHeight).toInt() / 2
        }
        if (drawableBottom != null) {
            totalDrawableHeight += drawableBottom.intrinsicHeight
            totalDrawablePaddingV += compoundDrawablePadding

            paddingH = 0

            totalHeight = (Math.max(drawableVHeight.toFloat(),textHeight.toFloat()) + totalDrawableHeight + totalDrawablePaddingV)
            paddingV = (height - totalHeight).toInt() / 2
        }

        setTextGravity(paddingH, paddingV, paddingH, paddingV)
    }

    fun setTextGravity(left: Int, top: Int, right: Int, bottom: Int) {
        Log.e("DrawableCenterTextView","setTextGravity()......")
        when (mCtvGravity) {
            START -> {
                Log.d("DrawableCenterTextView","setTextGravity()......START")
                setPadding(0, top, right + left, bottom)
            }
            CENTER -> {
                Log.d("DrawableCenterTextView","setTextGravity()......CENTER")
                setPadding(left, top, right, bottom)
            }
            END -> {
                Log.d("DrawableCenterTextView","setTextGravity()......END")
                setPadding(left * 2, top, 0, bottom)
            }
            else -> {
                Log.d("DrawableCenterTextView","setTextGravity()......else")
                setPadding(left, top, right, bottom)
            }
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        requestLayout()
    }
}