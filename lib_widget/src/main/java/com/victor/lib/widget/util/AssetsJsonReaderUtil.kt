package com.victor.lib.widget.util

import com.victor.lib.widget.module.WidgetModule
import java.io.ByteArrayOutputStream
import java.io.IOException

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AssetsJsonReaderUtil
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

object AssetsJsonReaderUtil {
    fun getJsonString(fileName: String?): String? {
        val baos = ByteArrayOutputStream()
        try {
            val assetManager = WidgetModule.instance.mApplication?.assets
            val inputStream = assetManager?.open(fileName!!)
            val buffer = ByteArray(1024)
            var size = inputStream?.read(buffer) ?: 0
            while (size != -1) {
                baos.write(buffer, 0, size)
                size = inputStream?.read(buffer) ?: 0
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                baos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Loger.e("AssetsJsonReaderUtil","baos.toString() = ${baos.toString()}")
        return baos.toString()
    }
}