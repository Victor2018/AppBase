package com.victor.lib.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.victor.lib.widget.interfaces.WindowInsetsHandler


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: InsetsToolbar.java
 * Author: Victor
 * Date: 2019/10/14 16:06
 * Description:
 * -----------------------------------------------------------------
 */
class InsetsToolbar: Toolbar, WindowInsetsHandler {

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            val l = insets.systemWindowInsetLeft
            val t = insets.systemWindowInsetTop
            val r = insets.systemWindowInsetRight
            setPadding(l, t, r, 0)
            insets.consumeSystemWindowInsets()
        }

    }

    override fun onApplyWindowInsets(insets: Rect): Boolean {
        setPadding(insets.left, insets.top, insets.right, 0);
        return true
    }

}