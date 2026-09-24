package com.victor.lib.widget.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ViewUtils
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

object ViewUtils {
    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun getViewByLayout (context: Context?,layoutId: Int): View {
        val inflater = LayoutInflater.from(context)
        return inflater.inflate(layoutId, null)
    }

    fun getViewByLayout (inflater: LayoutInflater,layoutId: Int): View {
        return inflater.inflate(layoutId, null)
    }

    fun View.bitmap(): Bitmap {
        //不加下面两句，会报错：width and height must be > 0
        measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

        layout(0, 0, measuredWidth, measuredHeight)

        val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    fun View.setSafeClick(delay: Long = 500L, action: (View) -> Unit) {
        var isClickable = true
        setOnClickListener {
            if (isClickable) {
                isClickable = false
                action(it)
                Log.e("TAG","setSafeClick-action")
                postDelayed({ isClickable = true }, delay)
            }
        }
    }

    fun GradientDrawable.setCornerAndColor(
        color: Int,
        radius: Float = 0f
    ): GradientDrawable {
        shape = GradientDrawable.RECTANGLE
        setColor(color)
        if (radius > 0) {
            cornerRadius = radius
        }
        return this
    }
}