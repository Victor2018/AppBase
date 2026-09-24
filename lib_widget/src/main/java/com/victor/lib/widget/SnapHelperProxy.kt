package com.victor.lib.widget

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SnapHelperProxy
 * Author: Victor
 * Date: 2022/6/6 19:14
 * Description: 
 * -----------------------------------------------------------------
 */

class SnapHelperProxy(private var snapHelper: SnapHelper) : SnapHelper() {

    var onSnapListener: OnSnapListener? = null
    private var mRecyclerView: RecyclerView? = null

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
        val gravityScrollerField = SnapHelper::class.java.getDeclaredField("mGravityScroller")
        gravityScrollerField.isAccessible = true
        gravityScrollerField.set(snapHelper, gravityScrollerField.get(this))

        val field = SnapHelper::class.java.getDeclaredField("mRecyclerView")
        field.isAccessible = true
        field.set(snapHelper, recyclerView)
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        val result = snapHelper.calculateDistanceToFinalSnap(layoutManager, targetView)
        onSnapListener?.onFinalSnap(targetView, layoutManager.getPosition(targetView))
        return result
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        return snapHelper.findSnapView(layoutManager)
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager?, velocityX: Int,
        velocityY: Int
    ): Int {
        val position = snapHelper.findTargetSnapPosition(layoutManager, velocityX, velocityY)
        if (position >= 0) {
            onSnapListener?.onSnapFromFling(position)
        }
        return position
    }

    fun getSnapPosition(): Int {
        return mRecyclerView?.layoutManager?.run {
            val view = snapHelper.findSnapView(this) ?: return 0
            this.getPosition(view)
        } ?: 0
    }

    interface OnSnapListener {
        fun onSnapFromFling(position: Int)
        fun onFinalSnap(view: View, position: Int)
    }
}