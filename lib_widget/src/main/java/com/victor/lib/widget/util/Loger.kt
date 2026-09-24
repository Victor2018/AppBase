package com.victor.lib.widget.util

import android.util.Log
import com.victor.screen.match.library.BuildConfig

object Loger {
    fun d(TAG: String, msg: Any) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg.toString())
        }
    }

    fun e(TAG: String, msg: Any) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, msg.toString())
        }
    }

    fun i(TAG: String, msg: Any) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, msg.toString())
        }
    }

    fun v(TAG: String, msg: Any) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, msg.toString())
        }
    }

    fun w(TAG: String, msg: Any) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, msg.toString())
        }
    }
}