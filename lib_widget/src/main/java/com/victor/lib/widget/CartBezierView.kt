package com.victor.lib.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.victor.lib.widget.util.BitmapUtil
import com.victor.lib.widget.util.DensityUtil

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CartBezierView
 * Author: Victor
 * Date: 2022/8/12 10:38
 * Description: 
 * -----------------------------------------------------------------
 */

class CartBezierView : View,ValueAnimator.AnimatorUpdateListener {
    val bezierAnimDuration = 500

    var mMeasureWidth = 0
    var mWidthMode = 0
    var mMeasureHeight = 0
    var mHeightMode = 0

    var startPosition = Point()
    var endPosition = Point()
    var ctrlPosition = Point()
    var mIconResId: Int = 0
    var mBitmap: Bitmap? = null
    var animatedFraction = 1.0f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        mHeightMode = MeasureSpec.getMode(heightMeasureSpec)
        mMeasureWidth = MeasureSpec.getSize(widthMeasureSpec)
        mMeasureHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (mWidthMode == MeasureSpec.AT_MOST && mHeightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(
                context.resources.getDimension(com.victor.screen.match.library.R.dimen.dp_50)
                    .toInt(),
                context.resources.getDimension(com.victor.screen.match.library.R.dimen.dp_50)
                    .toInt()
            )
        } else if (mWidthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(
                context.resources.getDimension(com.victor.screen.match.library.R.dimen.dp_50)
                    .toInt(), mMeasureHeight
            )
        } else if (mHeightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(
                mMeasureWidth,
                context.resources.getDimension(com.victor.screen.match.library.R.dimen.dp_50)
                    .toInt()
            )
        } else {
            setMeasuredDimension(mMeasureWidth, mMeasureHeight)
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mBitmap != null) {
//            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_square_gift),new Matrix(),new Paint());

            //缩小动画
            val src_w = mBitmap!!.width
            val src_h = mBitmap!!.height
            val scale_w =
                context.resources.getDimension(com.victor.screen.match.library.R.dimen.dp_50) * animatedFraction / src_w
            val scale_h =
                context.resources.getDimension(com.victor.screen.match.library.R.dimen.dp_50) * animatedFraction / src_h
            val matrix = Matrix()
            matrix.postScale(scale_w, scale_h)
            canvas!!.drawBitmap(mBitmap!!, matrix, Paint())
        }
    }

    /**
     * 设置开始点坐标
     */
    fun setStartPoint(x: Int,y: Int) {
        startPosition.x = x
        startPosition.y = y
    }

    /**
     * 设置结束点坐标
     * @param x
     * @param y
     */
    fun setEndPoint(x: Int,y: Int) {
        endPosition.x = x
        endPosition.y = y
    }

    /**
     * 设置控制点坐标
     * @param x
     * @param y
     */
    fun setCtrlPoint(x: Int,y: Int) {
        ctrlPosition.x = x
        ctrlPosition.y = y
    }

    /**
     * 开始动画
     */
    fun startAnimation() {
        if (startPosition == null || endPosition == null) {
            return
        }

        //设置控制点
        val pointY = (startPosition.y - DensityUtil.dip2px(
            context,
            context.resources.getDimension(com.victor.screen.match.library.R.dimen.dp_60)
        ))

        //设置值动画
        val valueAnimator =
            ValueAnimator.ofObject(BezierEvaluator(ctrlPosition), startPosition, endPosition)
        valueAnimator.duration = bezierAnimDuration.toLong()
        //先加速再减速
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener(this)
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                val viewGroup = parent as ViewGroup
                viewGroup.removeView(this@CartBezierView)
//                postEvent(HomeActions.ADD_TO_CART)
            }
        })
        valueAnimator.start()
    }

    fun setBitmap(bitmap: Bitmap) {
        val width =
            context.resources.getDimension(com.victor.screen.match.library.R.dimen.dp_50).toInt()
        val height =
            context.resources.getDimension(com.victor.screen.match.library.R.dimen.dp_50).toInt()
        val bm = imageScale(bitmap, width, height)
        mBitmap = BitmapUtil.circleBitmap(bm)
    }

    fun setIconResId(iconResId: Int) {
        mIconResId = iconResId
        setBitmap(BitmapFactory.decodeResource(context.resources, iconResId))
    }

    /**
     * 调整图片大小
     *
     * @param bitmap 源
     * @param dst_w  输出宽度
     * @param dst_h  输出高度
     * @return
     */
    fun imageScale(bitmap: Bitmap, dst_w: Int, dst_h: Int): Bitmap {
        val src_w = bitmap.width
        val src_h = bitmap.height
        val scale_w = dst_w.toFloat() / src_w
        val scale_h = dst_h.toFloat() / src_h
        val matrix = Matrix()
        matrix.postScale(scale_w, scale_h)
        return Bitmap.createBitmap(
            bitmap, 0, 0, src_w, src_h, matrix,
            true
        )
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        val point = animation!!.animatedValue as Point
        x = point.x.toFloat()
        y = point.y.toFloat()

        //getAnimatedFraction()就是mBezierEvalutor估值器对象中evaluate方法t即时间因子,
        // 从0~1变化,所以爱心透明度应该是从1~0变化正好到了顶部，t变为1，透明度变为0，即爱心消失
        if (animation!!.animatedFraction < 0.5f) {
            alpha = 1 - animation.animatedFraction
        }

        /*AnimatorSet animatorSetsuofang = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 1 - animation.getAnimatedFraction());
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 1 - animation.getAnimatedFraction());

        animatorSetsuofang.setDuration(10);
        animatorSetsuofang.setInterpolator(new DecelerateInterpolator());
        animatorSetsuofang.play(scaleX).with(scaleY);//两个动画同时开始
        animatorSetsuofang.start();*/


        if (animation!!.animatedFraction < 0.3f) {
            animatedFraction = 1 - animation!!.animatedFraction
        }
        invalidate()
    }

    class BezierEvaluator(private val controllPoint: Point) :
        TypeEvaluator<Point> {
        override fun evaluate(t: Float, startValue: Point, endValue: Point): Point {

            //一阶Bezier曲线
//            var x = ((1 - t) * startValue.x + t * endValue.x).toInt()
//            var y = ((1 - t) * startValue.y + t * endValue.y).toInt()

            //二阶Bezier曲线
            var x =
                ((1 - t) * (1 - t) * startValue.x + 2 * t * (1 - t) * controllPoint.x + t * t * endValue.x).toInt()
            var y =
                ((1 - t) * (1 - t) * startValue.y + 2 * t * (1 - t) * controllPoint.y + t * t * endValue.y).toInt()

            return Point(x, y)
        }
    }
}