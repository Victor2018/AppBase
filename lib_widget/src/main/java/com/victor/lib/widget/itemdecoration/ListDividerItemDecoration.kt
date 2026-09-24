package com.victor.lib.widget.itemdecoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DividerItemDecoration
 * Author: Victor
 * Date: 2026/9/22 09:48
 * Description: 
 * -----------------------------------------------------------------
 */

class ListDividerItemDecoration: RecyclerView.ItemDecoration() {
    var mDividerColor: Int = Color.parseColor("#F9F9FB")
    var mDividerHeight: Int = 1
    var mMarginLeft: Int = 0
    var mMarginRight: Int = 0
    var startPosition: Int = 1

    // 是否绘制最后一行
    private var mIsDrawLastLine = false

    private val mPaint: Paint = Paint().apply {
        isAntiAlias = true
        color = mDividerColor
    }

    fun drawLastLine(isDrawLastLine: Boolean) {
        mIsDrawLastLine = isDrawLastLine
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount - if (mIsDrawLastLine) 0 else 1

        for (i in startPosition until childCount) {
            val child = parent.getChildAt(i)
            val paddingLeft = child.paddingLeft
            val left = child.left + paddingLeft + getMarginLeft(child)
            val right = child.right - mMarginRight
            val top = child.bottom
            val bottom = top + mDividerHeight

            onDrawDivider(c, left, top, right, bottom, child)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) {
            outRect.set(0, 0, 0, 0)
            return
        }
        val itemCount = state.itemCount
        // 当前 item 下方是否会绘制分割线：
        // 1. position >= startPosition（onDraw 从 startPosition 开始）
        // 2. 不是最后一个 item（最后一条默认不画）
        // 3. 如果是最后一个 item，则要求 mIsDrawLastLine 为 true
        val isLastItem = position == itemCount - 1
        val shouldDraw = position >= startPosition && (!isLastItem || mIsDrawLastLine)

        if (shouldDraw) {
            outRect.set(0, 0, 0, mDividerHeight)
        } else {
            outRect.set(0, 0, 0, 0)
        }
    }

    private fun getMarginLeft(child: View): Int {
        return mMarginLeft
    }

    private fun onDrawDivider(
        c: Canvas,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        child: View
    ) {
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
    }
}