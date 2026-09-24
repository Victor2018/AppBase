package com.victor.lib.widget

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MaxHeightLMRecyclerView
 * Author: Victor
 * Date: 2023/06/29 21:10
 * Description: 
 * -----------------------------------------------------------------
 */

class MaxHeightLMRecyclerView : LMRecyclerView {
    private var maxHeight = -1

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context,attrs,defStyleAttr)
    }

    // Modified changes
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        context.withStyledAttributes(
            attrs, R.styleable.MaxHeightNestedScrollView, defStyleAttr, 0
        ) {
            maxHeight = getDimensionPixelSize(R.styleable.MaxHeightNestedScrollView_maxHeight, 0)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        if (maxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setMaxHeight (height: Int) {
        maxHeight = height
        requestLayout()
    }
}