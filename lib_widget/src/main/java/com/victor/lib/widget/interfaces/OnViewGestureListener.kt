package com.victor.lib.widget.interfaces

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnGestureListener
 * Author: Victor
 * Date: 2022/7/25 18:56
 * Description: 
 * -----------------------------------------------------------------
 */

interface OnViewGestureListener {
    /**
     * 水平滑动距离
     *
     * @param downX 按下位置
     * @param nowX  当前位置
     */
    fun onHorizontalDistance(downX: Float, nowX: Float)

    /**
     * 左边垂直滑动距离
     *
     * @param downY 按下位置
     * @param nowY  当前位置
     */
    fun onLeftVerticalDistance(downY: Float, nowY: Float)

    /**
     * 右边垂直滑动距离
     *
     * @param downY 按下位置
     * @param nowY  当前位置
     */
    fun onRightVerticalDistance(downY: Float, nowY: Float)

    /**
     * 手势结束
     */
    fun onGestureEnd()

    /**
     * 单击事件
     */
    fun onSingleTap()

    /**
     * 双击事件
     */
    fun onDoubleTap()
}