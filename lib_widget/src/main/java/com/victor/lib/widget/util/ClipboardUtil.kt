package com.victor.lib.widget.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ClipboardUtil
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

object ClipboardUtil {
    fun copy (context: Context?,copyKey: String?,copyValue: CharSequence?) {
        //获取剪贴板管理器：
        val cm = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 创建普通字符型ClipData
        val mClipData: ClipData = ClipData.newPlainText(copyKey, copyValue)
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData)
    }
}