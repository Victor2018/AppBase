package com.victor.lib.widget.breatheredview

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.victor.lib.widget.util.ViewUtils.hide
import com.victor.lib.widget.util.ViewUtils.show

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BreatheRedView
 * Author: Victor
 * Date: 2022/5/11 14:30
 * Description: 呼吸灯闪烁红点
 * -----------------------------------------------------------------
 */

class BreatheRedView : View {
    var showAnim = false

    private var mObjectAnimator: ObjectAnimator? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initialize()
    }

    fun initialize () {
        mObjectAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
        mObjectAnimator?.duration = 2000
        mObjectAnimator?.interpolator = BreatheInterpolator()//使用自定义的插值器
        mObjectAnimator?.repeatCount = ValueAnimator.INFINITE
    }

    /**
     * 开启透明度渐变呼吸动画
     */
    fun startAlphaBreatheAnimation() {
        show()
        if (showAnim) {
            mObjectAnimator?.start()
        }
    }

    /**
     * 关闭透明度渐变呼吸动画
     */
    fun stopAlphaBreatheAnimation () {
        hide()
        mObjectAnimator?.cancel()
    }
}