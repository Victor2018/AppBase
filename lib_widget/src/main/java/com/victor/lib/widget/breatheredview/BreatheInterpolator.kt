package com.victor.lib.widget.breatheredview

import android.animation.TimeInterpolator

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BreatheInterpolator
 * Author: Victor
 * Date: 2022/5/11 14:28
 * Description: 
 * -----------------------------------------------------------------
 */

class BreatheInterpolator: TimeInterpolator {
    override fun getInterpolation(input: Float): Float {
        val x: Float = 6 * input

        val k = 1.0f / 3
        val t = 6
        val n = 1 //控制函数周期，这里取此函数的第一个周期
        val PI = 3.1416f
        var output = 0f
        if (x >= (n - 1) * t && x < (n - (1 - k)) * t) {
            output = (0.5 * Math.sin((PI / (k * t) * (x - k * t / 2 - (n - 1) * t))
                .toDouble()) + 0.5).toFloat()
        } else if (x >= (n - (1 - k)) * t && x < n * t) {
            output = Math.pow(0.5 * Math.sin((PI / ((1 - k) * t) *
                    (x - (3 - k) * t / 2 - (n - 1) * t)).toDouble()) + 0.5, 2.0).toFloat()
        }

        return output
    }
}