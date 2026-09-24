package com.victor.lib.widget.etfilter

import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SpaceInputFilter
 * Author: Victor
 * Date: 2022/9/6 14:12
 * Description: 只允许输入中文
 * -----------------------------------------------------------------
 */

class ChineseInputFilter: InputFilter {
    private val pattern = Regex("[\u4e00-\u9fa5]+")

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        // 判断是否是空格
        if (TextUtils.equals(source," ")){
            return ""
        }
        // 获取本次输入的内容
        val input = source ?: ""

        // 如果输入内容匹配纯中文正则，返回 null 表示保留
        // 否则返回空字符串，表示过滤掉该输入
        return if (pattern.matches(input)) null else ""
    }
}