package com.victor.lib.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.victor.lib.widget.interfaces.WindowInsetsHandler
import com.victor.lib.widget.interfaces.WindowInsetsHandlingBehavior
import com.victor.lib.widget.util.WindowInsetsHelper

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: InsetsAppBarLayout.java
 * Author: Victor
 * Date: 2019/10/14 16:16
 * Description:
 * -----------------------------------------------------------------
 */
class InsetsAppBarLayout: AppBarLayout, WindowInsetsHandler {
    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    }

    override fun onApplyWindowInsets(insets: Rect): Boolean {
        return WindowInsetsHelper.dispatchApplyWindowInsets(this, insets)
    }

    class Behavior : AppBarLayout.Behavior, WindowInsetsHandlingBehavior {

        constructor() {}

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

        override fun onApplyWindowInsets(layout: CoordinatorLayout, child: View, insets: Rect): Boolean {
            return WindowInsetsHelper.onApplyWindowInsets(child, insets)
        }

    }
}