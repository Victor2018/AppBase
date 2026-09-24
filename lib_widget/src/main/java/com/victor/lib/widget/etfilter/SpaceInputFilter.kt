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
 * Description: 自动剔除空格输入
 * -----------------------------------------------------------------
 */

class SpaceInputFilter: InputFilter {

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
        return null
    }
}