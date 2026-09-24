package com.victor.lib.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.SpannableString
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.victor.lib.widget.util.SpannableUtil

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RequiredTextView
 * Author: Victor
 * Date: 2020/12/17 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class RequiredTextView :AppCompatTextView {
    private var prefix: String? = "*"
    private var suffix: String? = "*"
    private var prefixColor: Int = Color.RED
    private var suffixColor: Int = Color.RED

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs,defStyle) {
        initAttr(context,attrs)
    }

    private fun initAttr(
        context: Context,
       attrs: AttributeSet?
    ) {
        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RequiredTextView)
        prefix = ta.getString(R.styleable.RequiredTextView_prefix)
        suffix = ta.getString(R.styleable.RequiredTextView_suffix)
        prefixColor = ta.getInteger(R.styleable.RequiredTextView_prefix_color, Color.RED)
        suffixColor = ta.getInteger(R.styleable.RequiredTextView_suffix_color, Color.RED)
        var text: String? = ta.getString(R.styleable.RequiredTextView_android_text)
        if (TextUtils.isEmpty(prefix)) {
            prefix = "*"
        }
        if (!TextUtils.isEmpty(suffix)) {
            prefix = ""
            suffix = "*"
        }
        if (TextUtils.isEmpty(text)) {
            text = ""
        }
        ta.recycle()
        setTextValue(text!!)
    }

    fun setTextValue(text: String?) {
        try {
            val content = SpannableString("${prefix ?: ""}$text${suffix ?:""}")
            if (!TextUtils.isEmpty(prefix)) {
                val textSpan = SpannableUtil.getSpannableColorText(content,prefix,prefixColor)
                setText(textSpan)
            }
            if (!TextUtils.isEmpty(suffix)) {
                val textSpan = SpannableUtil.getSpannableColorText(content,suffix,suffixColor)
                setText(textSpan)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}