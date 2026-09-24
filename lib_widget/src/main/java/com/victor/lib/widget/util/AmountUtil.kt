package com.victor.lib.widget.util

import android.text.SpannableString
import android.text.TextUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.regex.Pattern

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AmountUtil
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 金额转换工具
 * -----------------------------------------------------------------
 */

object AmountUtil {

    val TAG = "AmountUtil"

    /**
     * 使用BigDecimal做乘法避免double / double 丢失精度
     */
    fun multiply(value: String?, divisor: String?): String? {
        return try {
            BigDecimal(value).multiply(BigDecimal(divisor)).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "0.0"
        }
    }

    /**
     * 使用BigDecimal做除法避免double / double 丢失精度
     */
    fun div(value: String?, divisor: String?): String? {
        return try {
            BigDecimal(value).divide(BigDecimal(divisor), 10, RoundingMode.HALF_UP).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "0.0"
        }
    }
    /**
     * 四舍五入
     *
     * @param value 数值
     * @param digit 保留小数位
     * @return
     */
    fun getRoundUp(value: String?, digit: Int): String? {
        return getRoundUp(value,digit,true)
    }

    /**
     * @param value 数值
     * @param digit 保留小数位
     * @param hasZero 小数点后是00是否保留
     * @return
     */

    fun getRoundUp(value: String?, digit: Int,hasZero: Boolean): String? {
        var result = "0.0"
        try {
            var inputValue = value ?: "0.0"
            if (TextUtils.isEmpty(inputValue)) {
                inputValue = "0.0"
            }
            val bigDecimal = BigDecimal(inputValue)
            val scaled =  bigDecimal.setScale(digit, BigDecimal.ROUND_HALF_UP)

            // Remove trailing zeros and optional decimal point
            result = scaled.stripTrailingZeros().toPlainString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    fun getRoundUpDouble(value: Double?, digit: Int): Double {
        var result = 0.0
        try {
            var inputValue = value.toString()
            if (TextUtils.isEmpty(inputValue)) {
                inputValue = "0.0"
            }
            val bigDecimal = BigDecimal(inputValue)
            result =  bigDecimal.setScale(digit, BigDecimal.ROUND_HALF_UP).toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    fun getRoundDownToHalf(value: Double?): Float {
        return try {
            val inputValue = value?.toString() ?: "0.0"
            val bigDecimal = BigDecimal(inputValue)
            // 乘以2，向下取整，再除以2，得到最近的0.5
            val result = bigDecimal.multiply(BigDecimal("2"))
                .setScale(0, BigDecimal.ROUND_DOWN)
                .divide(BigDecimal("2"))
                .toFloat()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            0f
        }
    }

    fun getRoundUpString(value: Double?, digit: Int): String {
        var result = "0.0"
        try {
            var inputValue = value.toString()
            if (TextUtils.isEmpty(inputValue)) {
                inputValue = "0.0"
            }
            val bigDecimal = BigDecimal(inputValue)
            result =  bigDecimal.setScale(digit, BigDecimal.ROUND_HALF_UP).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**
     * 将每三个数字加上逗号处理,最后保留两位小数（通常使用金额方面的编辑）示例：9，702.44
     *
     * @param value 金额
     * @param hasZero 小数点后是00是否保留
     * @return
     */
    fun addCommaDots(value: Double?,hasZero: Boolean): String {
        var result = "0"
        if (hasZero) {
            result = "0.00"
        }
        try {
            val myformat = DecimalFormat()
            myformat.applyPattern(",##0.##")//去除小数点后多余的0
            if (hasZero) {
                myformat.applyPattern(",##0.00")//格式化后没有小数的补00
            }
//            myformat.applyPattern(",##0.00")//千分位格式化后没有小数的补00
//            myformat.applyPattern(",##0.##")//去除小数点后多余的0
            result = myformat.format(value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun addCommaDots(value: String?,hasZero: Boolean): Double {
        var result = 0.00
        try {
            result = addCommaDots(value?.toDouble(),hasZero).toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun addCommaDotsNoZero(value: Double,digit: Int): String {
        var result = "0.00"
        try {
            val myformat = DecimalFormat()
            var pattenSb = StringBuffer(",##0.")
            if (digit == 0) {
                pattenSb = StringBuffer(",##0")
            } else {
                for (i in 0 until digit) {
                    pattenSb.append("#")
                }
            }
            myformat.applyPattern(pattenSb.toString())//去除小数点后多余的0
            result = myformat.format(value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * 只格式化金额前缀和小数点后文字字体大小并加粗金额整数部分
     */
    fun getShowAmount (prefixStr: String,value: Double,dotTextSize: Int): SpannableString? {
        return getShowAmount(prefixStr,value,dotTextSize, "",listOf(),true)
    }

    /**
     * 只格式化金额前缀和小数点后文字字体大小并加粗金额整数部分
     */
    fun getShowAmount (prefixStr: String,value: Double,dotTextSize: Int,hasZero: Boolean): SpannableString? {
        return getShowAmount(prefixStr,value,dotTextSize, "",listOf(),hasZero)
    }

    fun getShowAmount (prefixStr: String,value: Double,dotTextSize: Int,suffixStr: String): SpannableString? {
        return getShowAmount(prefixStr,value,dotTextSize, suffixStr,listOf(),true)
    }

    fun getShowAmount (prefixStr: String,value: Double,dotTextSize: Int,suffixStr: String,hasZero: Boolean): SpannableString? {
        return getShowAmount(prefixStr,value,dotTextSize, suffixStr,listOf(),hasZero)
    }

    /**
     * 只格式化金额前缀和小数点后文字字体大小并加粗金额整数部分
     * @param boldSpanText 加粗字体为空默认加粗金额整数部分，否则加粗内容为传入boldSpanText
     */
    fun getShowAmount (prefixStr: String,value: Double,dotTextSize: Int,boldSpanText: List<String>): SpannableString? {
        //格式化后的金额字符串
        return getShowAmount(prefixStr,value,dotTextSize, "",boldSpanText,true)
    }

    /**
     * 只格式化金额前缀和小数点后文字字体大小并加粗金额整数部分
     * @param boldSpanText 加粗字体为空默认加粗金额整数部分，否则加粗内容为传入boldSpanText
     */
    fun getShowAmount (prefixStr: String,value: Double,dotTextSize: Int,boldSpanText: List<String>,hasZero: Boolean): SpannableString? {
        //格式化后的金额字符串
        return getShowAmount(prefixStr,value,dotTextSize, "",boldSpanText,hasZero)
    }

    /**
     * 格式化金额前缀、小数点后文字、sampleSpanText字体大小
     */
    fun getShowAmount (prefixStr: String,value: Double,dotTextSize: Int,suffixStr: String, boldSpanText: List<String>,hasZero: Boolean): SpannableString? {
        //格式化后的金额字符串
        val text = addCommaDots(value,hasZero)
        val textStr = "$prefixStr$text$suffixStr"
        if (textStr.length < 3) return null
        val dotSpanText = textStr.substring(textStr.length - 3 - suffixStr.length,textStr.length)

        val spanList = ArrayList<String>()
        if (!TextUtils.isEmpty(prefixStr)) {
            spanList.add(prefixStr)
        }
        spanList.add(dotSpanText)
        if (!TextUtils.isEmpty(suffixStr)) {
            spanList.add(suffixStr)
        }
        val amountText = SpannableUtil.getSpannableTextSize(dotTextSize, textStr,spanList)

        if (text.contains(".")) {
            val amountStrs = text.split(".")
            val boldText = "${amountStrs[0]}."
            if (boldSpanText.isEmpty()) {
                return SpannableUtil.getSpannableBoldText(amountText,boldText)
            }
        }
        return SpannableUtil.getSpannableBoldText(amountText,boldSpanText)
    }

    fun getTextSizeShowAmount (prefixStr: String,value: Double,dotTextSize: Int,suffixStr: String,dotTextSizeFormat: Boolean): SpannableString? {
        //格式化后的金额字符串
        val text = addCommaDots(value,false)
        val textStr = "$prefixStr$text$suffixStr"
        val spanList = ArrayList<String>()
        spanList.add(prefixStr)
        if (dotTextSizeFormat) {
            if (textStr.length < 3) return null
            val dotSpanText = textStr.substring(textStr.length - 3 - suffixStr.length,textStr.length)
            spanList.add(dotSpanText)
        }

        if (!TextUtils.isEmpty(suffixStr)) {
            spanList.add(suffixStr)
        }
        val amountText = SpannableUtil.getSpannableTextSize(dotTextSize, textStr,spanList)

        return amountText
    }

    fun addCommaDotsForOrder(valueX: Double?,digit: Int,addUnit: Boolean,halfUp: Boolean): String {
        var value = valueX ?: 0.0
        var inputValue = value
        var result = "0.00"
        var unitStr = ""
        if (addUnit) {
            if (value >= 100000000.0) {
                inputValue = value / 100000000.0
                unitStr = "亿"
            } else if (value >= 10000.0) {
                inputValue = value / 10000.0
                unitStr = "万"
            }
        }
        try {
            val myformat = DecimalFormat()
            if (!halfUp) {//比例接口已经四色五人了，这里直接处理显示
                myformat.roundingMode = RoundingMode.DOWN
            }
            var pattenSb = StringBuffer(",##0.")
            if (digit == 0) {
                pattenSb = StringBuffer(",##0")
            } else {
                for (i in 0 until digit) {
                    pattenSb.append("#")
                }
            }

            myformat.applyPattern(pattenSb.toString())//去除小数点后多余的0
            result = myformat.format(inputValue)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result + unitStr
    }

    fun getEvaluationCount(value: Int): String? {
        try {
            if (value < 1000) return value.toString()

            if (value % 1000.0 > 0) {
                val bigDecimal = BigDecimal((value / 1000.0).toString())
                val result: Double = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).toDouble()
                return "${result}k"
            }

            return "${value / 1000}K"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getViewCount(value: Int): String? {
        try {
            if (value < 10000) return value.toString()

            if (value % 10000.0 > 0) {
                val bigDecimal = BigDecimal((value / 10000.0).toString())
                val result: Double = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).toDouble()
                return "${result}w"
            }

            return "${value / 10000}K"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun isNumber(userId: String?): Boolean {
        return try {
            val pattern = Pattern.compile("\\d+");
            pattern.matcher(userId).matches()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 隐藏金额中的数字
     */
    fun hideAmount(text: String): String {
        try {
            return text.replace(Regex("""[,.]"""), "")  // 去掉逗号和小数点
                .replace(Regex("""\d"""), "*")   // 替换数字为星号
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return text
    }

    /**
     * 完全替换为*
     */
    fun hideAmountAll(text: String): String {
        try {
            // 移除所有非数字字符，只保留数字
            val digitsOnly = text.filter { it.isDigit() }
            // 根据数字位数生成对应数量的星号
            return "*".repeat(digitsOnly.length)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return text
    }
}