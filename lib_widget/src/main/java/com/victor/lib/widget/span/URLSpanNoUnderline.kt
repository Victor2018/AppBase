package com.victor.lib.widget.span

import android.text.TextPaint
import android.text.style.URLSpan

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: URLSpanNoUnderline
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

class URLSpanNoUnderline(url: String,val linkColor: Int) : URLSpan(url) {
    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
        if (linkColor != 0) {
            ds.color = linkColor
        }
    }
}