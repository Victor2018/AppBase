package com.victor.lib.widget

import android.content.Context
import android.content.res.TypedArray
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.victor.lib.widget.util.ResUtils
import com.victor.lib.widget.util.SpannableUtil

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LinkTextView
 * Author: Victor
 * Date: 2025/12/12 10:45
 * Description: 带跳转链接的TextView
 * -----------------------------------------------------------------
 */

class LinkTextView: AppCompatTextView {

    private var underLineShow = false
    private var linkTextColor = 0

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(context,attrs)
    }

    fun initAttrs (context: Context, attrs: AttributeSet?) {
        movementMethod = LinkMovementMethod.getInstance()

        val a: TypedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.LinkTextView, 0, 0)

        try {
            underLineShow = a.getBoolean(R.styleable.LinkTextView_ltv_under_line_show, false)
            linkTextColor = a.getColor(R.styleable.LinkTextView_highlight_link_text_color,
                ResUtils.getColorRes(context,R.color.color_0084FF))
        } finally {
            a.recycle()
        }

       if (!underLineShow) {
           //去掉下划线
           SpannableUtil.stripUnderlines(this,linkTextColor)
       }
    }
}