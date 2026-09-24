package com.victor.lib.widget.etfilter

import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PositiveIntegerFilter
 * Author: Victor
 * Date: 2026/9/8 15:45
 * Description: 
 * -----------------------------------------------------------------
 */

class PositiveIntegerFilter : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        // 如果输入为空，不做限制
        if (TextUtils.isEmpty(source)) {
            return null
        }

        // 构建新字符串：原有内容 + 即将输入的内容
        val oldContent = dest.toString()
        val newContent = oldContent.substring(0, dstart) + source.toString() + oldContent.substring(dend)

        // 如果新内容为空，允许（用户清空输入框）
        if (TextUtils.isEmpty(newContent)) {
            return null
        }

        return try {
            val value = newContent.toInt()
            if (value > 0) {
                // 合法正整数，接受输入
                null
            } else {
                // 值为 0 或负数，拒绝
                ""
            }
        } catch (e: NumberFormatException) {
            // 包含非数字字符，拒绝
            ""
        }
    }
}