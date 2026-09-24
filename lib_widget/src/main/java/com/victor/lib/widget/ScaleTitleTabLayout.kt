package com.victor.lib.widget

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ScaleTitleTabLayout
 * Author: Victor
 * Date: 2025/7/22 9:54
 * Description: 
 * -----------------------------------------------------------------
 */
class ScaleTitleTabLayout : TabLayout,OnTabSelectedListener {

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        addOnTabSelectedListener(this)
    }

    override fun onTabSelected(tab: Tab?) {
        tabSelector(true,tab?.position ?: 0)
    }

    override fun onTabUnselected(tab: Tab?) {
        tabSelector(false,tab?.position ?: 0)
    }

    override fun onTabReselected(tab: Tab?) {
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun tabSelector(checked: Boolean,position: Int) {

        val tabRoot = (getChildAt(0) as ViewGroup).getChildAt(position) as ViewGroup

        val textView = tabRoot.getChildAt(1) as AppCompatTextView
        textView.paint.isFakeBoldText = checked

        val anim = ObjectAnimator
            .ofFloat(tabRoot, "", if (checked) 1.0f  else 1.1f, if (checked) 1.1f  else 1.0f)
            .setDuration(200)
        anim.start()
        anim.addUpdateListener { animation ->
            val cVal = animation.animatedValue as Float
            tabRoot.alpha = if (checked) (0.5f + (cVal - 1f) * (0.5f / 0.1f)) else 1f - (1f - cVal) * (0.5f / 0.1f)
            tabRoot.scaleX = cVal
            tabRoot.scaleY = cVal
        }
    }

}