package com.victor.lib.widget.pagerlayoutmanager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.CallSuper
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.annotation.RestrictTo
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PagerGridLayoutManager
 * Author: Victor
 * Date: 2022/10/9 12:15
 * Description: 
 * -----------------------------------------------------------------
 */

class PagerGridLayoutManager(rows: Int,columns: Int,orientation: Int,reverseLayout: Boolean)
    : LayoutManager(), SmoothScroller.ScrollVectorProvider {

    private val TAG = "PagerGridLayoutManager"

    companion object {

        /**
         * 水平滑动
         */
        const val HORIZONTAL = RecyclerView.HORIZONTAL

        /**
         * 垂直滑动
         */
        const val VERTICAL = RecyclerView.VERTICAL

        /**
         * @see .mCurrentPagerIndex
         */
        const val NO_ITEM = -1
        const val NO_PAGER_COUNT = 0
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    @IntDef(HORIZONTAL, VERTICAL)
    @Retention(
        RetentionPolicy.SOURCE
    )
    annotation class Orientation

    private var mPagerGridSnapHelper: PagerGridSnapHelper? = null

    /**
     * 当前滑动方向
     */
    @Orientation
    private var mOrientation = HORIZONTAL

    /**
     * 行数
     */
    private var mRows = 0

    /**
     * 列数
     */
    private var mColumns = 0

    /**
     * 一页的数量 [.mRows] * [.mColumns]
     */
    private var mOnePageSize = 0

    /**
     * 总页数
     */
    private var mPagerCount = NO_PAGER_COUNT

    /**
     * 当前页码下标
     * 从0开始
     */
    private var mCurrentPagerIndex = NO_ITEM

    /**
     * item的宽度
     */
    private var mItemWidth = 0

    /**
     * item的高度
     */
    private var mItemHeight = 0

    /**
     * 一个ItemView的所有ItemDecoration占用的宽度(px)
     */
    private var mItemWidthUsed = 0

    /**
     * 一个ItemView的所有ItemDecoration占用的高度(px)
     */
    private var mItemHeightUsed = 0

    /**
     * 用于保存一些状态
     */
    protected var mLayoutState: LayoutState? = null

    protected var mLayoutChunkResult: LayoutChunkResult? = null

    /**
     * 用于计算锚点坐标
     * [.mShouldReverseLayout] 为false：左上角第一个view的位置
     * [.mShouldReverseLayout] 为true：右上角第一个view的位置
     */
    private val mStartSnapRect = Rect()

    /**
     * 用于计算锚点坐标
     * [.mShouldReverseLayout] 为false：右下角最后一个view的位置
     * [.mShouldReverseLayout] 为true：左上角最后一个view的位置
     */
    private val mEndSnapRect = Rect()

    private var mRecyclerView: RecyclerView? = null

    /**
     * 定义是否应从头到尾计算布局
     *
     * @see .mShouldReverseLayout
     */
    private var mReverseLayout = false

    /**
     * 这保留了 PagerGridLayoutManager 应该如何开始布局视图的最终值。
     * 它是通过检查 [.getReverseLayout] 和 View 的布局方向来计算的。
     */
    protected var mShouldReverseLayout = false

    private var mPagerChangedListener: PagerChangedListener? = null

    /**
     * 计算多出来的宽度，因为在均分的时候，存在除不尽的情况，要减去多出来的这部分大小，一般也就为几px
     * 不减去的话，会导致翻页计算不触发
     *
     * @see .onMeasure
     */
    private var diffWidth = 0

    /**
     * 计算多出来的高度，因为在均分的时候，存在除不尽的情况，要减去多出来的这部分大小，一般也就为几px
     * 不减去的话，会导致翻页计算不触发
     *
     * @see .onMeasure
     */
    private var diffHeight = 0

    /**
     * 是否启用处理滑动冲突滑动冲突，默认开启
     * 只会在[RecyclerView] 在可滑动布局[.isInScrollingContainer]中起作用
     */
    private var isHandlingSlidingConflictsEnabled = true
    private var mMillisecondPreInch: Float = PagerGridSmoothScroller.MILLISECONDS_PER_INCH
    private var mMaxScrollOnFlingDuration: Int =
        PagerGridSmoothScroller.MAX_SCROLL_ON_FLING_DURATION

    private val onChildAttachStateChangeListener: OnChildAttachStateChangeListener =
        object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                val layoutParams = view.layoutParams as LayoutParams
                //判断ItemLayout的宽高是否是match_parent
//                check(
//                    !(layoutParams.width != ViewGroup.LayoutParams.MATCH_PARENT
//                            || layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT)
//                ) { "Item layout  must fill the whole PagerGridLayoutManager (use match_parent)" }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                // nothing
            }
        }

    private var onItemTouchListener: PagerGridItemTouchListener? = null

    init {
        mLayoutState = createLayoutState()
        mLayoutChunkResult = createLayoutChunkResult()
        setRows(rows)
        setColumns(columns)
        setOrientation(orientation)
        setReverseLayout(reverseLayout)
    }

    /**
     * @return 子布局LayoutParams，默认全部填充，子布局会根据[.mRows]和[.mColumns] 均分RecyclerView
     */
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun generateLayoutParams(
        c: Context?,
        attrs: AttributeSet?
    ): RecyclerView.LayoutParams? {
        return LayoutParams(c, attrs)
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): RecyclerView.LayoutParams? {
        return if (lp is RecyclerView.LayoutParams) {
            LayoutParams(lp as RecyclerView.LayoutParams?)
        } else if (lp is MarginLayoutParams) {
            LayoutParams(lp as MarginLayoutParams?)
        } else {
            LayoutParams(lp)
        }
    }

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        return lp is LayoutParams
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        Log.d(TAG, "onAttachedToWindow: ")
        //默认先这么设置
        view.setHasFixedSize(true)
        if (isInScrollingContainer(view)) {
            //在一个可滑动的布局中
            if (isHandlingSlidingConflictsEnabled) {
                onItemTouchListener = PagerGridItemTouchListener(this, view)
                view.addOnItemTouchListener(onItemTouchListener!!)
            } else {
                //不启用的话可以自行解决
                Log.i(TAG, "isHandlingSlidingConflictsEnabled: false.")
            }
        }
        view.addOnChildAttachStateChangeListener(onChildAttachStateChangeListener)
        mPagerGridSnapHelper = PagerGridSnapHelper()
        mPagerGridSnapHelper?.attachToRecyclerView(view)
        mRecyclerView = view
    }

    override fun onMeasure(recycler: Recycler, state: State, widthSpec: Int, heightSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthSpec)
        val heightMode = MeasureSpec.getMode(heightSpec)
        //判断RecyclerView的宽度和高度是不是精确值
        check(!(widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY)) { "RecyclerView's width and height must be exactly" }
        val widthSize = MeasureSpec.getSize(widthSpec)
        val heightSize = MeasureSpec.getSize(heightSpec)
        val realWidth = widthSize - paddingStart - paddingEnd
        val realHeight = heightSize - paddingTop - paddingBottom
        //均分宽
        mItemWidth = if (mColumns > 0) realWidth / mColumns else 0
        //均分高
        mItemHeight = if (mRows > 0) realHeight / mRows else 0

        //重置下宽高，因为在均分的时候，存在除不尽的情况，要减去多出来的这部分大小，一般也就为几px
        //不减去的话，会导致翻页计算不触发
        diffWidth = realWidth - mItemWidth * mColumns
        diffHeight = realHeight - mItemHeight * mRows
        mItemWidthUsed = realWidth - diffWidth - mItemWidth
        mItemHeightUsed = realHeight - diffHeight - mItemHeight
        Log.d(TAG, "onMeasure-originalWidthSize: $widthSize,originalHeightSize: $heightSize,diffWidth: $diffWidth,diffHeight: $diffHeight,mItemWidth: $mItemWidth,mItemHeight: $mItemHeight,mStartSnapRect:$mStartSnapRect,mEndSnapRect:$mEndSnapRect")
        super.onMeasure(recycler, state, widthSpec, heightSpec)
    }

    override fun onLayoutChildren(recycler: Recycler, state: State) {
        Log.d(TAG, "onLayoutChildren: $state")
        val itemCount = itemCount
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler)
            setPagerCount(NO_PAGER_COUNT)
            setCurrentPagerIndex(NO_ITEM)
            return
        }
        if (state.isPreLayout) {
            return
        }

        // resolve layout direction
        resolveShouldLayoutReverse()

        //计算锚点的坐标
        if (mShouldReverseLayout) {
            //右上角第一个view的位置
            mStartSnapRect[width - paddingEnd - mItemWidth, paddingTop, width - paddingEnd] =
                paddingTop + mItemHeight
            //左下角最后一个view的位置
            mEndSnapRect[paddingStart, height - paddingBottom - mItemHeight, paddingStart + mItemWidth] =
                height - paddingBottom
        } else {
            //左上角第一个view的位置
            mStartSnapRect[paddingStart, paddingTop, paddingStart + mItemWidth] =
                paddingTop + mItemHeight
            //右下角最后一个view的位置
            mEndSnapRect[width - paddingEnd - mItemWidth, height - paddingBottom - mItemHeight, width - paddingEnd] =
                height - paddingBottom
        }

        //计算总页数
        var pagerCount = itemCount / mOnePageSize
        if (itemCount % mOnePageSize != 0) {
            ++pagerCount
        }

        //计算需要补充空间
        mLayoutState!!.replenishDelta = 0
        if (pagerCount > 1) {
            //超过一页，计算补充空间距离
            val remain = itemCount % mOnePageSize
            var replenish = 0
            if (remain != 0) {
                var i = remain / mColumns
                val k = remain % mColumns
                replenish = if (mOrientation == HORIZONTAL) {
                    if (i == 0) (mColumns - k) * mItemWidth else 0
                } else {
                    if (k > 0) {
                        ++i
                    }
                    (mRows - i) * mItemHeight
                }
            }
            mLayoutState!!.replenishDelta = replenish
        }
        mLayoutState!!.mRecycle = false
        mLayoutState!!.mLayoutDirection = LayoutState.LAYOUT_END
        mLayoutState!!.mAvailable = getEnd()
        mLayoutState!!.mScrollingOffset = LayoutState.SCROLLING_OFFSET_NaN
        var pagerIndex = mCurrentPagerIndex
        pagerIndex = if (pagerIndex == NO_ITEM) {
            0
        } else {
            //取上次PagerIndex和最大MaxPagerIndex中最小值。
            Math.min(pagerIndex, getMaxPagerIndex())
        }
        val firstView: View?
        firstView = if (!isIdle() && childCount != 0) {
            //滑动中的更新状态
            getChildClosestToStart()
        } else {
            //没有子view或者不在滑动状态
            null
        }

        //计算首个位置的偏移量，主要是为了方便child layout，计算出目标位置的上一个位置的坐标
        val left: Int
        val top: Int
        val right: Int
        val bottom: Int
        if (mShouldReverseLayout) {
            if (firstView == null) {
                //按页且从右上角开始布局
                mLayoutState?.mCurrentPosition = pagerIndex * mOnePageSize
                val calculateClipOffset = calculateClipOffset(true, mLayoutState!!.mCurrentPosition)
                if (mOrientation == RecyclerView.HORIZONTAL) {
                    bottom = height - paddingBottom
                    left = width - paddingEnd + calculateClipOffset
                } else {
                    bottom = paddingTop - calculateClipOffset
                    left = paddingStart
                }
            } else {
                //计算布局偏移量
                val position = getPosition(firstView)
                mLayoutState?.mCurrentPosition = position
                val rect = mLayoutState?.mOffsetRect ?: Rect()
                val calculateClipOffset = calculateClipOffset(true, mLayoutState!!.mCurrentPosition)
                getDecoratedBoundsWithMargins(firstView, rect)
                if (mOrientation == RecyclerView.HORIZONTAL) {
                    if (isNeedMoveToNextSpan(position)) {
                        //为了方便计算
                        bottom = height - paddingBottom
                        left = rect.right + calculateClipOffset
                    } else {
                        bottom = rect.top
                        left = rect.left
                    }
                } else {
                    if (isNeedMoveToNextSpan(position)) {
                        //为了方便计算
                        bottom = rect.top - calculateClipOffset
                        left = paddingStart
                    } else {
                        bottom = rect.bottom
                        left = rect.right
                    }
                }
                //追加额外的滑动空间
                val scrollingOffset: Int
                scrollingOffset = if (mOrientation == HORIZONTAL) {
                    getDecoratedStart(firstView) - getEndAfterPadding()
                } else {
                    getDecoratedStart(firstView)
                }
                mLayoutState?.mAvailable = mLayoutState?.mAvailable?.minus(scrollingOffset) ?: 0
            }
            top = bottom - mItemHeight
            right = left + mItemWidth
        } else {
            if (firstView == null) {
                //按页且从左上角开始布局
                mLayoutState?.mCurrentPosition = pagerIndex * mOnePageSize
                val calculateClipOffset = calculateClipOffset(true, mLayoutState!!.mCurrentPosition)
                if (mOrientation == RecyclerView.HORIZONTAL) {
                    bottom = height - paddingBottom
                    right = paddingStart - calculateClipOffset
                } else {
                    bottom = paddingTop - calculateClipOffset
                    right = width - paddingEnd
                }
            } else {
                //计算布局偏移量
                val position = getPosition(firstView)
                mLayoutState?.mCurrentPosition = position
                val rect = mLayoutState!!.mOffsetRect
                val calculateClipOffset = calculateClipOffset(true, mLayoutState!!.mCurrentPosition)
                getDecoratedBoundsWithMargins(firstView, rect)
                if (mOrientation == RecyclerView.HORIZONTAL) {
                    if (isNeedMoveToNextSpan(position)) {
                        //为了方便计算
                        bottom = height - paddingBottom
                        right = rect.left - calculateClipOffset
                    } else {
                        bottom = rect.top
                        right = rect.right
                    }
                } else {
                    if (isNeedMoveToNextSpan(position)) {
                        //为了方便计算
                        bottom = rect.top - calculateClipOffset
                        right = width - paddingEnd
                    } else {
                        bottom = rect.bottom
                        right = rect.left
                    }
                }
                //追加额外的滑动空间
                val scrollingOffset = getDecoratedStart(firstView)
                mLayoutState?.mAvailable = mLayoutState?.mAvailable?.minus(scrollingOffset) ?: 0
            }
            top = bottom - mItemHeight
            left = right - mItemWidth
        }
        mLayoutState?.setOffsetRect(left, top, right, bottom)
        Log.i(TAG, "onLayoutChildren-pagerCount:" + pagerCount +
                ",mLayoutState.mAvailable: " + mLayoutState?.mAvailable)

        //回收views
        detachAndScrapAttachedViews(recycler)
        //填充views
        fill(recycler, state)
        Log.i(TAG, "onLayoutChildren: childCount:" + childCount +
                ",recycler.scrapList.size:" + recycler.scrapList.size +
                ",mLayoutState.replenishDelta:" + mLayoutState?.replenishDelta)
        if (firstView == null) {
            //移动状态不更新页数和页码
            setPagerCount(pagerCount)
            setCurrentPagerIndex(pagerIndex)
        }
    }

    override fun onLayoutCompleted(state: State?) {}

    override fun findViewByPosition(position: Int): View? {
        val childCount = childCount
        if (childCount == 0) {
            return null
        }
        val firstChild = getPosition(getChildAt(0)!!)
        val viewPosition = position - firstChild
        if (viewPosition >= 0 && viewPosition < childCount) {
            val child = getChildAt(viewPosition)
            if (getPosition(child!!) == position) {
                return child
            }
        }
        return super.findViewByPosition(position)
    }

    override fun computeHorizontalScrollOffset(state: State): Int {
        return computeScrollOffset(state)
    }

    override fun computeVerticalScrollOffset(state: State): Int {
        return computeScrollOffset(state)
    }

    override fun computeHorizontalScrollExtent(state: State): Int {
        return computeScrollExtent(state)
    }

    override fun computeVerticalScrollExtent(state: State): Int {
        return computeScrollExtent(state)
    }

    override fun computeVerticalScrollRange(state: State): Int {
        return computeScrollRange(state)
    }

    override fun computeHorizontalScrollRange(state: State): Int {
        return computeScrollRange(state)
    }

    override fun onSaveInstanceState(): Parcelable? {
        Log.d(TAG, "onSaveInstanceState: ")
        val state = SavedState()
        state.mOrientation = mOrientation
        state.mRows = mRows
        state.mColumns = mColumns
        state.mCurrentPagerIndex = mCurrentPagerIndex
        state.mReverseLayout = mReverseLayout
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            val savedState = state
            mOrientation = savedState.mOrientation
            mRows = savedState.mRows
            mColumns = savedState.mColumns
            calculateOnePageSize()
            setCurrentPagerIndex(savedState.mCurrentPagerIndex)
            mReverseLayout = savedState.mReverseLayout
            requestLayout()
            Log.d(TAG, "onRestoreInstanceState: loaded saved state")
        }
    }

    override fun scrollToPosition(position: Int) {
        assertNotInLayoutOrScroll(null)

        //先找到目标position所在第几页
        val pagerIndex = getPagerIndexByPosition(position)
        scrollToPagerIndex(pagerIndex)
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: State?, position: Int) {
        assertNotInLayoutOrScroll(null)

        //先找到目标position所在第几页
        val pagerIndex = getPagerIndexByPosition(position)
        smoothScrollToPagerIndex(pagerIndex)
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: State): Int {
        return if (mOrientation == VERTICAL) {
            //垂直滑动不处理
            0
        } else scrollBy(dx, recycler, state)
    }

    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: State): Int {
        return if (mOrientation == HORIZONTAL) {
            //水平滑动不处理
            0
        } else scrollBy(dy, recycler, state)
    }

    override fun onScrollStateChanged(state: Int) {
        when (state) {
            SCROLL_STATE_IDLE -> {}
            SCROLL_STATE_DRAGGING -> {}
            SCROLL_STATE_SETTLING -> {}
        }
    }

    override fun canScrollHorizontally(): Boolean {
        return mOrientation == RecyclerView.HORIZONTAL
    }

    override fun canScrollVertically(): Boolean {
        return mOrientation == RecyclerView.VERTICAL
    }

    override fun getWidth(): Int {
        return super.getWidth() - getDiffWidth()
    }

    override fun getHeight(): Int {
        return super.getHeight() - getDiffHeight()
    }

    @CallSuper
    override fun onDetachedFromWindow(view: RecyclerView?, recycler: Recycler?) {
        super.onDetachedFromWindow(view, recycler)
        Log.d(TAG, "onDetachedFromWindow: ")
        if (mRecyclerView != null) {
            if (onItemTouchListener != null) {
                mRecyclerView!!.removeOnItemTouchListener(onItemTouchListener!!)
            }
            mRecyclerView!!.removeOnChildAttachStateChangeListener(onChildAttachStateChangeListener)
            mRecyclerView = null
        }
        mPagerGridSnapHelper!!.attachToRecyclerView(null)
        mPagerGridSnapHelper = null
        //这里不能置为null，因为在ViewPager2嵌套Fragment使用，
        //部分情况下Fragment不回调onDestroyView，但会导致onDetachedFromWindow触发。
        //所以如果想置null，请调用{@link #setPagerChangedListener(null)}
//        mPagerChangedListener = null;
    }

    /**
     * 设置监听回调
     *
     * @param listener
     */
    fun setPagerChangedListener(listener: PagerChangedListener?) {
        mPagerChangedListener = listener
    }

    /**
     * 是否启用处理滑动冲突滑动冲突，默认true
     * 这个方法必须要在[RecyclerView.setLayoutManager] 之前调用，否则无效
     * you must call this method before [RecyclerView.setLayoutManager]
     *
     * @param enabled 是否启用
     * @see .isInScrollingContainer
     * @see .onAttachedToWindow
     */
    fun setHandlingSlidingConflictsEnabled(enabled: Boolean) {
        isHandlingSlidingConflictsEnabled = enabled
    }

    fun isHandlingSlidingConflictsEnabled(): Boolean {
        return isHandlingSlidingConflictsEnabled
    }

    /**
     * 设置滑动每像素需要花费的时间，不可过小，不然可能会出现划过再回退的情况
     * 默认值：[PagerGridSmoothScroller.MILLISECONDS_PER_INCH]
     *
     *
     * set millisecond pre inch. not too small.
     * default value: [PagerGridSmoothScroller.MILLISECONDS_PER_INCH]
     *
     * @param millisecondPreInch 值越大，滚动速率越慢，反之
     * @see PagerGridSmoothScroller.calculateSpeedPerPixel
     */
    fun setMillisecondPreInch(millisecondPreInch: Float) {
        mMillisecondPreInch = Math.max(1f, millisecondPreInch)
    }

    /**
     * @return 滑动每像素需要花费的时间
     * @see PagerGridSmoothScroller.calculateSpeedPerPixel
     */
    fun getMillisecondPreInch(): Float {
        return mMillisecondPreInch
    }

    /**
     * 设置最大滚动时间，如果您想此值无效，请使用[Integer.MAX_VALUE]
     * 默认值：[PagerGridSmoothScroller.MAX_SCROLL_ON_FLING_DURATION]，单位：毫秒
     *
     *
     * set max scroll on fling duration.If you want this value to expire, use [Integer.MAX_VALUE]
     * default value: [PagerGridSmoothScroller.MAX_SCROLL_ON_FLING_DURATION],Unit: ms
     *
     * @param maxScrollOnFlingDuration 值越大，滑动时间越长，滚动速率越慢，反之
     * @see PagerGridSmoothScroller.calculateTimeForScrolling
     */
    fun setMaxScrollOnFlingDuration(@IntRange(from = 1) maxScrollOnFlingDuration: Int) {
        mMaxScrollOnFlingDuration = Math.max(1, maxScrollOnFlingDuration)
    }

    /**
     * @return 最大滚动时间
     * @see PagerGridSmoothScroller.calculateTimeForScrolling
     */
    fun getMaxScrollOnFlingDuration(): Int {
        return mMaxScrollOnFlingDuration
    }

    fun getItemWidth(): Int {
        return mItemWidth
    }

    fun getItemHeight(): Int {
        return mItemHeight
    }

    /**
     * 计算一页的数量
     */
    private fun calculateOnePageSize() {
        mOnePageSize = mRows * mColumns
    }

    /**
     * @return 一页的数量
     */
    @IntRange(from = 1)
    fun getOnePageSize(): Int {
        return mOnePageSize
    }

    fun setColumns(@IntRange(from = 1) columns: Int) {
        assertNotInLayoutOrScroll(null)
        if (mColumns == columns) {
            return
        }
        mColumns = Math.max(columns, 1)
        mPagerCount = NO_PAGER_COUNT
        mCurrentPagerIndex = NO_ITEM
        calculateOnePageSize()
        requestLayout()
    }

    /**
     * @return 列数
     */
    @IntRange(from = 1)
    fun getColumns(): Int {
        return mColumns
    }

    fun setRows(@IntRange(from = 1) rows: Int) {
        assertNotInLayoutOrScroll(null)
        if (mRows == rows) {
            return
        }
        mRows = Math.max(rows, 1)
        mPagerCount = NO_PAGER_COUNT
        mCurrentPagerIndex = NO_ITEM
        calculateOnePageSize()
        requestLayout()
    }

    /**
     * @return 行数
     */
    @IntRange(from = 1)
    fun getRows(): Int {
        return mRows
    }

    /**
     * 设置滑动方向
     *
     * @param orientation [.HORIZONTAL] or [.VERTICAL]
     */
    fun setOrientation(@Orientation orientation: Int) {
        assertNotInLayoutOrScroll(null)
        require(!(orientation != HORIZONTAL && orientation != VERTICAL)) { "invalid orientation:$orientation" }
        if (orientation != mOrientation) {
            mOrientation = orientation
            requestLayout()
        }
    }

    @Orientation
    fun getOrientation(): Int {
        return mOrientation
    }

    fun setReverseLayout(reverseLayout: Boolean) {
        assertNotInLayoutOrScroll(null)
        if (reverseLayout == mReverseLayout) {
            return
        }
        mReverseLayout = reverseLayout
        requestLayout()
    }

    fun getReverseLayout(): Boolean {
        return mReverseLayout
    }

    /**
     * @param position position
     * @return 获取当前position所在页下标
     */
    fun getPagerIndexByPosition(position: Int): Int {
        return position / mOnePageSize
    }

    /**
     * @return 获取最大页数
     */
    fun getMaxPagerIndex(): Int {
        return getPagerIndexByPosition(itemCount - 1)
    }

    /**
     * 直接滚到第几页
     *
     * @param pagerIndex 第几页
     */
    fun scrollToPagerIndex(@IntRange(from = 0) pagerIndex: Int) {
        if (mRecyclerView == null) {
            Log.e(TAG, "scrollToPagerIndex - RecyclerView is null")
            return
        }
        Log.e(TAG,"scrollToPagerIndex-pagerIndex = $pagerIndex")
        Log.e(TAG,"scrollToPagerIndex-mCurrentPagerIndex = $mCurrentPagerIndex")
        Log.e(TAG,"scrollToPagerIndex-itemCount = $itemCount")
        var pagerIndex = pagerIndex
        assertNotInLayoutOrScroll(null)

        //先找到目标position所在第几页
        pagerIndex = Math.min(Math.max(pagerIndex, 0), getMaxPagerIndex())
        if (pagerIndex == mCurrentPagerIndex) {
            //同一页直接return
            return
        }
        Log.e(TAG,"scrollToPagerIndex-pagerIndex2 = $pagerIndex")
        setCurrentPagerIndex(pagerIndex)
        requestLayout()
    }

    /**
     * 直接滚动到上一页
     */
    fun scrollToPrePager() {
        assertNotInLayoutOrScroll(null)
        scrollToPagerIndex(mCurrentPagerIndex - 1)
    }

    /**
     * 直接滚动到下一页
     */
    fun scrollToNextPager() {
        assertNotInLayoutOrScroll(null)
        scrollToPagerIndex(mCurrentPagerIndex + 1)
    }

    /**
     * 平滑滚到第几页，为避免长时间滚动，会预先跳转到就近位置，默认3页
     *
     * @param pagerIndex 第几页，下标从0开始
     */
    fun smoothScrollToPagerIndex(@IntRange(from = 0) pagerIndex: Int) {
        var pagerIndex = pagerIndex
        assertNotInLayoutOrScroll(null)
        pagerIndex = Math.min(Math.max(pagerIndex, 0), getMaxPagerIndex())
        val previousIndex = mCurrentPagerIndex
        if (pagerIndex == previousIndex) {
            //同一页直接return
            return
        }
        val isLayoutToEnd = pagerIndex > previousIndex
        if (Math.abs(pagerIndex - previousIndex) > 3) {
            //先就近直接跳转
            val transitionIndex = if (pagerIndex > previousIndex) pagerIndex - 3 else pagerIndex + 3
            scrollToPagerIndex(transitionIndex)
            if (mRecyclerView != null) {
                mRecyclerView!!.post(
                    SmoothScrollToPosition(
                        getPositionByPagerIndex(pagerIndex, isLayoutToEnd), this,
                        mRecyclerView!!
                    )
                )
            }
        } else {
            val smoothScroller = PagerGridSmoothScroller(mRecyclerView, this)
            smoothScroller.targetPosition = getPositionByPagerIndex(pagerIndex, isLayoutToEnd)
            startSmoothScroll(smoothScroller)
        }
    }

    /**
     * 平滑到上一页
     */
    fun smoothScrollToPrePager() {
        assertNotInLayoutOrScroll(null)
        smoothScrollToPagerIndex(mCurrentPagerIndex - 1)
    }

    /**
     * 平滑到下一页
     */
    fun smoothScrollToNextPager() {
        assertNotInLayoutOrScroll(null)
        smoothScrollToPagerIndex(mCurrentPagerIndex + 1)
    }

    protected fun createLayoutState(): LayoutState? {
        return LayoutState()
    }

    protected fun createLayoutChunkResult(): LayoutChunkResult? {
        return LayoutChunkResult()
    }

    protected fun isLayoutRTL(): Boolean {
        return layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL
    }

    /**
     * 设置总页数
     *
     * @param pagerCount
     */
    private fun setPagerCount(pagerCount: Int) {
        if (mPagerCount == pagerCount) {
            return
        }
        mPagerCount = pagerCount
        mPagerChangedListener?.onPagerCountChanged(pagerCount)
    }

    /**
     * 返回总页数
     *
     * @return 0：[.getItemCount] is 0
     */
    @IntRange(from = 0)
    fun getPagerCount(): Int {
        return Math.max(mPagerCount, 0)
    }

    /**
     * 设置当前页码
     *
     * @param pagerIndex 页码
     */
    private fun setCurrentPagerIndex(pagerIndex: Int) {
        if (mCurrentPagerIndex == pagerIndex) {
            return
        }
        val prePagerIndex = mCurrentPagerIndex
        mCurrentPagerIndex = pagerIndex
        mPagerChangedListener?.onPagerIndexSelected(prePagerIndex, pagerIndex)
    }

    /**
     * 获取当前的页码
     *
     * @return -1：[.getItemCount] is 0,[.NO_ITEM] . else [.mCurrentPagerIndex]
     */
    @IntRange(from = -1)
    fun getCurrentPagerIndex(): Int {
        return mCurrentPagerIndex
    }

    /**
     * 由于View类中这个方法无法使用，直接copy处理
     *
     * @param view
     * @return 判断view是不是处在一个可滑动的布局中
     * @see ViewGroup.shouldDelayChildPressedState
     */
    private fun isInScrollingContainer(view: View): Boolean {
        var p = view.parent
        while (p is ViewGroup) {
            if (p.shouldDelayChildPressedState()) {
                return true
            }
            p = p.getParent()
        }
        return false
    }

    /**
     * 根据页码下标获取position
     *
     * @param pagerIndex    页码
     * @param isLayoutToEnd true:页的第一个位置，false:页的最后一个位置
     * @return
     */
    private fun getPositionByPagerIndex(pagerIndex: Int, isLayoutToEnd: Boolean): Int {
        return if (isLayoutToEnd) pagerIndex * mOnePageSize else pagerIndex * mOnePageSize + mOnePageSize - 1
    }

    fun getDiffWidth(): Int {
        return Math.max(diffWidth, 0)
    }

    fun getDiffHeight(): Int {
        return Math.max(diffHeight, 0)
    }

    /**
     * 获取真实宽度
     *
     * @return
     */
    private fun getRealWidth(): Int {
        return width - paddingStart - paddingEnd
    }

    /**
     * 获取真实高度
     *
     * @return
     */
    private fun getRealHeight(): Int {
        return height - paddingTop - paddingBottom
    }

    /**
     * 填充布局
     *
     * @param recycler
     * @param state
     * @return 添加的像素数，用于滚动
     */
    private fun fill(recycler: Recycler, state: State): Int {
        val layoutState = mLayoutState
        val start = layoutState?.mAvailable ?: 0
        var remainingSpace = layoutState?.mAvailable ?: 0
        val layoutChunkResult = mLayoutChunkResult
        while (remainingSpace > 0 && layoutState?.hasMore(state) == true) {
            if (mShouldReverseLayout) {
                reverseLayoutChunk(recycler, state, layoutState, layoutChunkResult)
            } else {
                layoutChunk(recycler, state, layoutState, layoutChunkResult)
            }
            layoutState.mAvailable -= layoutChunkResult!!.mConsumed
            remainingSpace -= layoutChunkResult.mConsumed
        }
        val layoutToEnd = layoutState?.mLayoutDirection == LayoutState.LAYOUT_END
        //因为最后一列或者一行可能只绘制了收尾的一个，补满
        while (layoutState?.hasMore(state) == true) {
            val isNeedMoveSpan =
                if (layoutToEnd) isNeedMoveToNextSpan(layoutState.mCurrentPosition) else isNeedMoveToPreSpan(
                    layoutState.mCurrentPosition
                )
            if (isNeedMoveSpan) {
                //如果需要切换行或列，直接退出
                break
            }
            if (mShouldReverseLayout) {
                reverseLayoutChunk(recycler, state, layoutState, layoutChunkResult)
            } else {
                layoutChunk(recycler, state, layoutState, layoutChunkResult)
            }
        }
        //回收View
        recycleViews(recycler)
        var mAvailable = layoutState?.mAvailable ?: 0
        return start - mAvailable
    }

    /**
     * 正项布局
     *
     * @param recycler
     * @param state
     * @param layoutState
     * @param layoutChunkResult
     * @see .layoutChunk
     * @see .mShouldReverseLayout
     */
    private fun layoutChunk(
        recycler: Recycler,
        state: State,
        layoutState: LayoutState?,
        layoutChunkResult: LayoutChunkResult?
    ) {
        val layoutToEnd = layoutState?.mLayoutDirection == LayoutState.LAYOUT_END
        val position = layoutState?.mCurrentPosition ?: 0
        val view = layoutState?.next(recycler)
        if (layoutToEnd) {
            addView(view)
        } else {
            addView(view, 0)
        }
        layoutState?.mCurrentPosition = if (layoutToEnd) layoutState!!.getNextPosition(
            position,
            mOrientation,
            mRows,
            mColumns,
            state
        ) else layoutState!!.getPrePosition(position, mOrientation, mRows, mColumns, state)
        measureChildWithMargins(view!!, mItemWidthUsed, mItemHeightUsed)
        //是否需要换行或者换列
        val isNeedMoveSpan =
            if (layoutToEnd) isNeedMoveToNextSpan(position) else isNeedMoveToPreSpan(position)
        layoutChunkResult?.mConsumed =
            if (isNeedMoveSpan) if (mOrientation == HORIZONTAL) mItemWidth else mItemHeight else 0

        //记录的上一个View的位置
        val rect = layoutState.mOffsetRect
        val left: Int
        val top: Int
        val right: Int
        val bottom: Int
        if (mOrientation == HORIZONTAL) {
            //水平滑动
            if (layoutToEnd) {
                //向后填充，绘制方向：从上到下
                if (isNeedMoveSpan) {
                    //下一列绘制，从头部开始
                    left = rect.left + mItemWidth + calculateClipOffset(true, position)
                    top = paddingTop
                } else {
                    //当前列绘制
                    left = rect.left
                    top = rect.bottom
                }
                right = left + mItemWidth
                bottom = top + mItemHeight
            } else {
                //向前填充，绘制方向：从下到上
                if (isNeedMoveSpan) {
                    //上一列绘制，从底部开启
                    left = rect.left - mItemWidth - calculateClipOffset(false, position)
                    bottom = height - paddingBottom
                } else {
                    //当前列绘制
                    left = rect.left
                    bottom = rect.top
                }
                top = bottom - mItemHeight
                right = left + mItemWidth
            }
        } else {
            if (layoutToEnd) {
                //向下填充，绘制方向：从左到右
                if (isNeedMoveSpan) {
                    //下一行绘制，从头部开始
                    left = paddingStart
                    top = rect.bottom + calculateClipOffset(true, position)
                } else {
                    //当前行绘制
                    left = rect.left + mItemWidth
                    top = rect.top
                }
                right = left + mItemWidth
                bottom = top + mItemHeight
            } else {
                //向上填充，绘制方向：从右到左
                if (isNeedMoveSpan) {
                    //上一行绘制，从尾部开始
                    right = width - paddingEnd
                    left = right - mItemWidth
                    bottom = rect.top - calculateClipOffset(false, position)
                    top = bottom - mItemHeight
                } else {
                    //当前行绘制
                    left = rect.left - mItemWidth
                    top = rect.top
                    right = left + mItemWidth
                    bottom = top + mItemHeight
                }
            }
        }
        layoutState.setOffsetRect(left, top, right, bottom)
        layoutDecoratedWithMargins(view, left, top, right, bottom)
    }

    /**
     * 反向布局
     *
     * @param recycler
     * @param state
     * @param layoutState
     * @param layoutChunkResult
     * @see .layoutChunk
     * @see .mShouldReverseLayout
     */
    private fun reverseLayoutChunk(
        recycler: Recycler,
        state: State,
        layoutState: LayoutState?,
        layoutChunkResult: LayoutChunkResult?
    ) {
        //仅处理水平反向滑动，垂直仅改变排列顺序
        val layoutToEnd = layoutState?.mLayoutDirection == LayoutState.LAYOUT_END
        val position = layoutState?.mCurrentPosition ?: 0
        val view = layoutState?.next(recycler)
        if (layoutToEnd) {
            addView(view)
        } else {
            addView(view, 0)
        }
        layoutState?.mCurrentPosition = if (layoutToEnd) layoutState!!.getNextPosition(
            position,
            mOrientation,
            mRows,
            mColumns,
            state
        ) else layoutState!!.getPrePosition(position, mOrientation, mRows, mColumns, state)
        measureChildWithMargins(view!!, mItemWidthUsed, mItemHeightUsed)
        //是否需要换行或者换列
        val isNeedMoveSpan =
            if (layoutToEnd) isNeedMoveToNextSpan(position) else isNeedMoveToPreSpan(position)
        layoutChunkResult!!.mConsumed =
            if (isNeedMoveSpan) if (mOrientation == HORIZONTAL) mItemWidth else mItemHeight else 0

        //记录的上一个View的位置
        val rect = layoutState.mOffsetRect
        val left: Int
        val top: Int
        val right: Int
        val bottom: Int
        if (mOrientation == HORIZONTAL) {
            //水平滑动
            if (layoutToEnd) {
                //向前填充，绘制方向：从上到下
                if (isNeedMoveSpan) {
                    //上一列绘制，从头部开始
                    left = rect.left - mItemWidth - calculateClipOffset(true, position)
                    top = paddingTop
                } else {
                    //当前列绘制
                    left = rect.left
                    top = rect.bottom
                }
                right = left + mItemWidth
                bottom = top + mItemHeight
            } else {
                //向后填充，绘制方向：从下到上
                if (isNeedMoveSpan) {
                    //下一列绘制，从底部开启
                    left = rect.left + mItemWidth + calculateClipOffset(false, position)
                    bottom = height - paddingBottom
                } else {
                    //当前列绘制
                    left = rect.left
                    bottom = rect.top
                }
                top = bottom - mItemHeight
                right = left + mItemWidth
            }
        } else {
            if (layoutToEnd) {
                //向下填充，绘制方向：从右到左
                if (isNeedMoveSpan) {
                    //下一行绘制，从尾部开始
                    right = width - paddingEnd
                    top = rect.bottom + calculateClipOffset(true, position)
                } else {
                    //当前行绘制，向前布局
                    right = rect.left
                    top = rect.top
                }
                left = right - mItemWidth
                bottom = top + mItemHeight
            } else {
                //向上填充，绘制方向：从左到右
                if (isNeedMoveSpan) {
                    //上一行绘制，从头部开始
                    left = paddingStart
                    right = left + mItemWidth
                    bottom = rect.top - calculateClipOffset(false, position)
                    top = bottom - mItemHeight
                } else {
                    //当前行绘制，向后布局
                    left = rect.right
                    right = left + mItemWidth
                    top = rect.top
                    bottom = top + mItemHeight
                }
            }
        }
        layoutState.setOffsetRect(left, top, right, bottom)
        layoutDecoratedWithMargins(view, left, top, right, bottom)
    }

    /**
     * @param delta    手指滑动的距离
     * @param recycler
     * @param state
     * @return
     */
    private fun scrollBy(delta: Int, recycler: Recycler, state: State): Int {
        if (childCount == 0 || delta == 0 || mPagerCount == 1) {
            return 0
        }
        mLayoutState!!.mRecycle = true
        val layoutDirection: Int
        layoutDirection = if (shouldHorizontallyReverseLayout()) {
            if (delta > 0) LayoutState.LAYOUT_START else LayoutState.LAYOUT_END
        } else {
            if (delta > 0) LayoutState.LAYOUT_END else LayoutState.LAYOUT_START
        }
        mLayoutState!!.mLayoutDirection = layoutDirection
        val layoutToEnd = layoutDirection == LayoutState.LAYOUT_END
        val absDelta = Math.abs(delta)
        Log.i(TAG, "scrollBy -> before : childCount:" + childCount +
                ",recycler.scrapList.size:" + recycler.scrapList.size + ",delta:" + delta)
        updateLayoutState(layoutToEnd, absDelta, true, state)
        var consumed = mLayoutState!!.mScrollingOffset + fill(recycler, state)
        if (layoutToEnd) {
            //向后滑动，添加补充距离
            consumed += mLayoutState!!.replenishDelta
        }
        if (consumed < 0) {
            return 0
        }
        //是否已经完全填充到头部或者尾部，滑动的像素>消费的像素
        val isOver = absDelta > consumed
        //计算实际可移动值
        val scrolled = if (isOver) layoutDirection * consumed else delta
        //移动
        offsetChildren(-scrolled)
        mLayoutState!!.mLastScrollDelta = scrolled

        //回收view，此步骤在移动之后
        recycleViews(recycler)
        Log.i(TAG, "scrollBy -> end : childCount:" + childCount + "," +
                "recycler.scrapList.size:" + recycler.scrapList.size + "," +
                "delta:" + delta + ",scrolled:" + scrolled)
        return scrolled
    }

    private fun updateLayoutState(
        layoutToEnd: Boolean, requiredSpace: Int,
        canUseExistingSpace: Boolean, state: State
    ) {
        val child: View?
        //计算在不添加新view的情况下可以滚动多少（与布局无关）
        val scrollingOffset: Int
        if (layoutToEnd) {
            child = getChildClosestToEnd()
            scrollingOffset = if (shouldHorizontallyReverseLayout()) {
                -getDecoratedStart(child) + getStartAfterPadding()
            } else {
                getDecoratedEnd(child) - getEndAfterPadding()
            }
        } else {
            child = getChildClosestToStart()
            scrollingOffset = if (shouldHorizontallyReverseLayout()) {
                getDecoratedEnd(child) - getEndAfterPadding()
            } else {
                -getDecoratedStart(child) + getStartAfterPadding()
            }
        }
        getDecoratedBoundsWithMargins(child!!, mLayoutState!!.mOffsetRect)
        mLayoutState!!.mCurrentPosition = if (layoutToEnd) mLayoutState!!.getNextPosition(
            getPosition(
                child
            ), mOrientation, mRows, mColumns, state
        ) else mLayoutState!!.getPrePosition(
            getPosition(
                child
            ), mOrientation, mRows, mColumns, state
        )
        mLayoutState!!.mAvailable = requiredSpace
        if (canUseExistingSpace) {
            mLayoutState!!.mAvailable -= scrollingOffset
        }
        mLayoutState!!.mScrollingOffset = scrollingOffset
    }

    private fun getChildClosestToEnd(): View? {
        return getChildAt(childCount - 1)
    }

    private fun getChildClosestToStart(): View? {
        return getChildAt(0)
    }

    /**
     * 回收View
     *
     * @param recycler
     */
    private fun recycleViews(recycler: Recycler) {
        //是否回收view
        if (!mLayoutState!!.mRecycle) {
            return
        }
        if (shouldHorizontallyReverseLayout()) {
            if (mLayoutState!!.mLayoutDirection == LayoutState.LAYOUT_START) {
                //水平向右或者垂直向下滑动
                recycleViewsFromStart(recycler)
            } else {
                //水平向左或者垂直向上滑动
                recycleViewsFromEnd(recycler)
            }
        } else {
            if (mLayoutState!!.mLayoutDirection == LayoutState.LAYOUT_START) {
                //水平向左或者垂直向上滑动
                recycleViewsFromEnd(recycler)
            } else {
                //水平向右或者垂直向下滑动
                recycleViewsFromStart(recycler)
            }
        }
    }

    private fun recycleViewsFromStart(recycler: Recycler) {
        //如果clipToPadding==false，则不计算padding
        val clipToPadding = clipToPadding
        val start = if (clipToPadding) getStartAfterPadding() else 0
        val childCount = childCount
        for (i in childCount - 1 downTo 0) {
            val childAt = getChildAt(i)
            if (childAt != null) {
                val decorated = getDecoratedEnd(childAt)
                if (decorated >= start) {
                    continue
                }
                Log.d(TAG, "recycleViewsFromStart-removeAndRecycleViewAt: " + i +
                        ", position: " + getPosition(childAt))
                removeAndRecycleViewAt(i, recycler)
                //                removeAndRecycleView(childAt, recycler);
            }
        }
    }

    private fun recycleViewsFromEnd(recycler: Recycler) {
        //如果clipToPadding==false，则不计算padding
        val clipToPadding = clipToPadding
        val end =
            if (clipToPadding) getEndAfterPadding() else if (mOrientation == HORIZONTAL) width else height
        val childCount = childCount
        for (i in childCount - 1 downTo 0) {
            val childAt = getChildAt(i)
            if (childAt != null) {
                val decorated = getDecoratedStart(childAt)
                if (decorated <= end) {
                    continue
                }
                Log.d(TAG,"recycleViewsFromEnd-removeAndRecycleViewAt: " + i + ", " +
                        "position: " + getPosition(childAt))
                removeAndRecycleViewAt(i, recycler)
                //                removeAndRecycleView(childAt, recycler);
            }
        }
    }

    private fun getDecoratedEnd(child: View?): Int {
        val params = child!!.layoutParams as LayoutParams
        return if (mOrientation == HORIZONTAL) getDecoratedRight(child) + params.rightMargin else getDecoratedBottom(
            child
        ) + params.bottomMargin
    }

    private fun getDecoratedStart(child: View?): Int {
        val params = child!!.layoutParams as LayoutParams
        return if (mOrientation == HORIZONTAL) getDecoratedLeft(child) - params.leftMargin else getDecoratedTop(
            child
        ) - params.topMargin
    }

    private fun getEndAfterPadding(): Int {
        return if (mOrientation == HORIZONTAL) width - paddingEnd else height - paddingBottom
    }

    private fun getStartAfterPadding(): Int {
        return if (mOrientation == HORIZONTAL) paddingStart else paddingTop
    }

    private fun getClipToPaddingSize(): Int {
        return if (mOrientation == HORIZONTAL) paddingStart + paddingEnd else paddingTop + paddingBottom
    }

    /**
     * 计算[.getClipToPadding]==false时偏移量
     *
     * @param layoutToEnd 是否是向后布局
     * @param position    position
     * @return offset
     */
    private fun calculateClipOffset(layoutToEnd: Boolean, position: Int): Int {
        val clipToPadding = clipToPadding
        return if (!clipToPadding && position % mOnePageSize == if (layoutToEnd) 0 else mOnePageSize - 1) getClipToPaddingSize() else 0
    }

    private fun getEnd(): Int {
        return if (mOrientation == HORIZONTAL) getRealWidth() else getRealHeight()
    }

    /**
     * 移动Children
     *
     * @param delta 移动偏移量
     */
    private fun offsetChildren(delta: Int) {
        if (mOrientation == HORIZONTAL) {
            offsetChildrenHorizontal(delta)
        } else {
            offsetChildrenVertical(delta)
        }
    }

    /**
     * @return 当前Recycler是否是静止状态
     */
    private fun isIdle(): Boolean {
        return mRecyclerView == null || mRecyclerView!!.scrollState == SCROLL_STATE_IDLE
    }

    /**
     * @param position
     * @return 是否需要换到下一行或列
     */
    private fun isNeedMoveToNextSpan(position: Int): Boolean {
        return if (mOrientation == HORIZONTAL) {
            val surplus = position % mOnePageSize
            val rowIndex = surplus / mColumns
            //是否在最后一行
            rowIndex == 0
        } else {
            position % mColumns == 0
        }
    }

    /**
     * @param position
     * @return 是否需要换到上一行或列
     */
    private fun isNeedMoveToPreSpan(position: Int): Boolean {
        return if (mOrientation == HORIZONTAL) {
            val surplus = position % mOnePageSize
            //在第几行
            val rowIndex = surplus / mColumns
            //是否在第一行
            rowIndex == mRows - 1
        } else {
            position % mColumns == mColumns - 1
        }
    }

    private fun computeScrollOffset(state: State): Int {
        if (childCount == 0 || state.itemCount == 0) {
            return 0
        }
        val firstView = getChildAt(0) ?: return 0
        val position = getPosition(firstView)
        val avgSize = getEnd().toFloat() / if (mOrientation == HORIZONTAL) mColumns else mRows
        val index: Int
        index = if (mOrientation == HORIZONTAL) {
            //所在第几列
            val pagerIndex = getPagerIndexByPosition(position)
            pagerIndex * mColumns + position % mColumns
        } else {
            //所在第几行
            position / mColumns
        }
        val scrollOffset: Int
        scrollOffset = if (shouldHorizontallyReverseLayout()) {
            val scrollRange = computeScrollRange(state) - computeScrollExtent(state)
            scrollRange - Math.round(index * avgSize + (getDecoratedEnd(firstView) - getEndAfterPadding()))
        } else {
            Math.round(index * avgSize + (getStartAfterPadding() - getDecoratedStart(firstView)))
        }
        Log.i(TAG, "computeScrollOffset: $scrollOffset")
        return scrollOffset
    }

    private fun computeScrollExtent(state: State): Int {
        if (childCount == 0 || state.itemCount == 0) {
            return 0
        }
        val scrollExtent = getEnd()
        Log.i(TAG, "computeScrollExtent: $scrollExtent")
        return scrollExtent
    }

    private fun computeScrollRange(state: State): Int {
        if (childCount == 0 || state.itemCount == 0) {
            return 0
        }
        val scrollRange = Math.max(mPagerCount, 0) * getEnd()
        Log.i(TAG, "computeScrollRange: $scrollRange")
        return scrollRange
    }

    private fun resolveShouldLayoutReverse() {
        mShouldReverseLayout = if (mOrientation == VERTICAL || !isLayoutRTL()) {
            mReverseLayout
        } else {
            //水平滑动且是RTL
            !mReverseLayout
        }
    }

    fun getShouldReverseLayout(): Boolean {
        return mShouldReverseLayout
    }

    /**
     * @return 左上角第一个view的位置
     */
    fun getStartSnapRect(): Rect {
        return mStartSnapRect
    }

    /**
     * @return 右下角最后一个view的位置
     */
    fun getEndSnapRect(): Rect {
        return mEndSnapRect
    }

    /**
     * 根据下标计算页码
     *
     * @param position
     */
    fun calculateCurrentPagerIndexByPosition(position: Int) {
        setCurrentPagerIndex(getPagerIndexByPosition(position))
    }

    fun getLayoutState(): LayoutState? {
        return mLayoutState
    }

    /**
     * @return 是否水平方向反转布局
     */
    fun shouldHorizontallyReverseLayout(): Boolean {
        return mShouldReverseLayout && mOrientation == HORIZONTAL
    }

    fun getPageSize(): Int {
        return mOnePageSize
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        val childCount = childCount
        if (childCount == 0) {
            return null
        }
        var firstSnapPosition = NO_POSITION
        for (i in childCount - 1 downTo 0) {
            val childAt = getChildAt(i)
            if (childAt != null) {
                val position = getPosition(childAt)
                if (position % mOnePageSize == 0) {
                    firstSnapPosition = position
                    break
                }
            }
        }
        if (firstSnapPosition == NO_POSITION) {
            return null
        }
        var direction = if (targetPosition < firstSnapPosition) -1f else 1f
        if (shouldHorizontallyReverseLayout()) {
            direction = -direction
        }
        Log.d(TAG, "computeScrollVectorForPosition-firstSnapPosition: $firstSnapPosition, " +
                "targetPosition:$targetPosition,mOrientation :$mOrientation, direction:$direction")
        return if (mOrientation == HORIZONTAL) {
            PointF(direction, 0f)
        } else {
            PointF(0f, direction)
        }
    }

    fun getCurrentPage(): Int {
        return mCurrentPagerIndex
    }

    fun getTotalPageCount(): Int {
        return mPagerCount
    }

    /**
     * 自定义LayoutParams
     */
    class LayoutParams : RecyclerView.LayoutParams {
        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs) {}
        constructor(width: Int, height: Int) : super(width, height) {}
        constructor(source: MarginLayoutParams?) : super(source) {}
        constructor(source: ViewGroup.LayoutParams?) : super(source) {}
        constructor(source: RecyclerView.LayoutParams?) : super(source) {}
    }

    private class SmoothScrollToPosition internal constructor(
        private val mPosition: Int,
        private val mLayoutManager: PagerGridLayoutManager,
        private val mRecyclerView: RecyclerView
    ) :
        Runnable {
        override fun run() {
            val smoothScroller = PagerGridSmoothScroller(mRecyclerView, mLayoutManager)
            smoothScroller.targetPosition = mPosition
            mLayoutManager.startSmoothScroll(smoothScroller)
        }
    }

    class LayoutState {
        /**
         * 可填充的View空间大小
         */
        var mAvailable = 0

        /**
         * 是否需要回收View
         */
        var mRecycle = false
        var mCurrentPosition = 0

        /**
         * 布局的填充方向
         * 值为 [.LAYOUT_START] or [.LAYOUT_END]
         */
        var mLayoutDirection = 0

        /**
         * 在滚动状态下构造布局状态时使用。
         * 它应该设置我们可以在不创建新视图的情况下进行滚动量。
         * 有效的视图回收需要设置
         */
        var mScrollingOffset = 0

        /**
         * 开始绘制的坐标位置
         */
        val mOffsetRect = Rect()

        /**
         * 最近一次的滑动数量
         */
        var mLastScrollDelta = 0

        /**
         * 需要补充滑动的距离
         */
        var replenishDelta = 0
        fun setOffsetRect(left: Int, top: Int, right: Int, bottom: Int) {
            mOffsetRect[left, top, right] = bottom
        }

        fun next(recycler: Recycler): View {
            return recycler.getViewForPosition(mCurrentPosition)
        }

        fun hasMore(state: State): Boolean {
            return mCurrentPosition >= 0 && mCurrentPosition < state.itemCount
        }

        /**
         * @param currentPosition 当前的位置
         * @param orientation     方向
         * @param rows            行数
         * @param columns         列数
         * @param state           状态
         * @return 下一个位置
         */
        fun getNextPosition(
            currentPosition: Int,
            orientation: Int,
            rows: Int,
            columns: Int,
            state: State
        ): Int {
            var position: Int
            val onePageSize = rows * columns
            if (orientation == HORIZONTAL) {
                val surplus = currentPosition % onePageSize
                //水平滑动
                //向后追加item
                if (surplus == onePageSize - 1) {
                    //一页的最后一个位置
                    position = currentPosition + 1
                } else {
                    //在第几列
                    val columnsIndex = currentPosition % columns
                    //在第几行
                    val rowIndex = surplus / columns
                    //是否在最后一行
                    val isLastRow = rowIndex == rows - 1
                    if (isLastRow) {
                        position = currentPosition - rowIndex * columns + 1
                    } else {
                        position = currentPosition + columns
                        if (position >= state.itemCount) {
                            //越界了
                            if (columnsIndex != columns - 1) {
                                //如果不是最后一列，计算换行位置
                                position = currentPosition - rowIndex * columns + 1
                            }
                        }
                    }
                }
            } else {
                //垂直滑动
                position = currentPosition + 1
            }
            return position
        }

        /**
         * @param currentPosition 当前的位置
         * @param orientation     方向
         * @param rows            行数
         * @param columns         列数
         * @param state           状态
         * @return 上一个位置
         */
        fun getPrePosition(
            currentPosition: Int,
            orientation: Int,
            rows: Int,
            columns: Int,
            state: State?
        ): Int {
            val position: Int
            val onePageSize = rows * columns
            position = if (orientation == HORIZONTAL) {
                val surplus = currentPosition % onePageSize
                //水平滑动
                //向前追加item
                if (surplus == 0) {
                    //一页的第一个位置
                    currentPosition - 1
                } else {
                    //在第几行
                    val rowIndex = surplus / columns
                    //是否在第一行
                    val isFirstRow = rowIndex == 0
                    if (isFirstRow) {
                        currentPosition - 1 + (rows - 1) * columns
                    } else {
                        currentPosition - columns
                    }
                }
            } else {
                //垂直滑动
                currentPosition - 1
            }
            return position
        }

        companion object {
            const val LAYOUT_START = -1
            const val LAYOUT_END = 1
            const val SCROLLING_OFFSET_NaN = Int.MIN_VALUE
        }
    }

    protected class LayoutChunkResult {
        var mConsumed = 0
        protected var mFinished = false
        protected var mIgnoreConsumed = false
        protected var mFocusable = false
        protected fun resetInternal() {
            mConsumed = 0
            mFinished = false
            mIgnoreConsumed = false
            mFocusable = false
        }
    }

    /**
     * @see RecyclerView.LayoutManager.onSaveInstanceState
     * @see RecyclerView.LayoutManager.onRestoreInstanceState
     */
    protected class SavedState : Parcelable {
        /**
         * 当前滑动方向
         */
        var mOrientation = 0

        /**
         * 行数
         */
        var mRows = 0

        /**
         * 列数
         */
        var mColumns = 0

        /**
         * 当前页码下标
         * 从0开始
         */
        var mCurrentPagerIndex: Int = NO_ITEM
        var mReverseLayout = false
        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(mOrientation)
            dest.writeInt(mRows)
            dest.writeInt(mColumns)
            dest.writeInt(mCurrentPagerIndex)
        }

        fun readFromParcel(source: Parcel) {
            mOrientation = source.readInt()
            mRows = source.readInt()
            mColumns = source.readInt()
            mCurrentPagerIndex = source.readInt()
        }

        constructor() {}
        protected constructor(`in`: Parcel) {
            mOrientation = `in`.readInt()
            mRows = `in`.readInt()
            mColumns = `in`.readInt()
            mCurrentPagerIndex = `in`.readInt()
        }

        override fun toString(): String {
            return "SavedState{" +
                    "mOrientation=" + mOrientation +
                    ", mRows=" + mRows +
                    ", mColumns=" + mColumns +
                    ", mCurrentPagerIndex=" + mCurrentPagerIndex +
                    '}'
        }

        @SuppressLint("ParcelCreator")
        val CREATOR = object : Creator<SavedState> {
            override fun createFromParcel(source: Parcel): SavedState {
                return SavedState(source)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    interface PagerChangedListener {
        /**
         * 页面总数量变化
         *
         * @param pagerCount 页面总数，从1开始，为0时说明无数据，{[.NO_PAGER_COUNT]}
         */
        fun onPagerCountChanged(@IntRange(from = 0) pagerCount: Int)

        /**
         * 选中的页面下标
         *
         * @param prePagerIndex     上次的页码，当{[.getItemCount]}为0时，为-1，{[.NO_ITEM]}
         * @param currentPagerIndex 当前的页码，当{[.getItemCount]}为0时，为-1，{[.NO_ITEM]}
         */
        fun onPagerIndexSelected(
            @IntRange(from = -1) prePagerIndex: Int,
            @IntRange(from = -1) currentPagerIndex: Int
        )
    }
}