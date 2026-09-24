package com.victor.lib.widget.util

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.victor.lib.widget.R

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ResUtils.java
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

object ResUtils {
    val TAG = "ResUtils"

    /**
     * Resources#getText() 能解析到字符串中包含的 HTML 标记，并返回一个携带了样式的 CharSequence 对象
     */
    fun getTextRes(context: Context,id: Int): CharSequence? {
        try {
            return getResources(context)?.getText(id)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return ""
        }
    }
    fun getStringRes(context: Context?,id: Int): String? {
        try {
            return getResources(context)?.getString(id)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return ""
        }
    }

    fun getStringRes(context: Context,id: Int, vararg args: Any): String? {
        try {
            return getResources(context)?.getString(id, args)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return ""
        }

    }

    /**
     * 获取 String[] 值. 如果id对应的资源文件不存在, 则返回 null.
     *
     * @param id
     * @return
     */
    fun getStringArrayRes(context: Context,id: Int): Array<String>? {
        try {
            return getResources(context)?.getStringArray(id)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return null
        }

    }
    /**
     * 获取 int[] 值. 如果id对应的资源文件不存在, 则返回 null.
     *
     * @param id
     * @return
     */
    fun getIntArrayRes(context: Context,id: Int): IntArray? {
        try {
            return getResources(context)?.getIntArray(id)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取dimension px值. 如果id对应的资源文件不存在, 则返回 -1.
     *
     * @param id
     * @return
     */
    fun getDimenPixRes(context: Context,id: Int): Int {
        try {
            return getResources(context)?.getDimensionPixelOffset(id) ?: 0
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return -1
        }
    }
    fun getDimenPixResByName(context: Context,resourceName: String): Int {
        return try {
            val resourceId = getResources(context)?.getIdentifier(resourceName,
                "dimen", context.packageName) ?: 0
            if (resourceId > 0) {
                getResources(context)?.getDimensionPixelOffset(resourceId) ?: 0
            } else {
                // 资源未找到
                -1
            }
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            -1
        }
    }

    /**
     * 获取dimension float形式的 px值. 如果id对应的资源文件不存在, 则返回 -1.
     *
     * @param id
     * @return
     */
    fun getDimenFloatPixRes(context: Context,id: Int): Float {
        try {
            return getResources(context)?.getDimension(id) ?: 0f
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return -1f
        }

    }

    /**
     * 获取 color 值. 如果id对应的资源文件不存在, 则返回 -1.
     *
     * @param id
     * @return
     */
    @ColorInt
    fun getColorRes(context: Context?,id: Int): Int {
        try {
            return context?.let { ContextCompat.getColor(it, id) } ?: -1
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return -1
        }

    }

    /**
     * 获取 Drawable 对象. 如果id对应的资源文件不存在, 则返回 null.
     *
     * @param id
     * @return
     */
    fun getDrawableRes(context: Context,id: Int): Drawable? {
        try {
            return ContextCompat.getDrawable(context, id)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return null
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取资源
     *
     * @return
     */
    fun getResources(context: Context?): Resources? {
        return context?.resources
    }

    fun getDrawableByName(context: Context,name: String): Int {
        return getResources(context)?.getIdentifier(name, "mipmap", context.packageName) ?: 0
    }

    /**
     * 根据图片名字获取Id
     */
    fun getDrawableId(name: String): Int {
        try {
            val field = R.drawable::class.java!!.getField(name)
            return field.getInt(field.name)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1
    }
}