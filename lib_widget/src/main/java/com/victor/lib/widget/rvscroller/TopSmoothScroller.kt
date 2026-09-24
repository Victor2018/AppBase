package com.victor.lib.widget.rvscroller

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller

class TopSmoothScroller(context: Context?) : LinearSmoothScroller(context) {

    var duration = 100f //越小越快
    //25f是系统的smoothScrollToPosition的速度
    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        return duration / displayMetrics.densityDpi
    }

    override fun calculateDtToFit(
        viewStart: Int,
        viewEnd: Int,
        boxStart: Int,
        boxEnd: Int,
        snapPreference: Int
    ): Int {
        return boxStart - viewStart
    }
}