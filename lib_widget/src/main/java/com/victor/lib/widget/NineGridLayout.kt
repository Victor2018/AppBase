package com.victor.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NineGridLayout
 * Author: Victor
 * Date: 2026/3/24 10:50
 * Description: 
 * -----------------------------------------------------------------
 */

class NineGridLayout: ViewGroup {
    private val MAX_CHILDREN_COUNT = 9 //最大的子View数量

    private var horizontalSpacing = 20 //每一个Item的左右间距

    private var verticalSpacing = 20 //每一个Item的上下间距

    private var fourGridMode = true //是否支持四宫格模式

    private var singleMode = true //是否支持单布局模式

    private val singleModeScale = true //是否支持单布局模式按比例缩放

    private var singleWidth = 0
    private var singleHeight = 0

    private var itemWidth = 0
    private var itemHeight = 0

    private var mAdapter: Adapter? = null

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.NineGridLayout)
        val spacing = a.getDimensionPixelSize(R.styleable.NineGridLayout_spacing, 0)
        horizontalSpacing =
            a.getDimensionPixelSize(R.styleable.NineGridLayout_horizontal_spacing, spacing)
        verticalSpacing =
            a.getDimensionPixelSize(R.styleable.NineGridLayout_vertical_spacing, spacing)
        singleMode = a.getBoolean(R.styleable.NineGridLayout_single_mode, true)
        fourGridMode = a.getBoolean(R.styleable.NineGridLayout_four_gird_mode, true)
        singleWidth = a.getDimensionPixelSize(R.styleable.NineGridLayout_single_mode_width, 0)
        singleHeight = a.getDimensionPixelSize(R.styleable.NineGridLayout_single_mode_height, 0)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        var notGoneChildCount = getNotGoneChildCount()
        if (mAdapter == null || mAdapter?.itemCount == 0 || notGoneChildCount == 0) {
            setMeasuredDimension(widthSize, 0)
            return
        }
        if (notGoneChildCount == 1 && singleMode) {
            itemWidth = if (singleWidth > 0) singleWidth else widthSize
            itemHeight = if (singleHeight > 0) singleHeight else widthSize
            if (itemWidth > widthSize && singleModeScale) {
                itemWidth = widthSize //单张图片先定宽度。
                itemHeight = (widthSize * 1f / singleWidth * singleHeight).toInt() //根据宽度计算高度
            }
        } else {
            //除了单布局模式，其他的都是指定的固定宽高
            itemWidth = (widthSize - horizontalSpacing * 2) / 3
            itemHeight = itemWidth
        }

        //measureChildren内部调用measureChild，这里我们就可以指定宽高
        measureChildren(
            MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY)
        )
        if (heightMode == MeasureSpec.EXACTLY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            notGoneChildCount = Math.min(
                notGoneChildCount,
                MAX_CHILDREN_COUNT
            )
            val heightSize = ((notGoneChildCount - 1) / 3 + 1) *
                    (itemHeight + verticalSpacing) - verticalSpacing + paddingTop + paddingBottom
            setMeasuredDimension(widthSize, heightSize)
        }
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childCount = childCount
        val notGoneChildCount = getNotGoneChildCount()
        if (mAdapter == null || mAdapter?.itemCount == 0 || notGoneChildCount == 0) {
            return
        }
        var position = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }
            var row = position / 3 //当前子View是第几行（索引）
            var column = position % 3 //当前子View是第几列（索引）
            if (notGoneChildCount == 4 && fourGridMode) {
                row = position / 2
                column = position % 2
            }

            //当前需要绘制的光标的X与Y值
            val x = column * itemWidth + paddingLeft + horizontalSpacing * column
            val y = row * itemHeight + paddingTop + verticalSpacing * row
            child.layout(x, y, x + itemWidth, y + itemHeight)

            //最多只摆放9个
            position++
            if (position == MAX_CHILDREN_COUNT) {
                break
            }
        }
        performBind()
    }

    /**
     * 布局完成之后绑定对应的数据到对应的ItemView
     */
    private fun performBind() {
        if (mAdapter == null || mAdapter?.itemCount == 0) {
            return
        }
        post {
            for (i in 0 until getNotGoneChildCount()) {
                val itemType = mAdapter?.getItemViewType(i) ?: 0
                val view = getChildAt(i)
                mAdapter?.onBindItemView(view, itemType, i)
            }
        }
    }

    fun setAdapter(adapter: Adapter) {
        mAdapter = adapter
        inflateAllViews()
    }

    private fun inflateAllViews() {
        removeAllViewsInLayout()
        if (mAdapter == null || mAdapter?.itemCount == 0) {
            return
        }
        val displayCount =
            Math.min(mAdapter?.itemCount ?: 0, MAX_CHILDREN_COUNT)

        //单布局处理
        if (singleMode && displayCount == 1) {
            val view = mAdapter?.onCreateItemView(context, this, -1)
            addView(view)
            requestLayout()
            return
        }

        //多布局处理
        for (i in 0 until displayCount) {
            val itemType = mAdapter?.getItemViewType(i) ?: 0
            val view = mAdapter?.onCreateItemView(context, this, itemType)
            view?.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            addView(view)
        }
        requestLayout()
    }

    //获取没有隐藏的子布局
    private fun getNotGoneChildCount(): Int {
        val childCount = childCount
        var notGoneCount = 0
        for (i in 0 until childCount) {
            if (getChildAt(i).visibility != GONE) {
                notGoneCount++
            }
        }
        return notGoneCount
    }

    /**
     * 设置显示的数量
     */
    fun setDisplayCount(count: Int) {
        val childCount = childCount
        for (i in 0 until childCount) {
            getChildAt(i).visibility = if (i < count) VISIBLE else GONE
        }
    }

    /**
     * 设置单独布局的宽和高
     */
    fun setSingleModeSize(w: Int, h: Int) {
        if (w != 0 && h != 0) {
            singleMode = true
            singleWidth = w
            singleHeight = h
        }
    }

    /**
     * 单独设置是否支持四宫格模式
     */
    fun setFourGridMode(enable: Boolean) {
        fourGridMode = enable
    }

    abstract class Adapter {
        //返回总共子View的数量
        abstract val itemCount: Int

        //根据索引创建不同的布局类型，如果都是一样的布局则不需要重写
        fun getItemViewType(position: Int): Int {
            return 0
        }

        //简单点，根据类型创建对应的View布局
        abstract fun onCreateItemView(context: Context?, parent: ViewGroup?, itemType: Int): View

        //可以根据类型或索引绑定数据
        abstract fun onBindItemView(itemView: View?, itemType: Int, position: Int)
    }
}