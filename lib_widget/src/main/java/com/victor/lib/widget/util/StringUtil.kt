package com.victor.lib.widget.util

import android.text.TextUtils
import java.lang.Exception

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: StringUtil
 * Author: Victor
 * Date: 2026/4/16 17:12
 * Description: 
 * -----------------------------------------------------------------
 */

object StringUtil {
    /**
     * 拼接分类路径（列表形式）
     * @param categories 分类名称列表
     * @param separator 分隔符，默认为 ">"
     * @return 拼接后的路径，过滤掉空白字符串
     */
    fun join(categories: List<String?>?, separator: String = ">"): String {
        return categories
            ?.filterNotNull()
            ?.filter { it.isNotBlank() }
            ?.joinToString(separator) ?: ""
    }

    fun String.formatDouble(): Double {
        return try {
            if (TextUtils.isEmpty(this)) {
                0.0
            } else {
                this.replace(",","").toDouble()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return 0.0
        }
    }

    fun String.formatInt(): Int {
        return try {
            if (TextUtils.isEmpty(this)) {
                0
            } else {
                this.replace(",","").toInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }
}