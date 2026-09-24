package com.victor.lib.widget.interfaces

import android.graphics.Rect
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WindowInsetsHandlingBehavior.java
 * Author: Victor
 * Date: 2019/10/14 16:24
 * Description:
 * -----------------------------------------------------------------
 */
interface WindowInsetsHandlingBehavior {
    fun onApplyWindowInsets(layout: CoordinatorLayout, child: View, insets: Rect): Boolean
}