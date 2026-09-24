package com.victor.lib.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import androidx.appcompat.widget.AppCompatEditText

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UserEditText
 * Author: Victor
 * Date: 2026/6/11 11:18
 * Description: 
 * -----------------------------------------------------------------
 */

class UserEditText: AppCompatEditText, TextWatcher {

    var fromUserInput = false

    var afterTextChanged: ((Editable?, Boolean) -> Unit) ?= null

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,android.R.attr.editTextStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs,defStyle) {
        addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    override fun afterTextChanged(s: Editable?) {
        afterTextChanged?.invoke(s,fromUserInput)
        fromUserInput = false
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        fromUserInput = false
        super.setText(text, type)
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val inputConnection = super.onCreateInputConnection(outAttrs)
        return object : InputConnectionWrapper(inputConnection, true) {
            override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
                // 键盘输入、粘贴等都会调用此方法，标记为手动输入
                fromUserInput = true
                return super.commitText(text, newCursorPosition)
            }

            override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
                // 删除操作也视为手动输入
                fromUserInput = true
                return super.deleteSurroundingText(beforeLength, afterLength)
            }

            override fun sendKeyEvent(event: KeyEvent?): Boolean {
                // 物理键盘按键事件
                if (event?.action == KeyEvent.ACTION_DOWN) {
                    fromUserInput = true
                }
                return super.sendKeyEvent(event)
            }
        }
    }

}