package com.victor.lib.widget.etfilter

import android.text.method.PasswordTransformationMethod
import android.view.View

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: StarPasswordTransformationMethod
 * Author: Victor
 * Date: 2025/12/18 16:19
 * Description: 
 * -----------------------------------------------------------------
 */

class StarPasswordTransformationMethod : PasswordTransformationMethod() {

    val STAR_CHAR = '*'

    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return PasswordCharSequence(source)
    }

    private inner class PasswordCharSequence(
        private val source: CharSequence
    ) : CharSequence {
        override val length: Int get() = source.length

        override fun get(index: Int): Char = STAR_CHAR

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return PasswordCharSequence(source.subSequence(startIndex, endIndex))
        }

    }
}