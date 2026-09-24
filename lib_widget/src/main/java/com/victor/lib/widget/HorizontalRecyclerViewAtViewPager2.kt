package com.victor.lib.widget

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RecyclerViewAtViewPager
 * Author: Victor
 * Date: 2026/2/2 8:51
 * Description: 
 * -----------------------------------------------------------------
 */

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NoHorizontalNestedScrollView
 * Author: Victor
 * Date: 2026/1/30 17:21
 * Description: 处理RecyclerView水平滑动在viewpage2中的滑动冲突
 * -----------------------------------------------------------------
 */

class HorizontalRecyclerViewAtViewPager2 : LMRecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var startX = 0
    private var startY = 0

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x.toInt()
                startY = ev.y.toInt()
                parent.requestDisallowInterceptTouchEvent(true) // 告诉viewgroup不要去拦截我
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = ev.x - startX
                val deltaY = ev.y - startY

                // 判断是否为水平滑动
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    // 检查是否到达边界
                    if (!canScrollHorizontally(deltaX.toInt())) {
                        // 不能继续滑动了，传递给父View（ViewPager2）
                        parent.requestDisallowInterceptTouchEvent(false)
                        return false
                    }
                    parent.requestDisallowInterceptTouchEvent(true)
                } else {
                    // 垂直滑动，传递给父View
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    // 重写 canScrollHorizontally 方法
    override fun canScrollHorizontally(direction: Int): Boolean {
        if (layoutManager == null || adapter == null) {
            return false
        }

        val layoutManager = layoutManager as? LinearLayoutManager ?: return false
        val totalItems = adapter?.itemCount ?: 0

        if (totalItems == 0) return false

        // 判断是否可以继续滚动
        return when {
            direction < 0 -> {
                // 向左滚动（查看右侧内容）
                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                val lastItem = findViewByPosition(lastVisiblePosition)

                // 检查最后一个item是否完全可见
                val canScroll = lastVisiblePosition < totalItems - 1 ||
                        (lastItem != null && lastItem.right > width - paddingRight)
                Log.d("ScrollCheck", "向左滚动: $canScroll, lastVisible: $lastVisiblePosition, total: $totalItems")
                canScroll
            }
            direction > 0 -> {
                // 向右滚动（查看左侧内容）
                val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                val firstItem = findViewByPosition(firstVisiblePosition)

                // 检查第一个item是否完全可见
                val canScroll = firstVisiblePosition > 0 ||
                        (firstItem != null && firstItem.left < paddingLeft)
                Log.d("ScrollCheck", "向右滚动: $canScroll, firstVisible: $firstVisiblePosition")
                canScroll
            }
            else -> false
        }
    }

    // 辅助方法：通过位置查找View
    private fun findViewByPosition(position: Int): View? {
        return layoutManager?.findViewByPosition(position)
    }
}