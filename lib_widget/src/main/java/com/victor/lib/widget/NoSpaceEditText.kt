package com.victor.lib.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.victor.lib.widget.etfilter.SpaceInputFilter

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NoSpaceEditText
 * Author: Victor
 * Date: 2026/4/19 15:23
 * Description: 自动剔除空格输入
 * -----------------------------------------------------------------
 */

class NoSpaceEditText: AppCompatEditText {
    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,android.R.attr.editTextStyle)

    //注意：自定义EditText defStyleAttr必须设置android.R.attr.editTextStyle否则无法输入内容
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        filters += SpaceInputFilter()
    }
}