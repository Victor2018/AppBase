package com.victor.lib.widget.rvscroller

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class AdjustLinearSmoothScroller(context: Context?) : LinearSmoothScroller(context) {

    var duration = 200f //越小越快
    //25f是系统的smoothScrollToPosition的速度
    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        return duration / displayMetrics.densityDpi
    }

    override fun onTargetFound(targetView: View, state: RecyclerView.State, action: Action) {
        super.onTargetFound(targetView, state, action)
        val dy = calculateDyToMakeVisible(targetView, verticalSnapPreference)
        val time = calculateTimeForDeceleration(dy)
        if (time > 0) {
            action.update(0, -dy, time, mDecelerateInterpolator)
        }
    }
}