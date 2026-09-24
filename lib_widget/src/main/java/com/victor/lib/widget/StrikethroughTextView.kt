package com.victor.lib.widget

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: StrikethroughTextView
 * Author: Victor
 * Date: 2026/1/29 14:53
 * Description: 
 * -----------------------------------------------------------------
 */

class StrikethroughTextView: AppCompatTextView {
    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs,defStyle) {
        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }
}