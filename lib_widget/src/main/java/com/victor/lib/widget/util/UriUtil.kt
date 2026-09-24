package com.victor.lib.widget.util

import android.net.Uri

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UriUtil
 * Author: Victor
 * Date: 2024/05/28 16:18
 * Description: 
 * -----------------------------------------------------------------
 */

object UriUtil {
    fun getParm(url: String?,key: String?): String {
        var value = ""
        try {
            val uri = Uri.parse(url)
            value = uri.getQueryParameter(key) ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return value
    }
}