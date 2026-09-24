package com.victor.lib.widget.etfilter

import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import com.victor.lib.widget.util.StringUtil.formatDouble
import java.util.regex.Matcher
import java.util.regex.Pattern


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MoneyInputFilter
 * Author: Victor
 * Date: 2021/2/2 10:16
 * Description: 输入金额时的文本过虑器，可设置最大输入金额、小数点后位数
 * -----------------------------------------------------------------
 */
class MoneyInputFilter: InputFilter {
    val TAG = "MoneyInputFilter"
    /**
     * 正则表达式：支持整数、0.xx、正整数.xx(小数点后面跟0到2位数字)
     */
//    private val FORMAT = "^(0|[1-9]\\d*)(\\.\\d{0,%s})?$"
    private val FORMAT = "^(0|[1-9]\\d*)?(\\.\\d{0,%s})?$"

    /**
     * 正则表达式：0-9.之外的字符
     */
    private val SOURCE_PATTERN: Pattern = Pattern.compile("[^0-9.,]")

    /**
     * 默认保留小数点后2位
     */
    private var mPattern: Pattern = Pattern.compile(String.format(FORMAT, "2"))

    /**
     * 允许输入的最大金额
     */
    private var maxValue = Int.MAX_VALUE.toDouble()

    private var minValue = 0.0

    /**
     * 设置保留小数点后的位数，默认保留2位
     *
     * @param length
     */
    fun setDecimalLength(length: Int) {
        mPattern = Pattern.compile(String.format(FORMAT, length))
    }

    /**
     * 设置允许输入的最小金额
     *
     * @param minValue
     */
    fun setMinValue(minValue: Double) {
        this.minValue = minValue
    }
    /**
     * 设置允许输入的最大金额
     *
     * @param maxValue
     */
    fun setMaxValue(maxValue: Double) {
        this.maxValue = maxValue
    }

    /**
     * 当系统使用source的start到end的字串替换dest字符串中的dstart到dend位置的内容时，会调用本方法
     *
     * @param source 新输入的字符串
     * @param start  新输入的字符串起始下标，一般为0（删除时例外）
     * @param end    新输入的字符串终点下标，一般为source长度-1（删除时例外）
     * @param dest   输入之前文本框内容
     * @param dstart 原内容起始坐标，一般为dest长度（删除时例外）
     * @param dend   原内容终点坐标，一般为dest长度（删除时例外）
     * @return 你希望输入的内容，比如当新输入的字符串为“恨”时，你希望把“恨”变为“爱”，则return "爱"
     */
    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        // 删除时不用处理
        if (TextUtils.isEmpty(source)) {
            return null
        }

        // 不接受数字、小数点、逗号之外的字符
        if (SOURCE_PATTERN.matcher(source).find()) {
            Log.e(TAG,"不接受数字、小数点、逗号之外的字符")
            return ""
        }

        val ssb = SpannableStringBuilder(dest)
        ssb.replace(dstart, dend, source, start, end)

        Log.e(TAG,"filter-ssb = $ssb")

        // 如果第一个输入的是"."则返回"0."
        if (TextUtils.equals(".", ssb)) {
            return "0."
        }

        // 移除逗号后进行验证（保留逗号用于显示，但验证时去掉）
        val ssbStr = ssb.toString()
        val pureNumber = ssbStr.replace(",", "")

        Log.e(TAG,"pureNumber = $pureNumber")
        if (TextUtils.isEmpty(pureNumber)) {
            return ""
        }

        // 检查是否只有小数点
        if (TextUtils.equals(".", pureNumber)) {
            return "0."
        }

        val matcher: Matcher = mPattern.matcher(pureNumber)

        // 不管是否包含逗号，都需要经过验证
        if (matcher.find()) {
            val str: String = matcher.group()
            Log.e(TAG,"匹配到的字符串=$str")

            // 验证输入金额的大小
            try {
                val value = str.formatDouble()
                if (value < minValue) {
                    Log.e(TAG,"匹配到的字符串=$str 验证输入金额的小于 minValue：$minValue")
                    return ""
                } else if (value > maxValue) {
                    Log.e(TAG,"匹配到的字符串=$str 验证输入金额的大于 maxValue：$maxValue")
                    return ""
                } else {
                    // 验证通过，返回原始source
                    return source
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG,"字符串：$str 格式化数字失败")
                return ""
            }
        } else {
            Log.e(TAG,"不匹配的字符串=" + ssb.toString())
            return ""
        }
    }
}