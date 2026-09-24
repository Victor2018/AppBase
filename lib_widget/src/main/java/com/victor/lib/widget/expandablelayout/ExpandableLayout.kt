package com.victor.lib.widget.expandablelayout

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.victor.lib.widget.R

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ExpandableLayout
 * Author: Victor
 * Date: 2025/12/19 10:28
 * Description: 折叠layout
 * -----------------------------------------------------------------
 */

class ExpandableLayout : FrameLayout {

    val KEY_SUPER_STATE = "super_state"
    val KEY_EXPANSION = "expansion"

    private var duration = ExpandableConfig.DEFAULT_DURATION
    private var parallax = 0f
    private var expansion = 0f
    private var orientation = 0
    private var state = 0

    private val interpolator: Interpolator = FastOutSlowInInterpolator()
    private var animator: ValueAnimator? = null

    var mOnExpansionUpdateListener: OnExpansionUpdateListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        if (attrs != null) {
            val a = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableLayout)
            duration = a.getInt(
                R.styleable.ExpandableLayout_el_duration,
                ExpandableConfig.DEFAULT_DURATION
            )
            expansion = if (a.getBoolean(
                    R.styleable.ExpandableLayout_el_expanded,
                    false
                )
            ) 1f else 0f
            orientation = a.getInt(
                R.styleable.ExpandableLayout_android_orientation,
                ExpandableConfig.VERTICAL
            )
            parallax = a.getFloat(R.styleable.ExpandableLayout_el_parallax, 1f)
            a.recycle()
            state =
                if (expansion == 0f) ExpandableConfig.COLLAPSED else ExpandableConfig.EXPANDED
            setParallax(parallax)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val bundle = Bundle()
        expansion = if (isExpanded()) 1f else 0f
        bundle.putFloat(KEY_EXPANSION, expansion)
        bundle.putParcelable(KEY_SUPER_STATE, superState)
        return bundle
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val bundle = parcelable as Bundle
        expansion = bundle.getFloat(KEY_EXPANSION)
        state =
            if (expansion == 1f) ExpandableConfig.EXPANDED else ExpandableConfig.COLLAPSED
        val superState = bundle.getParcelable<Parcelable>(KEY_SUPER_STATE)
        super.onRestoreInstanceState(superState)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        val size = if (orientation == LinearLayout.HORIZONTAL) width else height
        visibility = if (expansion == 0f && size == 0) GONE else VISIBLE
        val expansionDelta = size - Math.round(size * expansion)
        if (parallax > 0) {
            val parallaxDelta = expansionDelta * parallax
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (orientation == ExpandableConfig.HORIZONTAL) {
                    var direction = -1
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && layoutDirection == LAYOUT_DIRECTION_RTL) {
                        direction = 1
                    }
                    child.translationX = direction * parallaxDelta
                } else {
                    child.translationY = -parallaxDelta
                }
            }
        }
        if (orientation == ExpandableConfig.HORIZONTAL) {
            setMeasuredDimension(width - expansionDelta, height)
        } else {
            setMeasuredDimension(width, height - expansionDelta)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        if (animator != null) {
            animator?.cancel()
        }
        super.onConfigurationChanged(newConfig)
    }

    /**
     * Get expansion state
     *
     * @return one of [State]
     */
    fun getState(): Int {
        return state
    }

    fun isExpanded(): Boolean {
        return state == ExpandableConfig.EXPANDING || state == ExpandableConfig.EXPANDED
    }

    fun toggle() {
        toggle(true)
    }

    fun toggle(animate: Boolean) {
        if (isExpanded()) {
            collapse(animate)
        } else {
            expand(animate)
        }
    }

    fun expand() {
        expand(true)
    }

    fun expand(animate: Boolean) {
        setExpanded(true, animate)
    }

    fun collapse() {
        collapse(true)
    }

    fun collapse(animate: Boolean) {
        setExpanded(false, animate)
    }

    /**
     * Convenience method - same as calling setExpanded(expanded, true)
     */
    fun setExpanded(expand: Boolean) {
        setExpanded(expand, true)
    }

    fun setExpanded(expand: Boolean, animate: Boolean) {
        if (expand == isExpanded()) {
            return
        }
        val targetExpansion = if (expand) 1 else 0
        if (animate) {
            animateSize(targetExpansion)
        } else {
            setExpansion(targetExpansion.toFloat())
        }
    }

    fun setExpansion(expansion: Float) {
        if (this.expansion == expansion) {
            return
        }

        // Infer state from previous value
        val delta = expansion - this.expansion
        if (expansion == 0f) {
            state = ExpandableConfig.COLLAPSED
        } else if (expansion == 1f) {
            state = ExpandableConfig.EXPANDED
        } else if (delta < 0) {
            state = ExpandableConfig.COLLAPSING
        } else if (delta > 0) {
            state = ExpandableConfig.EXPANDING
        }
        visibility = if (state == ExpandableConfig.COLLAPSED) GONE else VISIBLE
        this.expansion = expansion
        requestLayout()
        mOnExpansionUpdateListener?.onExpansionUpdate(expansion, state)
    }

    fun getParallax(): Float {
        return parallax
    }

    fun setParallax(parallax: Float) {
        // Make sure parallax is between 0 and 1
        var parallax = parallax
        parallax = Math.min(1f, Math.max(0f, parallax))
        this.parallax = parallax
    }

    fun getOrientation(): Int {
        return orientation
    }

    fun setOrientation(orientation: Int) {
        require(!(orientation < 0 || orientation > 1)) { "Orientation must be either 0 (horizontal) or 1 (vertical)" }
        this.orientation = orientation
    }

    private fun animateSize(targetExpansion: Int) {
        if (animator != null) {
            animator?.cancel()
            animator = null
        }
        animator = ValueAnimator.ofFloat(expansion, targetExpansion.toFloat())
        animator?.setInterpolator(interpolator)
        animator?.setDuration(duration.toLong())
        animator?.addUpdateListener(AnimatorUpdateListener { valueAnimator ->
            setExpansion(
                valueAnimator.animatedValue as Float
            )
        })
        animator?.addListener(ExpansionListener(targetExpansion))
        animator?.start()
    }

    interface OnExpansionUpdateListener {
        /**
         * Callback for expansion updates
         *
         * @param expansionFraction Value between 0 (collapsed) and 1 (expanded) representing the the expansion progress
         * @param state             One of [State] repesenting the current expansion state
         */
        fun onExpansionUpdate(expansionFraction: Float, state: Int)
    }

    inner class ExpansionListener(private val targetExpansion: Int) : Animator.AnimatorListener {
        private var canceled = false
        override fun onAnimationStart(animation: Animator) {
            state =
                if (targetExpansion == 0) ExpandableConfig.COLLAPSING else ExpandableConfig.EXPANDING
        }

        override fun onAnimationEnd(animation: Animator) {
            if (!canceled) {
                state =
                    if (targetExpansion == 0) ExpandableConfig.COLLAPSED else ExpandableConfig.EXPANDED
                setExpansion(targetExpansion.toFloat())
            }
        }

        override fun onAnimationCancel(animation: Animator) {
            canceled = true
        }

        override fun onAnimationRepeat(animation: Animator) {}
    }
}