package com.victor.lib.widget.textwatcher

import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.widget.EditText

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AddSpaceTextWatcher
 * Author: Victor
 * Date: 2026/6/3 10:30
 * Description: 
 * -----------------------------------------------------------------
 */

class AddSpaceTextWatcher(val editText: EditText, val maxLength: Int) : TextWatcher {

    /** text改变之前的长度 */
    private var beforeTextLength = 0
    private var onTextLength = 0
    private var isChanged = false
    private val buffer = StringBuffer()
    /** 改变之前text空格数量 */
    var spaceNumberA = 0
        private set
    /** text最大长度限制 */
    private var spaceType: SpaceType = SpaceType.defaultType
    /** 记录光标的位置 */
    private var location = 0
    /** 是否是主动设置text */
    private var isSetText = false

    var onNoSpaceTextChanged: ((String?) -> Unit)? = null

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        beforeTextLength = s.length
        if (buffer.isNotEmpty()) {
            buffer.delete(0, buffer.length)
        }
        spaceNumberA = 0
        for (i in 0 until s.length) {
            if (s[i] == ' ') {
                spaceNumberA++
            }
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        onTextLength = s.length
        buffer.append(s.toString())
        if (onTextLength == beforeTextLength || onTextLength > maxLength || isChanged) {
            isChanged = false
            return
        }
        isChanged = true
    }

    override fun afterTextChanged(s: Editable) {
        if (isChanged) {
            location = editText.selectionEnd
            var index = 0
            while (index < buffer.length) { // 删掉所有空格
                if (buffer[index] == ' ') {
                    buffer.deleteCharAt(index)
                } else {
                    index++
                }
            }
            index = 0
            var spaceNumberB = 0
            while (index < buffer.length) { // 插入所有空格
                spaceNumberB = insertSpace(index, spaceNumberB)
                index++
            }
            val str = buffer.toString()
            // 下面是计算光位置的
            if (spaceNumberB > spaceNumberA) {
                location += spaceNumberB - spaceNumberA
                spaceNumberA = spaceNumberB
            }
            if (isSetText) {
                location = str.length
                isSetText = false
            } else if (location > str.length) {
                location = str.length
            } else if (location < 0) {
                location = 0
            }
            updateContext(s, str)
            isChanged = false

            onNoSpaceTextChanged?.invoke(getTextNotSpace())
        }
    }

    /**
     * 更新编辑框中的内容
     *
     * @param editable
     * @param values
     */
    private fun updateContext(editable: Editable, values: String) {
        if (spaceType == SpaceType.IDCardNumberType) {
            editable.replace(0, editable.length, values)
        } else {
            editText.setText(values)
            try {
                editText.setSelection(location)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 根据类型插入空格
     *
     * @param index
     * @param spaceNumberAfter
     * @return
     * @see [类、类#方法、类#成员]
     */
    private fun insertSpace(index: Int, spaceNumberAfter: Int): Int {
        var spaceNumberAfterVar = spaceNumberAfter
        when (spaceType) {
            SpaceType.defaultType, SpaceType.bankCardNumberType -> {
                if (index > 3 && index % (4 * (spaceNumberAfterVar + 1)) == spaceNumberAfterVar) {
                    buffer.insert(index, ' ')
                    spaceNumberAfterVar++
                }
            }
            SpaceType.mobilePhoneNumberType -> {
                if (index == 3 || (index > 7 && (index - 3) % (4 * spaceNumberAfterVar) == spaceNumberAfterVar)) {
                    buffer.insert(index, ' ')
                    spaceNumberAfterVar++
                }
            }
            SpaceType.IDCardNumberType -> {
                if (index == 6 || (index > 10 && (index - 6) % (4 * spaceNumberAfterVar) == spaceNumberAfterVar)) {
                    buffer.insert(index, ' ')
                    spaceNumberAfterVar++
                }
            }
            else -> {
                if (index > 3 && index % (4 * (spaceNumberAfterVar + 1)) == spaceNumberAfterVar) {
                    buffer.insert(index, ' ')
                    spaceNumberAfterVar++
                }
            }
        }
        return spaceNumberAfterVar
    }

    /***
     * 计算需要的空格数
     *
     * @return 返回添加空格后的字符串长度
     * @see [类、类#方法、类#成员]
     */
    private fun computeSpaceCount(charSequence: CharSequence): Int {
        buffer.delete(0, buffer.length)
        buffer.append(charSequence.toString())
        var index = 0
        var spaceNumberB = 0
        while (index < buffer.length) { // 插入所有空格
            spaceNumberB = insertSpace(index, spaceNumberB)
            index++
        }
        buffer.delete(0, buffer.length)
        return index
    }

    /**
     * 设置空格类型
     *
     * @param spaceType
     * @see [类、类#方法、类#成员]
     */
    fun setSpaceType(spaceType: SpaceType) {
        this.spaceType = spaceType
        if (this.spaceType == SpaceType.IDCardNumberType) {
            editText.inputType = InputType.TYPE_CLASS_TEXT
            //此处添加输入法的限制
            val digits = "0123456789Xx "
            if (!TextUtils.isEmpty(digits)) {
                editText.keyListener = DigitsKeyListener.getInstance(digits)
            }
        }
    }

    /**
     * 设置输入字符
     *
     * @param charSequence
     * @return 返回设置成功失败
     * @see [类、类#方法、类#成员]
     */
    fun setText(charSequence: CharSequence): Boolean {
        if (editText != null && !TextUtils.isEmpty(charSequence) && computeSpaceCount(charSequence) <= maxLength) {
            isSetText = true
            editText.removeTextChangedListener(this)
            editText.setText(charSequence)
            editText.addTextChangedListener(this)
            return true
        }
        return false
    }

    /**
     * 得到输入的字符串去空格后的字符串
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    fun getTextNotSpace(): String? {
        return if (editText != null) {
            delSpace(editText.text.toString())
        } else {
            null
        }
    }

    /**
     * 得到输入的字符串去空格后的长度
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    fun getLengthNotSpace(): Int {
        return getTextNotSpace()?.length ?: 0
    }

    /**
     * 得到空格数量
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    fun getSpaceCount(): Int {
        return spaceNumberA
    }

    /**
     * 去掉字符空格，换行符等
     *
     * @param str
     * @return
     * @see [类、类#方法、类#成员]
     */
    private fun delSpace(str: String): String {
        var result = str
        if (result != null) {
            result = result.replace("\r", "")
            result = result.replace("\n", "")
            result = result.replace(" ", "")
        }
        return result
    }

    /**
     * 空格类型
     */
    enum class SpaceType {
        /** 默认类型 */
        defaultType,
        /** 银行卡类型 */
        bankCardNumberType,
        /** 手机号类型 */
        mobilePhoneNumberType,
        /** 身份证类型 */
        IDCardNumberType
    }
}