package com.victor.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.victor.lib.widget.util.DensityUtil


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PageIndicatorView
 * Author: Victor
 * Date: 2022/6/7 9:58
 * Description: 
 * -----------------------------------------------------------------
 */

class PageIndicatorView : LinearLayout {

    private var mContext: Context? = null
    private var margins = 12 // 指示器间距（dp）
    private var indicatorViews: ArrayList<ImageView>? = null // 存放指示器

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int)
            : super(context,attrs,defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        mContext = context
        gravity = Gravity.CENTER
        orientation = HORIZONTAL
        margins = DensityUtil.dip2px(context, margins.toFloat())
    }

    /**
     * 初始化指示器，默认选中第一页
     *
     * @param count 指示器数量，即页数
     */
    fun initIndicator(count: Int) {
        if (indicatorViews == null) {
            indicatorViews = ArrayList()
        } else {
            indicatorViews?.clear()
            removeAllViews()
        }
        val params =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(margins, 0, 0, 0)
        for (i in 0 until count) {
            var imageView = ImageView(mContext)
            imageView.setImageResource(R.mipmap.ic_rv_indicator_normal)
            addView(imageView, params)
            indicatorViews?.add(imageView)
        }
        var count = indicatorViews?.size ?: 0
        if (count > 0) {
            indicatorViews?.get(0)?.setImageResource(R.mipmap.ic_rv_indicator_focus)
        }
    }

    /**
     * 设置选中页
     *
     * @param selected 页下标，从0开始
     */
    fun setSelectedPage(selected: Int) {
        for (i in indicatorViews!!.indices) {
            if (i == selected) {
                indicatorViews!![i].setImageResource(R.mipmap.ic_rv_indicator_focus)
            } else {
                indicatorViews!![i].setImageResource(R.mipmap.ic_rv_indicator_normal)
            }
        }
    }

}