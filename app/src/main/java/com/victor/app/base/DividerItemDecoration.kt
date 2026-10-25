package com.bao.groupitemdecoration.widget

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

class DividerItemDecoration: RecyclerView.ItemDecoration() {
    private val mDividerColor: Int = Color.parseColor("#eaeaea")
    private val mDividerHeight: Int = 1
    private val mMarginLeft: Int = 0
    private val mMarginRight: Int = 0
    private val startPosition: Int = 1

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
        outRect.set(0, 0, 0, mDividerHeight)
    }

    protected open fun getMarginLeft(child: View): Int {
        return mMarginLeft
    }

    protected open fun onDrawDivider(
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