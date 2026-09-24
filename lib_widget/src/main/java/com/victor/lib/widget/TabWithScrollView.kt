package com.victor.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.widget.NestedScrollView
import com.google.android.material.tabs.TabLayout

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TabWithScrollView
 * Author: Victor
 * Date: 2022/6/20 20:04
 * Description: 
 * -----------------------------------------------------------------
 */

open class TabWithScrollView : NestedScrollView {
    private val TAG = "TabWithScrollView"

    /**
     * 模块View的集合
     */
    private var mViewList: List<View>? = null

    /**
     * 是否是ScrollView引起的滑动，true-是，false-TabLayout引起的滑动
     */
    private var isManualScroll = false

    /**
     * 记录上一次点击的position，防止多次点击
     */
    private var oldPosition = 0

    /**
     * 需要联动的tabLayout
     */
    private var mTabLayout: TabLayout? = null

    /**
     * ScrollView的滑动回调
     */
    private val onScrollCallback: OnScrollCallback? = null

    /**
     * 距离顶部的偏移量，默认为10px;
     */
    private var mTranslationY = 100

    private var mSelectTabFlag = false


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            Log.i(TAG, "onTouch: ACTION_DOWN")
            isManualScroll = true
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        onScrollCallback?.onScrollCallback(l, t, oldl, oldt)
        if (isManualScroll) {
            if (mViewList == null) {
                return
            }
            for (i in mViewList!!.indices.reversed()) {
                if (t > getViewTop(i)) {
                    setSelectedTab(i)
                    break
                }
            }
        }
    }

    /**
     * 获取View距离顶部的高度(mTranslationY是距离顶部的偏移量)
     *
     * @param position
     * @return
     */
    private fun getViewTop(position: Int): Int {
        if (position >= mViewList!!.size + 1) {
            throw IndexOutOfBoundsException("TabLayout的tab数量和视图View的数量不一致")
        }
        return mViewList!![position].top - mTranslationY
    }

    /**
     * 设置选中的tab标签
     *
     * @param position
     */
    private fun setSelectedTab(position: Int) {
        if (mTabLayout != null && position != oldPosition) {
            Log.i(TAG, "setSelectedTab: $position")
            oldPosition = position
            val newTab = mTabLayout?.getTabAt(position)
            if (newTab != null) {
                mSelectTabFlag = true
                newTab.select()
            }
        }
    }

    /**
     * 设置绑定的tabLayout,并给tabLayout添加OnTabSelectedListener监听
     *
     * @param tabLayout
     */
    fun setupWithTabLayout(tabLayout: TabLayout?) {
        if (tabLayout != null) {
            mTabLayout = tabLayout
            mTabLayout?.addOnTabSelectedListener(mTabSelectedListener)
        }
    }

    fun setAnchorList(anchorList: List<View>?) {
        mViewList = anchorList
    }


    fun setTranslationY(translationY: Int) {
        mTranslationY = translationY
    }

    var mTabSelectedListener: TabLayout.OnTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            oldPosition = tab.position
            isManualScroll = false
            mSelectTabFlag = !mSelectTabFlag
            if (mViewList == null) {
                return
            }
            if (mSelectTabFlag) { // 通过点击Tab触发
                // smoothScrollTo可以平滑的滑动到指定位置，并打断惯性滑动
                smoothScrollTo(0, getViewTop(oldPosition))
            } else { //通过滑动时切换Tab触发
                isManualScroll = true
            }
            mSelectTabFlag = false
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            Log.i(TAG, "onTabUnselected: " + tab.position)
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            Log.i(TAG, "onTabReselected: " + tab.position)
        }
    }

    /**
     * ScrollView的滚动回调
     */
    interface OnScrollCallback {
        fun onScrollCallback(l: Int, t: Int, oldl: Int, oldt: Int)
    }
}