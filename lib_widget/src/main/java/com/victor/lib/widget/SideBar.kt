package com.victor.lib.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.victor.lib.widget.util.DensityUtil
import com.victor.lib.widget.util.ResUtils

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SideBar
 * Author: Victor
 * Date: 2022/4/1 14:38
 * Description: 
 * -----------------------------------------------------------------
 */

class SideBar : View {
    // 触摸事件
    var onTouchingLetterChangedListener: OnTouchingLetterChangedListener? = null

    // 26个字母
    private var initials = arrayOf(
        "定","热","A", "B", "C", "D", "E", "F", "G", "H", "I",
        "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
        "W", "X", "Y", "Z"
    )
    var choose = -1 // 选中

    var paint = Paint()

    var mTextDialog: TextView? = null

    var mContext: Context? = null

    /**
     * 为SideBar设置显示字母的TextView
     *
     * @param mTextDialog
     */
    fun setTextView(mTextDialog: TextView?) {
        this.mTextDialog = mTextDialog
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mContext = context
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 获取焦点改变背景颜色.
        // 获取焦点改变背景颜色.
        val height = height // 获取对应高度

        val width = width // 获取对应宽度

        val singleHeight: Int = height / initials.size // 获取每一个字母的高度

        for (i in initials.indices) {
            paint.color = ResUtils.getColorRes(context,R.color.color_333333)
            paint.color = Color.BLACK
            paint.typeface = Typeface.DEFAULT
            paint.isAntiAlias = true
            paint.textSize = DensityUtil.dip2px(mContext!!, 12f).toFloat()
            // 选中的状态
            if (i == choose) {
                paint.color = resources.getColor(R.color.colorPrimaryDark)
                paint.isFakeBoldText = true
            }
            // x坐标等于中间-字符串宽度的一半.
            val xPos = width / 2 - paint.measureText(initials.get(i)) / 2
            val yPos: Float = (singleHeight * i + singleHeight).toFloat()
            canvas!!.drawText(initials.get(i), xPos, yPos, paint)
            paint.reset() // 重置画笔
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.action
        val y = event?.y ?: 0f // 点击y坐标

        val oldChoose = choose
        val listener: OnTouchingLetterChangedListener? =
            onTouchingLetterChangedListener
        val c = (y / height * initials.size).toInt() // 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.


        when (action) {
            MotionEvent.ACTION_UP -> {
                setBackgroundResource(R.drawable.shape_f6f6f6_radius_8)
                choose = -1 //
                invalidate()
                mTextDialog?.visibility = INVISIBLE
            }
            else -> {
                setBackgroundResource(R.drawable.shape_f6f6f6_radius_8)
                if (oldChoose != c) {
                    if (c >= 0 && c < initials.size) {
                        listener?.onTouchingLetterChanged(initials.get(c))
                        mTextDialog?.text = initials.get(c)
                        mTextDialog?.visibility = VISIBLE
                        choose = c
                        invalidate()
                    }
                }
            }
        }
        return true
    }

    fun setLetters (letters: ArrayList<String>) {
        initials = letters.toTypedArray()
        requestLayout()
    }

    /**
     * 接口
     *
     * @author coder
     */
    interface OnTouchingLetterChangedListener {
        fun onTouchingLetterChanged(s: String?)
    }
}