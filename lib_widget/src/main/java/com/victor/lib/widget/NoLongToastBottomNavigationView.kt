package com.victor.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.get
import com.google.android.material.bottomnavigation.BottomNavigationView

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NoLongToastBottomNavigationView
 * Author: Victor
 * Date: 2025/11/21 8:39
 * Description: 移除长按点击弹出Toast
 * -----------------------------------------------------------------
 */

class NoLongToastBottomNavigationView: BottomNavigationView {
    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        itemIconTintList = null//解决图标被颜色覆盖问题
        removeLongTouchToast()
    }

    /**
     * 移除长按点击弹出Toast
     */
    fun BottomNavigationView.removeLongTouchToast() {
        val bottomNavigationMenuView = this.getChildAt(0) as ViewGroup
        val size = bottomNavigationMenuView.childCount
        for (index in 0 until size) {
            bottomNavigationMenuView[index].setOnLongClickListener {
                true
            }
        }
    }
}