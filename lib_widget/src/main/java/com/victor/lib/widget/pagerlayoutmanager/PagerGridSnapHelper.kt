package com.victor.lib.widget.pagerlayoutmanager

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import java.util.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PagerGridSnapHelper
 * Author: Victor
 * Date: 2022/10/9 12:27
 * Description: 
 * -----------------------------------------------------------------
 */

class PagerGridSnapHelper : SnapHelper() {
    private val TAG = "PagerGridSnapHelper"

    private var mRecyclerView: RecyclerView? = null

    /**
     * 存放锚点位置的view，一般数量为1或2个
     */
    private val snapList: MutableList<View> = ArrayList(2)

    fun PagerGridSnapHelper() {}

    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun createScroller(layoutManager: RecyclerView.LayoutManager): RecyclerView.SmoothScroller? {
        if (layoutManager !is PagerGridLayoutManager) {
            return null
        }
        return if (mRecyclerView != null) {
            PagerGridSmoothScroller(mRecyclerView, layoutManager)
        } else null
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        val itemCount = layoutManager.itemCount
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION
        }
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return RecyclerView.NO_POSITION
        }
        if (layoutManager !is PagerGridLayoutManager) {
            return RecyclerView.NO_POSITION
        }
        val manager = layoutManager
        if (manager.getLayoutState()?.mLastScrollDelta === 0) {
            //说明无法滑动了，到头或滑动到底
            return RecyclerView.NO_POSITION
        }
        val calculateScrollDistance = calculateScrollDistance(velocityX, velocityY)
        //计算滑动的距离
        var scrollDistance =
            if (manager.canScrollHorizontally()) calculateScrollDistance[0] else calculateScrollDistance[1]
        if (manager.shouldHorizontallyReverseLayout()) {
            //取反
            scrollDistance = -scrollDistance
        }

        //滑动方向是否向前
        val forwardDirection = isForwardFling(manager, velocityX, velocityY)
        //布局中心位置，水平滑动为X轴坐标，垂直滑动为Y轴坐标
        val layoutCenter = getLayoutCenter(manager)
        reacquireSnapList(manager)

        //目标位置
        var targetPosition = RecyclerView.NO_POSITION
        when (snapList.size) {
            1 -> {
                val view = snapList[0]
                val position = manager.getPosition(view)
                if (forwardDirection) {
                    //方向向前
                    if (scrollDistance >= layoutCenter) {
                        //计算滑动的距离直接超过布局一半值
                        targetPosition = position
                    } else {
                        if (manager.shouldHorizontallyReverseLayout()) {
                            //水平滑动需要反转的情况
                            val viewDecoratedEnd = getViewDecoratedEnd(manager, view)
                            if (viewDecoratedEnd + scrollDistance >= layoutCenter) {
                                //view的结束线+scrollDistance大于于中间线，
                                //即view在中间线的左边或者上边
                                targetPosition = position
                            } else {
                                //寻找上一个锚点位置
                                targetPosition = position - 1
                                if (targetPosition < 0) {
                                    targetPosition = RecyclerView.NO_POSITION
                                }
                            }
                        } else {
                            val viewDecoratedStart = getViewDecoratedStart(manager, view)
                            if (viewDecoratedStart - scrollDistance <= layoutCenter) {
                                //view的起始线-scrollDistance 小于中间线，
                                //即view在中间线的左边或者上边
                                targetPosition = position
                            } else {
                                //寻找上一个锚点位置
                                targetPosition = position - 1
                                if (targetPosition < 0) {
                                    targetPosition = RecyclerView.NO_POSITION
                                }
                            }
                        }
                    }
                } else {
                    //方向向后
                    if (Math.abs(scrollDistance) >= layoutCenter) {
                        //计算滑动的距离直接超过布局一半值
                        targetPosition = position - 1
                        if (targetPosition < 0) {
                            targetPosition = RecyclerView.NO_POSITION
                        }
                    } else {
                        if (manager.shouldHorizontallyReverseLayout()) {
                            //水平滑动需要反转的情况
                            val viewDecoratedStart = getViewDecoratedStart(manager, view)
                            if (viewDecoratedStart - Math.abs(scrollDistance) < layoutCenter) {
                                //寻找上一个锚点位置
                                targetPosition = position - 1
                                if (targetPosition < 0) {
                                    targetPosition = RecyclerView.NO_POSITION
                                }
                            } else {
                                targetPosition = position
                            }
                        } else {
                            val viewDecoratedEnd = getViewDecoratedEnd(manager, view)
                            if (viewDecoratedEnd + Math.abs(scrollDistance) > layoutCenter) {
                                //寻找上一个锚点位置
                                targetPosition = position - 1
                                if (targetPosition < 0) {
                                    targetPosition = RecyclerView.NO_POSITION
                                }
                            } else {
                                targetPosition = position
                            }
                        }
                    }
                }
            }
            2 -> {
                val view1 = snapList[0]
                val position1 = manager.getPosition(view1)
                val view2 = snapList[1]
                val position2 = manager.getPosition(view2)
                if (manager.shouldHorizontallyReverseLayout()) {
                    if (forwardDirection) {
                        //方向向前
                        if (scrollDistance >= layoutCenter) {
                            //计算滑动的距离直接超过布局一半值
                            targetPosition = position2
                        } else {
                            val viewDecoratedEnd2 = getViewDecoratedEnd(manager, view2)
                            if (viewDecoratedEnd2 + scrollDistance >= layoutCenter) {
                                //view的结束线+scrollDistance 大于中间线，
                                //即view在中间线的左边或者上边
                                targetPosition = position2
                            } else {
                                targetPosition = position2 - 1
                                if (targetPosition < 0) {
                                    targetPosition = RecyclerView.NO_POSITION
                                }
                            }
                        }
                    } else {
                        if (Math.abs(scrollDistance) >= layoutCenter) {
                            targetPosition = position2 - 1
                            if (targetPosition < 0) {
                                targetPosition = RecyclerView.NO_POSITION
                            }
                        } else {
                            val viewDecoratedStart1 = getViewDecoratedStart(manager, view1)
                            if (viewDecoratedStart1 - Math.abs(scrollDistance) <= layoutCenter) {
                                targetPosition = position2 - 1
                                if (targetPosition < 0) {
                                    targetPosition = RecyclerView.NO_POSITION
                                }
                            } else {
                                targetPosition = position2
                            }
                        }
                    }
                } else {
                    if (forwardDirection) {
                        //方向向前
                        if (scrollDistance >= layoutCenter) {
                            //计算滑动的距离直接超过布局一半值
                            targetPosition = position2
                        } else {
                            val viewDecoratedStart2 = getViewDecoratedStart(manager, view2)
                            if (viewDecoratedStart2 - scrollDistance <= layoutCenter) {
                                //view的起始线-scrollDistance 小于中间线，
                                //即view在中间线的左边或者上边
                                targetPosition = position2
                            } else {
                                targetPosition = position2 - 1
                                if (targetPosition < 0) {
                                    targetPosition = RecyclerView.NO_POSITION
                                }
                            }
                        }
                    } else {
                        if (Math.abs(scrollDistance) >= layoutCenter) {
                            targetPosition = position2 - 1
                            if (targetPosition < 0) {
                                targetPosition = RecyclerView.NO_POSITION
                            }
                        } else {
                            val viewDecoratedEnd1 = getViewDecoratedEnd(manager, view1)
                            if (viewDecoratedEnd1 + Math.abs(scrollDistance) >= layoutCenter) {
                                targetPosition = position2 - 1
                                if (targetPosition < 0) {
                                    targetPosition = RecyclerView.NO_POSITION
                                }
                            } else {
                                targetPosition = position2
                            }
                        }
                    }
                }
            }
            3 ->                 //1行*1列可能出现的情况
                targetPosition = manager.getPosition(snapList[1])
            else -> {
                Log.d(TAG, "findTargetSnapPosition-snapList.size: " + snapList.size)
            }
        }
        Log.d(TAG, "findTargetSnapPosition->forwardDirection:" + forwardDirection
                + ",targetPosition:" + targetPosition + ",velocityX: " + velocityX +
                ",velocityY: " + velocityY + ",scrollDistance:" + scrollDistance +
                ",snapList:" + snapList.size)
        snapList.clear()
        return targetPosition
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        var snapView: View? = null
        if (layoutManager is PagerGridLayoutManager) {
            val manager = layoutManager
            reacquireSnapList(manager)
            when (snapList.size) {
                1 -> {
                    snapView = snapList[0]
                }
                2 -> {

                    //布局中心位置，水平滑动为X轴坐标，垂直滑动为Y轴坐标
                    val layoutCenter = getLayoutCenter(manager)
                    val view1 = snapList[0]
                    val view2 = snapList[1]
                    val rect = Rect()
                    manager.getDecoratedBoundsWithMargins(view2, rect)
                    snapView = if (manager.shouldHorizontallyReverseLayout()) {
                        val viewDecoratedEnd2 = getViewDecoratedEnd(manager, view2)
                        if (viewDecoratedEnd2 <= layoutCenter) {
                            view1
                        } else {
                            view2
                        }
                    } else {
                        val viewDecoratedStart2 = getViewDecoratedStart(manager, view2)
                        if (viewDecoratedStart2 <= layoutCenter) {
                            view2
                        } else {
                            view1
                        }
                    }
                }
                3 ->                     //1行*1列可能出现的情况
                    snapView = snapList[1]
                else -> {
                    Log.d(TAG, "findSnapView wrong -> snapList.size: " + snapList.size)
                }
            }
            Log.i(TAG, "findSnapView: position:" + (if (snapView != null) layoutManager.getPosition(
                snapView) else RecyclerView.NO_POSITION) + ", snapList.size:" + snapList.size)
            snapList.clear()
        }
        return snapView
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        val snapDistance = IntArray(2)
        val targetPosition = layoutManager.getPosition(targetView)
        if (layoutManager is PagerGridLayoutManager) {
            val manager = layoutManager
            //布局中心位置，水平滑动为X轴坐标，垂直滑动为Y轴坐标
            val layoutCenter = getLayoutCenter(manager)
            val dx: Int
            val dy: Int
            val targetRect = Rect()
            layoutManager.getDecoratedBoundsWithMargins(targetView, targetRect)
            if (manager.shouldHorizontallyReverseLayout()) {
                val viewDecoratedEnd = getViewDecoratedEnd(manager, targetView)
                if (viewDecoratedEnd >= layoutCenter) {
                    //向前回退
                    val snapRect: Rect = manager.getStartSnapRect()
                    dx = PagerGridSmoothScroller.calculateDx(manager, snapRect, targetRect)
                    dy = PagerGridSmoothScroller.calculateDy(manager, snapRect, targetRect)
                } else {
                    //向后前进
                    dx = -calculateDxToNextPager(manager, targetRect)
                    dy = -calculateDyToNextPager(manager, targetRect)
                }
            } else {
                val viewDecoratedStart = getViewDecoratedStart(manager, targetView)
                if (viewDecoratedStart <= layoutCenter) {
                    //向前回退
                    val snapRect: Rect = manager.getStartSnapRect()
                    dx = PagerGridSmoothScroller.calculateDx(manager, snapRect, targetRect)
                    dy = PagerGridSmoothScroller.calculateDy(manager, snapRect, targetRect)
                } else {
                    //向后前进
                    dx = -calculateDxToNextPager(manager, targetRect)
                    dy = -calculateDyToNextPager(manager, targetRect)
                }
            }
            snapDistance[0] = dx
            snapDistance[1] = dy
            if (snapDistance[0] == 0 && snapDistance[1] == 0) {
                //说明滑动完成，计算页标
                manager.calculateCurrentPagerIndexByPosition(targetPosition)
            }
            Log.i(TAG, "calculateDistanceToFinalSnap-targetView: " + targetPosition +
                    ",snapDistance: " + Arrays.toString(snapDistance))
        }
        return snapDistance
    }

    private fun isForwardFling(
        layoutManager: PagerGridLayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Boolean {
        return if (layoutManager.canScrollHorizontally()) if (layoutManager.getShouldReverseLayout()) velocityX < 0 else velocityX > 0 else velocityY > 0
    }

    /***
     * 获取锚点view
     * @param manager
     */
    private fun reacquireSnapList(manager: PagerGridLayoutManager) {
        if (!snapList.isEmpty()) {
            snapList.clear()
        }
        val childCount = manager.childCount
        for (i in 0 until childCount) {
            val child = manager.getChildAt(i) ?: continue
            //先去寻找符合锚点位置的view
            if (manager.getPosition(child) % manager.getOnePageSize() === 0) {
                snapList.add(child)
            }
        }
    }

    fun calculateDxToNextPager(manager: PagerGridLayoutManager, targetRect: Rect): Int {
        return if (!manager.canScrollHorizontally()) {
            0
        } else getLayoutEndAfterPadding(manager) - targetRect.left
    }

    fun calculateDyToNextPager(manager: PagerGridLayoutManager, targetRect: Rect): Int {
        return if (!manager.canScrollVertically()) {
            0
        } else getLayoutEndAfterPadding(manager) - targetRect.top
    }

    /**
     * 计算targetView中心位置到布局中心位置的距离
     *
     * @param layoutManager
     * @param targetView
     * @return
     */
    fun distanceToCenter(layoutManager: RecyclerView.LayoutManager, targetView: View): Int {
        //布局中心位置，水平滑动为X轴坐标，垂直滑动为Y轴坐标
        val layoutCenter = getLayoutCenter(layoutManager)
        val childCenter = getChildViewCenter(layoutManager, targetView)
        return childCenter - layoutCenter
    }

    fun getLayoutCenter(layoutManager: RecyclerView.LayoutManager): Int {
        return getLayoutStartAfterPadding(layoutManager) + getLayoutTotalSpace(layoutManager) / 2
    }

    fun getLayoutStartAfterPadding(layoutManager: RecyclerView.LayoutManager): Int {
        return if (layoutManager.canScrollHorizontally()) layoutManager.paddingStart else layoutManager.paddingTop
    }

    fun getLayoutEndAfterPadding(layoutManager: RecyclerView.LayoutManager): Int {
        return if (layoutManager.canScrollHorizontally()) layoutManager.width - layoutManager.paddingEnd else layoutManager.height - layoutManager.paddingBottom
    }

    fun getLayoutTotalSpace(layoutManager: RecyclerView.LayoutManager): Int {
        return if (layoutManager.canScrollHorizontally()) layoutManager.width - layoutManager.paddingStart - layoutManager.paddingEnd else layoutManager.height - layoutManager.paddingTop - layoutManager.paddingBottom
    }

    fun getChildViewCenter(layoutManager: RecyclerView.LayoutManager, targetView: View): Int {
        return (getViewDecoratedStart(layoutManager, targetView)
                + getViewDecoratedMeasurement(layoutManager, targetView) / 2)
    }

    fun getViewDecoratedStart(layoutManager: RecyclerView.LayoutManager, view: View): Int {
        val params = view.layoutParams as RecyclerView.LayoutParams
        return if (layoutManager.canScrollHorizontally()) {
            layoutManager.getDecoratedLeft(view) - params.leftMargin
        } else {
            layoutManager.getDecoratedTop(view) - params.topMargin
        }
    }

    fun getViewDecoratedEnd(layoutManager: RecyclerView.LayoutManager, view: View): Int {
        val params = view.layoutParams as RecyclerView.LayoutParams
        return if (layoutManager.canScrollHorizontally()) {
            layoutManager.getDecoratedRight(view) - params.rightMargin
        } else {
            layoutManager.getDecoratedBottom(view) - params.bottomMargin
        }
    }

    fun getViewDecoratedMeasurement(layoutManager: RecyclerView.LayoutManager, view: View): Int {
        val params = view.layoutParams as RecyclerView.LayoutParams
        return if (layoutManager.canScrollHorizontally()) {
            layoutManager.getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin
        } else {
            layoutManager.getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin
        }
    }
}