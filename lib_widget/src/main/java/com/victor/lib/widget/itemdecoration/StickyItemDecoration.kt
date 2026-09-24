package com.victor.lib.widget.itemdecoration

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: StickyItemDecoration
 * Author: Victor
 * Date: 2022/4/1 12:15
 * Description: 
 * -----------------------------------------------------------------
 */

class StickyItemDecoration : RecyclerView.ItemDecoration() {

    /**
     * 吸附的itemView
     */
    private var mStickyItemView: View? = null

    /**
     * 吸附itemView 距离顶部
     */
    private var mStickyItemViewMarginTop = 0

    /**
     * 吸附itemView 高度
     */
    private var mStickyItemViewHeight = 0

    /**
     * 通过它获取到需要吸附view的相关信息
     */
    private val mStickyView: StickyView = ExampleStickyView()

    /**
     * 滚动过程中当前的UI是否可以找到吸附的view
     */
    private var mCurrentUIFindStickView = false

    /**
     * adapter
     */
    private var mAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

    /**
     * viewHolder
     */
    private var mViewHolder: RecyclerView.ViewHolder? = null

    /**
     * position list
     */
    private val mStickyPositionList = ArrayList<Int>()

    /**
     * layout manager
     */
    private lateinit var mLayoutManager: LinearLayoutManager

    /**
     * 绑定数据的position
     */
    private var mBindDataPosition = -1

    /**
     * 监听 adapter 数据变更，自动重置缓存
     */
    private val mAdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            resetCache()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            resetCache()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            resetCache()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            resetCache()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            resetCache()
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        drawOverAction(c, parent, state)
    }

    fun drawOverAction(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val itemCount = parent.adapter?.itemCount ?: 0
        if (itemCount == 0) return
        if (parent.layoutManager is LinearLayoutManager) {
            mLayoutManager = parent.layoutManager as LinearLayoutManager
            mCurrentUIFindStickView = false
            clearStickyPositionList()

            var m = 0
            val size = parent.childCount
            while (m < size) {
                val view = parent.getChildAt(m)

                /**
                 * 如果是吸附的view
                 */
                if (mStickyView.isStickyView(view)) {
                    mCurrentUIFindStickView = true
                    getStickyViewHolder(parent)
                    cacheStickyViewPosition(m)

                    if (view.top <= 0) {
                        bindDataForStickyView(mLayoutManager.findFirstVisibleItemPosition(), parent.measuredWidth)
                    } else {
                        if (mStickyPositionList.size > 0) {
                            if (mStickyPositionList.size == 1) {
                                bindDataForStickyView(mStickyPositionList[0], parent.measuredWidth)
                            } else {
                                val currentPosition = getStickyViewPositionOfRecyclerView(m)
                                val indexOfCurrentPosition = mStickyPositionList.lastIndexOf(currentPosition)
                                if (indexOfCurrentPosition >= 1) {
                                    bindDataForStickyView(mStickyPositionList[indexOfCurrentPosition - 1], parent.measuredWidth)
                                }
                            }
                        }
                    }

                    if (view.top in 1..mStickyItemViewHeight) {
                        mStickyItemViewMarginTop = mStickyItemViewHeight - view.top
                    } else {
                        mStickyItemViewMarginTop = 0

                        val nextStickyView = getNextStickyView(parent)
                        if (nextStickyView != null && nextStickyView.top <= mStickyItemViewHeight) {
                            mStickyItemViewMarginTop = mStickyItemViewHeight - nextStickyView.top
                        }
                    }

                    drawStickyItemView(c)
                    break
                }
                m++
            }

            if (!mCurrentUIFindStickView) {
                mStickyItemViewMarginTop = 0
                if (mLayoutManager.findFirstVisibleItemPosition() + parent.childCount == parent.adapter?.itemCount
                    && mStickyPositionList.size > 0
                ) {
                    bindDataForStickyView(mStickyPositionList[mStickyPositionList.size - 1], parent.measuredWidth)
                }
                drawStickyItemView(c)
            }
        }
    }

    /**
     * 清空吸附position集合
     */
    private fun clearStickyPositionList() {
        if (mLayoutManager.findFirstVisibleItemPosition() == 0) {
            mStickyPositionList.clear()
        }
    }

    /**
     * 得到下一个吸附View
     */
    private fun getNextStickyView(parent: RecyclerView): View? {
        var num = 0
        var nextStickyView: View? = null

        for (m in 0 until parent.childCount) {
            val view = parent.getChildAt(m)
            if (mStickyView.isStickyView(view)) {
                nextStickyView = view
                num++
            }
            if (num == 2) break
        }

        return if (num >= 2) nextStickyView else null
    }

    /**
     * 给StickyView绑定数据
     */
    private fun bindDataForStickyView(position: Int, width: Int) {
        if (mBindDataPosition == position || mViewHolder == null) return

        mBindDataPosition = position
        mAdapter?.onBindViewHolder(mViewHolder!!, mBindDataPosition)
        measureLayoutStickyItemView(width);
        mStickyItemViewHeight = mViewHolder!!.itemView.bottom - mViewHolder!!.itemView.top
    }

    /**
     * 缓存吸附的view position
     */
    private fun cacheStickyViewPosition(m: Int) {
        val position = getStickyViewPositionOfRecyclerView(m)
        if (!mStickyPositionList.contains(position)) {
            mStickyPositionList.add(position)
        }
    }

    /**
     * 得到吸附view在RecyclerView中的position
     */
    private fun getStickyViewPositionOfRecyclerView(m: Int): Int {
        return mLayoutManager.findFirstVisibleItemPosition() + m
    }

    /**
     * 得到吸附viewHolder
     */
    private fun getStickyViewHolder(recyclerView: RecyclerView) {
        if (mAdapter != null) return
        mAdapter = recyclerView.adapter
        mAdapter?.registerAdapterDataObserver(mAdapterDataObserver)
        mViewHolder = mAdapter?.onCreateViewHolder(recyclerView, mStickyView.getStickViewType())
        mStickyItemView = mViewHolder?.itemView
    }

    /**
     * 计算布局吸附的itemView
     */
    private fun measureLayoutStickyItemView(parentWidth: Int) {
        if (mStickyItemView == null || mStickyItemView?.isLayoutRequested == false) return

        val widthSpec = View.MeasureSpec.makeMeasureSpec(parentWidth, View.MeasureSpec.EXACTLY)
        var heightSpec = 0

        val layoutParams = mStickyItemView?.layoutParams
        if (layoutParams != null && layoutParams.height > 0) {
            heightSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY)
        } else {
            heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        }

        mStickyItemView?.measure(widthSpec, heightSpec);
        mStickyItemView?.layout(0, 0, mStickyItemView?.measuredWidth ?: 0, mStickyItemView?.measuredHeight ?: 0)
    }

    /**
     * 绘制吸附的itemView
     */
    private fun drawStickyItemView(canvas: Canvas) {

        if (mStickyItemView == null) return

        val saveCount = canvas.save()
        canvas.translate(0f, -mStickyItemViewMarginTop.toFloat())
        mStickyItemView?.draw(canvas)
        canvas.restoreToCount(saveCount)
    }

    fun resetCache() {
        mBindDataPosition = -1          // 清除 position 去重标记，强制重新 bindData
        mStickyItemViewMarginTop = 0    // 重置偏移量
        mStickyItemViewHeight = 0       // 重置高度
        mCurrentUIFindStickView = false // 重置查找标记
    }
}