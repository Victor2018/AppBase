package com.victor.lib.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.victor.lib.widget.util.DensityUtil

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IndicatorView
 * Author: Victor
 * Date: 2022/6/7 10:22
 * Description: 
 * -----------------------------------------------------------------
 */

class IndicatorView : View {
    /**
     * 可用状态列表
     */
    private val STATE_LIST = arrayOf(
        intArrayOf(
            -android.R.attr.state_selected,
            -android.R.attr.state_pressed,
            -android.R.attr.state_checked,
            -android.R.attr.state_enabled
        ), intArrayOf(
            android.R.attr.state_selected, android.R.attr.state_pressed,
            android.R.attr.state_checked, android.R.attr.state_enabled
        )
    )
    private val DEFAULT_PADDING_TOP_BO = 10f
    private var defaultWidthHeight = 0
    private var mIndicatorTransformer: IndicatorTransformer? = null
    private var mPositionOffset = 0f
    private var isAnimation = false

    /**
     * 画笔设置抗锯齿
     */
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 每个绘图单元的个数
     */
    private var mCount = 0

    /**
     * 被选中绘图单元的索引
     */
    private var mSelect = 0

    /**
     * 被选中绘图单元的放缩比例
     */
    private var mSelectScale = 0f

    /**
     * 绘图单元的颜色
     */
    private var mColor = 0

    /**
     * 绘图单元的 Drawable
     */
    private var mUnitDrawable: StateListDrawable? = null

    /**
     * 绘图单元的Rect
     */
    private var mBounds: Rect? = null

    /**
     * 绘图单元的半径
     */
    private var mRadius = 0f
    private var mUnitWidth = 0f
    private var mUnitHeight = 0f
    private var mUnitPadding = 0f
    private var mBoPadding = DEFAULT_PADDING_TOP_BO

    /**
     * 画笔宽度
     */
    private var mStrokeWidth = 0f

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(context,attrs)
    }

    fun initAttrs (context: Context,attrs: AttributeSet?) {
        defaultWidthHeight = DensityUtil.dip2px(context, 10f)

        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.IndicatorView)
        mCount = ta.getInt(R.styleable.IndicatorView_indicator_count, 3)
        mColor = ta.getColor(R.styleable.IndicatorView_indicator_color, Color.RED)
        mRadius = ta.getDimension(R.styleable.IndicatorView_indicator_radius, -1f)
        mUnitWidth = ta.getDimension(R.styleable.IndicatorView_indicator_unit_width, -1f)
        mUnitHeight = ta.getDimension(R.styleable.IndicatorView_indicator_unit_height, -1f)
        mUnitPadding = ta.getDimension(
            R.styleable.IndicatorView_indicator_padding,
            DensityUtil.dip2px(context, 5f).toFloat()
        )
        mBoPadding = ta.getDimension(
            R.styleable.IndicatorView_indicator_bo_padding,
            DensityUtil.dip2px(context, DEFAULT_PADDING_TOP_BO).toFloat()
        )
        val tempDrawable = ta.getDrawable(R.styleable.IndicatorView_indicator_drawable)
        mSelectScale = ta.getFloat(R.styleable.IndicatorView_indicator_select_scale, 1.0f)
        mSelect = ta.getInt(R.styleable.IndicatorView_indicator_select, 0)
        ta.recycle()

        if (tempDrawable is StateListDrawable) {
            mUnitDrawable = tempDrawable
        }

        setSelect(mSelect)
        mPaint.color = mColor
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        handleUnitSize()

        // 得到模式和对应值
        val withMode = MeasureSpec.getMode(widthMeasureSpec)
        val withSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        // 设置默认宽高
        val width =
            ((mBounds!!.width() + mUnitPadding * 2) * mCount + paddingLeft + paddingRight).toInt()
        val height: Int =
            mBounds!!.height() + mBoPadding.toInt() * 2 + paddingTop + paddingBottom
        val w: Int
        val h: Int
        w = if (withMode == MeasureSpec.AT_MOST || withMode == MeasureSpec.UNSPECIFIED) {
            width
        } else {
            withSize
        }
        h = if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            height
        } else {
            heightSize
        }
        setMeasuredDimension(w, h)
    }

    /**
     * 处理尺寸
     */
    private fun handleUnitSize() {
        var width: Int
        var height: Int
        height = mUnitHeight.toInt()
        width = mUnitWidth.toInt()
        if (height < 0 && width < 0) {
            height = (mRadius * 2).toInt()
            width = (mRadius * 2).toInt()
        }
        if (mUnitDrawable != null && height < 0 && width < 0) {
            height = mUnitDrawable!!.intrinsicHeight
            width = mUnitDrawable!!.intrinsicWidth
        }
        //默认宽高
        if (height < 0 && width < 0) {
            height = defaultWidthHeight
            width = defaultWidthHeight
        }
        mBounds = Rect(0, 0, width, height)
        if (mUnitDrawable != null) {
            mUnitDrawable!!.setBounds(mBounds!!)
        }
        // 设置画笔
        mStrokeWidth = (Math.min(mBounds!!.width(), mBounds!!.height()) / 20).toFloat()
        mPaint.strokeWidth = mStrokeWidth
    }


    override fun onDraw(canvas: Canvas) {
        canvas.save()
        //处理view的padding
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())
        for (i in 0 until mCount) {
            //此处忽略画笔的宽度 移动绘制单元格 并居中
            canvas.save()
            canvas.translate((mUnitPadding * 2 + mBounds!!.width()) * i + mUnitPadding, mBoPadding)
            if (mUnitDrawable != null) {
                drawDrawableUnit(canvas, !isAnimation && mSelect == i)
            } else {
                drawDefaultUnit(canvas, !isAnimation && mSelect == i)
            }
            canvas.restore()
        }
        drawAnimationUnit(canvas)
        canvas.restore()
    }

    /**
     * 处理动画
     *
     * @param canvas Canvas
     */
    private fun drawAnimationUnit(canvas: Canvas) {
        if (!isAnimation) return
        if (mSelect >= 0 && mSelect < mCount) {
            canvas.save()
            val unitWidth = mUnitPadding * 2 + mBounds!!.width()
            canvas.translate(unitWidth * mSelect + mUnitPadding, mBoPadding)

            //处理动画
            if (mIndicatorTransformer != null) {
                mIndicatorTransformer?.transformPage(this, canvas, mSelect, mPositionOffset)
            }
            if (mUnitDrawable != null) {
                drawDrawableUnit(canvas, true)
            } else {
                drawDefaultUnit(canvas, true)
            }
            canvas.restore()
        }
    }

    /**
     * 此时居中
     * 用来绘制特定Drawable指示器单元显示
     */
    private fun drawDrawableUnit(canvas: Canvas, isSelect: Boolean) {
        if (isSelect) {
            mUnitDrawable?.state = STATE_LIST.get(1)
            canvas.scale(
                mSelectScale,
                mSelectScale,
                mBounds?.centerX()?.toFloat() ?: 0f,
                mBounds?.centerY()?.toFloat() ?: 0f
            )
        } else {
            mUnitDrawable?.state = STATE_LIST.get(0)
        }
        mUnitDrawable?.draw(canvas)
    }

    /**
     * 此时居中
     * 用来绘制默认指示器单元显示
     */
    private fun drawDefaultUnit(canvas: Canvas, isSelect: Boolean) {
        val drawRadius = (Math.min(mBounds!!.width(), mBounds!!.height()) - mStrokeWidth) / 2
        if (isSelect) {
            mPaint.style = Paint.Style.FILL_AND_STROKE
            canvas.drawCircle(
                mBounds!!.centerX().toFloat(),
                mBounds!!.centerY().toFloat(),
                drawRadius * mSelectScale,
                mPaint
            )
        } else {
            mPaint.style = Paint.Style.STROKE
            canvas.drawCircle(
                mBounds!!.centerX().toFloat(),
                mBounds!!.centerY().toFloat(),
                drawRadius,
                mPaint
            )
        }
    }


    fun setupWithViewPager(viewPager: ViewPager?) {
        viewPager?.removeOnPageChangeListener(IndicatorViewOnPageChangeListener)
        viewPager?.addOnPageChangeListener(IndicatorViewOnPageChangeListener)
    }

    fun setIndicatorTransformer(indicatorTransformer: IndicatorTransformer) {
        isAnimation = indicatorTransformer != null
        mIndicatorTransformer = indicatorTransformer
    }

    fun getColor(): Int {
        return mColor
    }

    fun setColor(color: Int) {
        mColor = color
        mPaint.color = mColor
        invalidate()
    }

    fun getCount(): Int {
        return mCount
    }

    fun setCount(count: Int) {
        if (count < 0) {
            mCount = 0
        }
        mCount = count
        invalidate()
    }

    fun getSelect(): Int {
        return mSelect
    }

    /**
     * 设置选中的unit的index
     *
     * @param select 选中的位置
     */
    fun setSelect(select: Int) {
        mSelect = select
        invalidate()
    }

    fun getUnitBounds(): Rect {
        return mBounds!!
    }

    fun getUnitPadding(): Float {
        return mUnitPadding
    }

    fun getUnitDrawable(): StateListDrawable? {
        return mUnitDrawable
    }

    fun setUnitDrawable(unitDrawable: Drawable?) {
        if (unitDrawable is StateListDrawable) {
            mUnitDrawable = unitDrawable
            mUnitDrawable!!.bounds = mBounds!!
            invalidate()
        }
    }

    fun setRadius(radius: Float) {
        if (mRadius < 0) return
        mRadius = radius
        // 设置画笔
        mStrokeWidth = mRadius / 10
        mPaint.strokeWidth = mStrokeWidth
        invalidate()
    }

    fun getSelectScale(): Float {
        return mSelectScale
    }

    /**
     * @param selectScale
     */
    fun setSelectScale(selectScale: Float) {
        mSelectScale = selectScale
        invalidate()
    }

    /**
     * 将当前指示器的位置向前移动
     */
    operator fun next() {
        mSelect = (mSelect + 1) % mCount
        invalidate()
    }

    /**
     * 将当前指示器的位置向后移动
     */
    fun previous() {
        mSelect = (mSelect - 1 + mCount) % mCount
        invalidate()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val parcelable = super.onSaveInstanceState()
        val ss = SavedState(parcelable)
        ss.count = mCount
        ss.select = mSelect
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        setCount(ss.count)
        setSelect(ss.select)
    }


    /**
     * 动画接口
     */
    interface IndicatorTransformer {
        fun transformPage(
            page: IndicatorView?,
            canvas: Canvas?,
            position: Int,
            positionOffset: Float
        )
    }

    /**
     * 平移动画
     */
    class TranslationIndicatorTransformer : IndicatorTransformer {
        override fun transformPage(
            page: IndicatorView?,
            canvas: Canvas?,
            position: Int,
            positionOffset: Float
        ) {
            val unitWidth = page!!.getUnitPadding() * 2 + page.getUnitBounds().width()
            var scrollWidth = unitWidth
            if (position + 1 >= 0 && position + 1 < page.getCount()) {
                scrollWidth += unitWidth
            }
            scrollWidth *= positionOffset * 0.5f
            canvas?.translate(scrollWidth, 0f)
        }
    }

    /**
     * 缩放动画
     */
    class ScaleIndicatorTransformer: IndicatorTransformer {
        override fun transformPage(
            page: IndicatorView?,
            canvas: Canvas?,
            position: Int,
            positionOffset: Float
        ) {
            val bounds = page!!.getUnitBounds()
            var pos = Math.abs(positionOffset - 0.5f) * 2
            pos = if (pos < 0.4f) 0.4f else pos
            canvas?.scale(pos, pos, bounds.centerX().toFloat(), bounds.centerY().toFloat())
        }

    }

    /**
     * User interface state that is stored by IndicatorView for implementing
     * [View.onSaveInstanceState].
     */
    class SavedState : BaseSavedState {
        var count = 0
        var select = 0

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(source: Parcel) : super(source) {
            count = source.readInt()
            select = source.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(count)
            out.writeInt(select)
        }

        companion object {
            @SuppressLint("ParcelCreator")
            val CREATOR: Creator<SavedState> = object : Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    private val IndicatorViewOnPageChangeListener = object :
        ViewPager.SimpleOnPageChangeListener() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            if (isAnimation) {
                mSelect = position
                mPositionOffset = positionOffset
            }
            invalidate()
        }

        override fun onPageSelected(position: Int) {
            if (!isAnimation) {
                mSelect = position
            }
        }
    }

}