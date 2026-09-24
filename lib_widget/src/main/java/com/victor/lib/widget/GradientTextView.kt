package com.victor.lib.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GradientTextView
 * Author: Victor
 * Date: 2024/06/03 11:55
 * Description: 
 * -----------------------------------------------------------------
 */

class GradientTextView : AppCompatTextView {
    private var mStartColor = 0
    private var mEndColor = 0
    private var mBold = false
    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context,  attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.GradientTextView)
            ?: return
        mBold = typedArray.getBoolean(
            R.styleable.GradientTextView_gradient_bold,
            false
        )
        mStartColor = typedArray.getColor(
            R.styleable.GradientTextView_gradient_start_color,
            Color.BLACK
        )
        mEndColor = typedArray.getColor(
            R.styleable.GradientTextView_gradient_end_color,
            Color.BLACK
        )
        typedArray.recycle()
        if (mBold) {
            paint.isFakeBoldText = true
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            paint.shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                mStartColor,
                mEndColor,
                Shader.TileMode.CLAMP
            )
        }
    }

    fun setGradientStartColor(gradientStartColor: Int) {
        mStartColor = gradientStartColor
        requestLayout()
    }

    fun setGradientEndColor(gradientEndColor: Int) {
        mEndColor = gradientEndColor
        requestLayout()
    }

}