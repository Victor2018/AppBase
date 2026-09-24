package com.victor.lib.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.EditText

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MNPasswordEditText
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

/**
 * @author : maning
 * @desc :   验证码和密码的输入框
 */
@SuppressLint("AppCompatCustomView")
class MNPasswordEditText : EditText {
    private val TAG = "MNPasswordEditText"
    private var mContext: Context
    private val defaultColor = "#FF0000"

    /**
     * 长度
     */
    private var mMaxLength: Int = 0

    /**
     * 文字的颜色
     */
    private var textColor: Int = 0

    /**
     * 文字的画笔
     */
    private lateinit var mPaintText: Paint

    /**
     * 线框的画笔
     */
    private lateinit var mPaintLine: Paint

    /**
     * 背景色
     */
    private var backgroundColor: Int = 0

    /**
     * 线框的颜色
     */
    private var borderColor: Int = 0

    /**
     * 线框被选中的颜色
     */
    private var borderSelectedColor: Int = 0

    /**
     * 线框的圆角
     */
    private var borderRadius: Float = 0f

    /**
     * 线框的宽度
     */
    private var borderWidth: Float = 0f

    /**
     * 密码框的间隔
     */
    private var itemMargin: Float = 0f

    /**
     * 输入的类型
     */
    private var inputMode: Int = 0

    /**
     * 样式
     */
    private var editTextStyle: Int = 0

    /**
     * 文字遮盖
     */
    private var coverText: String? = null

    /**
     * 图片遮盖
     */
    private var coverBitmapID: Int = 0

    /**
     * 图片宽度
     */
    private var coverBitmapWidth: Float = 0f

    /**
     * 圆形遮盖的颜色
     */
    private var coverCirclrColor: Int = 0

    /**
     * 圆形遮盖的半径
     */
    private var coverCirclrRadius: Float = 0f

    /**
     * 线框背景
     */
    private val gradientDrawable = GradientDrawable()
    private var coverBitmap: Bitmap? = null

    private lateinit var mPaintCursor: Paint

    /**
     * 光标
     */
    private val cursorDrawable = GradientDrawable()
    private var cursorColor: Int = 0
    private var cursorWidth: Float = 0f
    private var cursorHeight: Float = 0f
    private var cursorCornerRadius: Float = 0f
    private var showCursor: Boolean = false
    private var mCursorFlag: Boolean = false
    private var mBlink: Blink? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context

        // 初始化参数
        initAttrs(attrs, defStyleAttr)

        // 初始化相关
        init()
    }

    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val array = mContext.obtainStyledAttributes(attrs, R.styleable.MNPasswordEditText, defStyleAttr, 0)

        // 背景色
        backgroundColor = array.getColor(R.styleable.MNPasswordEditText_psw_background_color, Color.parseColor("#FFFFFF"))
        // 边框颜色
        borderColor = array.getColor(R.styleable.MNPasswordEditText_psw_border_color, Color.parseColor(defaultColor))
        // 边框选中的颜色
        borderSelectedColor = array.getColor(R.styleable.MNPasswordEditText_psw_border_selected_color, Color.parseColor(defaultColor))
        // 文字的颜色
        textColor = array.getColor(R.styleable.MNPasswordEditText_psw_text_color, Color.parseColor(defaultColor))
        // 边框圆角
        borderRadius = array.getDimension(R.styleable.MNPasswordEditText_psw_border_radius, dip2px(6f))
        // 边框线大小
        borderWidth = array.getDimension(R.styleable.MNPasswordEditText_psw_border_width, dip2px(1f))
        // 每个密码框的间隔
        itemMargin = array.getDimension(R.styleable.MNPasswordEditText_psw_item_margin, dip2px(10f))
        // 输入的模式
        inputMode = array.getInt(R.styleable.MNPasswordEditText_psw_mode, 1)
        // 整体样式
        editTextStyle = array.getInt(R.styleable.MNPasswordEditText_psw_style, 1)
        // 替换的图片
        coverBitmapID = array.getResourceId(R.styleable.MNPasswordEditText_psw_cover_bitmap_id, -1)
        // 替换的文字
        coverText = array.getString(R.styleable.MNPasswordEditText_psw_cover_text)
        if (TextUtils.isEmpty(coverText)) {
            coverText = "密"
        }
        // 圆形的颜色
        coverCirclrColor = array.getColor(R.styleable.MNPasswordEditText_psw_cover_circle_color, Color.parseColor(defaultColor))
        // 密码圆形遮盖半径
        coverCirclrRadius = array.getDimension(R.styleable.MNPasswordEditText_psw_cover_circle_radius, 0f)
        // 密码图片遮盖长宽
        coverBitmapWidth = array.getDimension(R.styleable.MNPasswordEditText_psw_cover_bitmap_width, 0f)

        // --------------光标属性-----
        showCursor = array.getBoolean(R.styleable.MNPasswordEditText_psw_show_cursor, false)
        cursorColor = array.getColor(R.styleable.MNPasswordEditText_psw_cursor_color, borderSelectedColor)
        cursorHeight = array.getDimension(R.styleable.MNPasswordEditText_psw_cursor_height, 0f)
        cursorWidth = array.getDimension(R.styleable.MNPasswordEditText_psw_cursor_width, 6f)
        cursorCornerRadius = array.getDimension(R.styleable.MNPasswordEditText_psw_cursor_corner_radius, 0f)

        // 回收
        array.recycle()
    }

    private fun init() {
        // 最大的长度
        mMaxLength = getMMaxLength()
        // 隐藏光标
        isCursorVisible = false
        // 设置本来文字的颜色为透明
        setTextColor(Color.TRANSPARENT)
        // 触摸获取焦点
        isFocusableInTouchMode = true
        // 屏蔽长按
        setOnLongClickListener { true }

        // 初始化画笔
        // 文字
        mPaintText = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintText.style = Paint.Style.FILL
        mPaintText.color = textColor
        mPaintText.textSize = textSize

        // 线
        mPaintLine = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintLine.style = Paint.Style.STROKE
        mPaintLine.color = borderColor
        mPaintLine.strokeWidth = borderWidth

        // 光标
        cursorDrawable.cornerRadius = cursorCornerRadius
        cursorDrawable.setColor(cursorColor)
        mPaintCursor = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintCursor.style = Paint.Style.FILL
        mPaintCursor.color = cursorColor

        // 遮盖是图片方式，提前加载图片
        if (inputMode == 2) {
            // 判断有没有图片
            if (coverBitmapID == -1) {
                // 抛出异常
                throw NullPointerException("遮盖图片为空")
            } else {
                coverBitmap = BitmapFactory.decodeResource(context.resources, coverBitmapID)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 获取宽高
        val measuredWidth = measuredWidth
        val itemH = measuredHeight.toFloat()
        // 方形框
        var margin = itemMargin
        var itemW = (measuredWidth - margin * (getMMaxLength() - 1)) / getMMaxLength()

        val currentIndex = text.length

        // 判断类型
        if (editTextStyle == 1) {
            // 连体框
            gradientDrawable.setStroke(borderWidth.toInt(), borderColor)
            gradientDrawable.cornerRadius = borderRadius
            gradientDrawable.setColor(backgroundColor)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                // Android系统大于等于API16，使用setBackground
                background = gradientDrawable
            } else {
                // Android系统小于API16，使用setBackgroundDrawable
                setBackgroundDrawable(gradientDrawable)
            }
            margin = 0f
            itemW = measuredWidth.toFloat() / getMMaxLength()
            // 画线
            for (i in 1 until getMMaxLength()) {
                val startX = itemW * i
                val startY = 0f
                val stopX = startX
                val stopY = itemH
                canvas.drawLine(startX, startY, stopX, stopY, mPaintLine)
            }
        } else if (editTextStyle == 2) {
            gradientDrawable.setStroke(borderWidth.toInt(), borderColor)
            gradientDrawable.cornerRadius = borderRadius
            gradientDrawable.setColor(backgroundColor)
            val bitmap = drawableToBitmap(gradientDrawable, itemW.toInt(), itemH.toInt())
            var bitmapSelected: Bitmap? = null
            if (borderSelectedColor != 0) {
                gradientDrawable.setStroke(borderWidth.toInt(), borderSelectedColor)
                bitmapSelected = drawableToBitmap(gradientDrawable, itemW.toInt(), itemH.toInt())
            }
            // 画每个Item背景
            for (i in 0 until getMMaxLength()) {
                val left = itemW * i + margin * i
                val top = 0f
                if (bitmapSelected == null) {
                    canvas.drawBitmap(bitmap, left, top, mPaintLine)
                } else {
                    if (currentIndex == i) {
                        // 选中是另外的颜色
                        canvas.drawBitmap(bitmapSelected, left, top, mPaintLine)
                    } else {
                        canvas.drawBitmap(bitmap, left, top, mPaintLine)
                    }
                }
            }
        } else if (editTextStyle == 3) {
            // 下划线格式
            for (i in 0 until getMMaxLength()) {
                if (borderSelectedColor != 0) {
                    if (currentIndex == i) {
                        // 选中是另外的颜色
                        mPaintLine.color = borderSelectedColor
                    } else {
                        mPaintLine.color = borderColor
                    }
                } else {
                    mPaintLine.color = borderColor
                }
                val startX = itemW * i + itemMargin * i
                val startY = itemH - borderWidth
                val stopX = startX + itemW
                val stopY = startY
                canvas.drawLine(startX, startY, stopX, stopY, mPaintLine)
            }
        }

        // 写文字
        val currentText = text.toString()
        for (i in 0 until getMMaxLength()) {
            if (!TextUtils.isEmpty(currentText) && i < currentText.length) {
                // <!--密码框输入的模式:1.圆形，2.图片，3.文字，4.明文-->
                when (inputMode) {
                    1 -> {
                        // 圆点半径
                        var circleRadius = itemW * 0.5f * 0.5f
                        if (circleRadius > itemH / 2f) {
                            circleRadius = itemH * 0.5f * 0.5f
                        }
                        if (coverCirclrRadius > 0) {
                            circleRadius = coverCirclrRadius
                        }
                        val startX = itemW / 2f + itemW * i + margin * i
                        val startY = itemH / 2.0f
                        mPaintText.color = coverCirclrColor
                        canvas.drawCircle(startX, startY, circleRadius, mPaintText)
                    }
                    2 -> {
                        var picW = itemW * 0.5f
                        if (coverBitmapWidth > 0) {
                            picW = coverBitmapWidth
                        }
                        val startX = (itemW - picW) / 2f + itemW * i + margin * i
                        val startY = (itemH - picW) / 2f
                        val bitmap = Bitmap.createScaledBitmap(coverBitmap!!, picW.toInt(), picW.toInt(), true)
                        canvas.drawBitmap(bitmap, startX, startY, mPaintText)
                    }
                    3 -> {
                        val fontWidth = getFontWidth(mPaintText, coverText!!)
                        val fontHeight = getFontHeight(mPaintText, coverText!!)
                        val startX = (itemW - fontWidth) / 2f + itemW * i + margin * i
                        val startY = (itemH + fontHeight) / 2f - 6
                        mPaintText.color = textColor
                        canvas.drawText(coverText!!, startX, startY, mPaintText)
                    }
                    else -> {
                        val strPosition = currentText[i].toString()
                        val fontWidth = getFontWidth(mPaintText, strPosition)
                        val fontHeight = getFontHeight(mPaintText, strPosition)
                        val startX = (itemW - fontWidth) / 2f + itemW * i + margin * i
                        val startY = (itemH + fontHeight) / 2f
                        mPaintText.color = textColor
                        canvas.drawText(strPosition, startX, startY, mPaintText)
                    }
                }
            }
        }

        if (showCursor && mCursorFlag) {
            if (cursorHeight == 0f || cursorHeight > itemH) {
                cursorHeight = itemH * 50 / 100
            }
            val bitmap = drawableToBitmap(cursorDrawable, cursorWidth.toInt(), cursorHeight.toInt())
            val cursorLeft = (itemW + margin) * currentIndex + itemW / 2 - cursorWidth / 2
            val cursorTop = (itemH - cursorHeight) / 2
            canvas.drawBitmap(bitmap, cursorLeft, cursorTop, mPaintCursor)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        resumeBlink()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        suspendBlink()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            mBlink?.uncancel()
            makeBlink()
        } else {
            mBlink?.cancel()
        }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            mBlink?.uncancel()
            makeBlink()
        } else {
            mBlink?.cancel()
        }
    }

    private fun resumeBlink() {
        mBlink?.uncancel()
        makeBlink()
    }

    private fun suspendBlink() {
        mBlink?.cancel()
    }

    private fun makeBlink() {
        if (true) {
            if (mBlink == null) mBlink = Blink()
            removeCallbacks(mBlink)
            postDelayed(mBlink, 500)
        } else {
            mBlink?.let { removeCallbacks(it) }
        }
    }

    private inner class Blink : Runnable {
        private var mCancelled = false

        override fun run() {
            mCursorFlag = !mCursorFlag
            invalidate()
            if (mCancelled) {
                return
            }
            // 每个500毫秒刷新一次
            postDelayed(this, 500)
        }

        fun cancel() {
            if (!mCancelled) {
                removeCallbacks(this)
                mCancelled = true
            }
        }

        fun uncancel() {
            mCancelled = false
        }
    }

    companion object {
        fun drawableToBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
            val bitmap = Bitmap.createBitmap(
                width,
                height,
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, width, height)
            drawable.draw(canvas)
            return bitmap
        }
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        // 刷新界面
        invalidate()
        onTextChangeListener?.let { listener ->
            if (this.text.toString().length == getMMaxLength()) {
                listener.onTextChange(this.text.toString(), true)
            } else {
                listener.onTextChange(this.text.toString(), false)
            }
        }
    }

    fun getFontWidth(paint: Paint, str: String): Float {
        val rect = Rect()
        paint.getTextBounds(str, 0, str.length, rect)
        return rect.width().toFloat()
    }

    fun getFontHeight(paint: Paint, str: String): Float {
        val rect = Rect()
        paint.getTextBounds(str, 0, str.length, rect)
        return rect.height().toFloat()
    }

    fun getMMaxLength(): Int {
        var length = 0
        try {
            val inputFilters = filters
            for (filter in inputFilters) {
                val c = filter.javaClass
                if (c.name == "android.text.InputFilter\$LengthFilter") {
                    val f = c.declaredFields
                    for (field in f) {
                        if (field.name == "mMax") {
                            field.isAccessible = true
                            length = field[filter] as Int
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
            return length
        }

    private fun dip2px(dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dpValue * scale + 0.5f
    }

    private var onTextChangeListener: OnTextChangeListener? = null

    fun setOnTextChangeListener(onTextChangeListener: OnTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener
    }

    interface OnTextChangeListener {
        /**
         * 监听输入变化
         *
         * @param text       当前的文案
         * @param isComplete 是不是完成输入
         */
        fun onTextChange(text: String, isComplete: Boolean)
    }
}