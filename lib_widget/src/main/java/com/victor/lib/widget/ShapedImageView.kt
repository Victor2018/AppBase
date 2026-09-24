package com.victor.lib.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.core.content.withStyledAttributes

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ShapedImageView
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

@SuppressLint("AppCompatCustomView")
class ShapedImageView: ImageView {

    companion object {
        const val SHAPE_MODE_ROUND_RECT = 1
        const val SHAPE_MODE_CIRCLE = 2
        const val SHAPE_MODE_ROUND_RECT_TOP = 3
        const val SHAPE_MODE_ROUND_RECT_BOTTOM = 4
        const val SHAPE_MODE_ROUND_RECT_LEFT = 5
        const val SHAPE_MODE_ROUND_RECT_RIGHT = 6
    }

    var mShapeMode = SHAPE_MODE_ROUND_RECT
    private var mRadius = 0f
    private var mStrokeColor = 0x26000000
    private var mStrokeWidth = 0f
    private var mShapeChanged = false
    private var mFillColor = 0

    private var mPath: Path? = null
    private var mShape: Shape? = null
    private  var mStrokeShape:Shape? = null
    private var mPaint: Paint? = null
    private  var mStrokePaint:Paint? = null
    private  var mPathPaint:Paint? = null
    private var mShapeBitmap: Bitmap? = null
    private  var mStrokeBitmap:Bitmap? = null

    private var mExtension: PathExtension? = null

    private val DST_IN: PorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    private val DST_OUT: PorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttr(context,attrs)
    }

    private fun initAttr(context: Context,attrs: AttributeSet?) {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        if (attrs != null) {
            context.withStyledAttributes(attrs, R.styleable.ShapedImageView) {
                mShapeMode =
                    getInt(R.styleable.ShapedImageView_shape_mode, SHAPE_MODE_ROUND_RECT)
                mRadius = getDimension(R.styleable.ShapedImageView_round_radius, 0f)
                mStrokeWidth = getDimension(R.styleable.ShapedImageView_stroke_width, 0f)
                mStrokeColor = getColor(R.styleable.ShapedImageView_stroke_color, mStrokeColor)
                mFillColor = getColor(R.styleable.ShapedImageView_fill_color, 0)
            }
        }
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.isFilterBitmap = true
        mPaint?.color = Color.BLACK
        mPaint?.xfermode = DST_IN
        mStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mStrokePaint?.isFilterBitmap = true
        mStrokePaint?.color = Color.BLACK
        mPathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPathPaint?.isFilterBitmap = true
        mPathPaint?.color = Color.BLACK
        mPathPaint?.xfermode = DST_OUT
        mPath = Path()
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed || mShapeChanged) {
            mShapeChanged = false
            val width = measuredWidth
            val height = measuredHeight
            when (mShapeMode) {
                SHAPE_MODE_ROUND_RECT -> {
                }
                SHAPE_MODE_CIRCLE -> {
                    val min = Math.min(width, height)
                    mRadius = min.toFloat() / 2
                }
            }
            if (mShape == null || mRadius != 0f) {
//                val radius = FloatArray(8)
//                Arrays.fill(radius, mRadius)

                var lt = mRadius//左上角
                var rt = mRadius//右上角
                var rb = mRadius//右下角
                var lb = mRadius//左下角
                if (mShapeMode == SHAPE_MODE_ROUND_RECT_TOP) {
                    lb = 0f
                    rb = 0f
                } else if (mShapeMode == SHAPE_MODE_ROUND_RECT_BOTTOM) {
                    lt = 0f
                    rt = 0f
                } else if (mShapeMode == SHAPE_MODE_ROUND_RECT_LEFT) {
                    rt = 0f
                    rb = 0f
                } else if (mShapeMode == SHAPE_MODE_ROUND_RECT_RIGHT) {
                    lt = 0f
                    lb = 0f
                }
                val radiusArray = floatArrayOf(lt, lt, rt, rt, rb, rb, lb, lb)

                mShape = RoundRectShape(radiusArray, null, null)
                mStrokeShape = RoundRectShape(radiusArray, null, null)
            }
            mShape?.resize(width.toFloat(), height.toFloat())
            mStrokeShape?.resize(width - mStrokeWidth * 2, height - mStrokeWidth * 2)
            makeStrokeBitmap()
            makeShapeBitmap()
            mExtension?.onLayout(mPath, width, height)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val saveLayers = canvas.saveLayer(
            0f,
            0f,
            measuredWidth.toFloat(),
            measuredHeight.toFloat(),
            null,
            Canvas.ALL_SAVE_FLAG
        )
        try {
            if (mFillColor != 0) {
                val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                fillPaint.color = mFillColor
                canvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), fillPaint)
            }
            super.onDraw(canvas)
        } catch (throwable: Throwable) {
        }
        if (mStrokeWidth > 0 && mStrokeShape != null) {
            if (mStrokeBitmap == null || mStrokeBitmap!!.isRecycled) {
                makeStrokeBitmap()
            }
            val i = canvas.saveLayer(
                0f,
                0f,
                measuredWidth.toFloat(),
                measuredHeight.toFloat(),
                null,
                Canvas.ALL_SAVE_FLAG
            )
            mStrokePaint?.xfermode = null
            if (mStrokeBitmap != null && !mStrokeBitmap!!.isRecycled) {
                canvas.drawBitmap(mStrokeBitmap!!, 0f, 0f, mStrokePaint)
            }
            canvas.translate(mStrokeWidth, mStrokeWidth)
            mStrokePaint?.xfermode = DST_OUT
            mStrokeShape?.draw(canvas, mStrokePaint)
            canvas.restoreToCount(i)
        }
        if (mExtension != null) {
            canvas.drawPath(mPath!!, mPathPaint!!)
        }
        when (mShapeMode) {
            SHAPE_MODE_ROUND_RECT, SHAPE_MODE_CIRCLE,
            SHAPE_MODE_ROUND_RECT_TOP, SHAPE_MODE_ROUND_RECT_BOTTOM,
            SHAPE_MODE_ROUND_RECT_LEFT, SHAPE_MODE_ROUND_RECT_RIGHT -> {
                if (mShapeBitmap == null || mShapeBitmap?.isRecycled!!) {
                    makeShapeBitmap()
                }
                if (mShapeBitmap != null && !mShapeBitmap!!.isRecycled) {
                    canvas.drawBitmap(mShapeBitmap!!, 0f, 0f, mPaint)
                }
            }
        }
        canvas.restoreToCount(saveLayers)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        releaseBitmap(mShapeBitmap)
        releaseBitmap(mStrokeBitmap)
    }

    private fun makeStrokeBitmap() {
        if (mStrokeWidth <= 0) return
        val w = measuredWidth
        val h = measuredHeight
        if (w == 0 || h == 0) return
        releaseBitmap(mStrokeBitmap)
        mStrokeBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(mStrokeBitmap!!)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = mStrokeColor
        c.drawRect(RectF(0f, 0f, w.toFloat(), h.toFloat()), p)
    }

    private fun releaseBitmap(bitmap: Bitmap?) {
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle()
        }
    }

    private fun makeShapeBitmap() {
        val w = measuredWidth
        val h = measuredHeight
        if (w == 0 || h == 0) return
        releaseBitmap(mShapeBitmap)
        mShapeBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(mShapeBitmap!!)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = Color.RED
        mShape?.draw(c, p)
    }

    fun setExtension(extension: PathExtension) {
        mExtension = extension
        requestLayout()
    }

    fun setStroke(strokeColor: Int, strokeWidth: Float) {
        if (mStrokeWidth <= 0) return
        if (mStrokeWidth != strokeWidth) {
            mStrokeWidth = strokeWidth
            val width = measuredWidth
            val height = measuredHeight
            mStrokeShape!!.resize(width - mStrokeWidth * 2, height - mStrokeWidth * 2)
            postInvalidate()
        }
        if (mStrokeColor != strokeColor) {
            mStrokeColor = strokeColor
            makeStrokeBitmap()
            postInvalidate()
        }
    }

    fun setStrokeColor(strokeColor: Int) {
        setStroke(strokeColor, mStrokeWidth)
    }

    fun setStrokeWidth(strokeWidth: Float) {
        setStroke(mStrokeColor, strokeWidth)
    }

    fun setShape(shapeMode: Int, radius: Float) {
        mShapeChanged = mShapeMode != shapeMode || mRadius != radius
        if (mShapeChanged) {
            mShapeMode = shapeMode
            mRadius = radius
            mShape = null
            mStrokeShape = null
            requestLayout()
        }
    }

    fun setShapeMode(shapeMode: Int) {
        setShape(shapeMode, mRadius)
    }

    fun setShapeRadius(radius: Float) {
        setShape(mShapeMode, radius)
    }

    interface PathExtension {
        fun onLayout(path: Path?, width: Int, height: Int)
    }
}