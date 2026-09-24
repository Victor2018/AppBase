package com.victor.lib.widget

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RefreshAppBarLayout
 * Author: Victor
 * Date: 2026/4/3 14:27
 * Description: 
 * -----------------------------------------------------------------
 */

class RefreshAppBarLayout: AppBarLayout,AppBarLayout.OnOffsetChangedListener {

    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        addOnOffsetChangedListener(this)
    }

    fun attachToSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {
        mSwipeRefreshLayout = swipeRefreshLayout
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        mSwipeRefreshLayout?.isEnabled = verticalOffset >= 0
    }
}