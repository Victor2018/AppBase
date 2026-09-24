package com.victor.lib.widget.util

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntegerRes
import com.victor.lib.widget.R
import com.victor.lib.widget.module.WidgetModule
import com.victor.screen.match.library.BuildConfig

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ToastUtils.java
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 吐司工具类
 * -----------------------------------------------------------------
 */

object ToastUtils {

    /**
     * 调试模式下可显示
     *
     * @param msg
     */
    fun showDebug(msg: String) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(WidgetModule.instance.mApplication, msg, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 调试模式下可显示
     *
     * @param resId
     */
    fun showDebug(@IntegerRes resId: Int) {
        if (BuildConfig.DEBUG) {
            val text = ResUtils.getStringRes(WidgetModule.instance.mApplication,resId)
            Toast.makeText(WidgetModule.instance.mApplication, text, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 短暂显示
     *
     * @param msg
     */
    fun showShort(msg: CharSequence) {
        Toast.makeText(WidgetModule.instance.mApplication, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * 短暂显示
     *
     * @param resId
     */
    fun showShort(resId: Int) {
        val text = ResUtils.getStringRes(WidgetModule.instance.mApplication,resId)
        Toast.makeText(WidgetModule.instance.mApplication, text, Toast.LENGTH_SHORT).show()
    }

    /**
     * 长时间显示
     *
     * @param msg
     */
    fun showLong(msg: CharSequence) {
        Toast.makeText(WidgetModule.instance.mApplication, msg, Toast.LENGTH_LONG).show()
    }

    /**
     * 自定义时间显示
     *
     * @param msg
     */
    fun showByDuration(msg: CharSequence,duration: Int) {
        Toast.makeText(WidgetModule.instance.mApplication, msg,duration).show()
    }

    /**
     * 短暂显示
     *
     * @param resId
     */
    fun showLong(resId: Int) {
        val text = ResUtils.getStringRes(WidgetModule.instance.mApplication,resId)
        Toast.makeText(WidgetModule.instance.mApplication, text, Toast.LENGTH_LONG).show()
    }

    fun show(msg: CharSequence?) {
        if (msg == null) return
        if (TextUtils.isEmpty(msg.toString())) return

        val inflater = WidgetModule.instance.mApplication?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //自定义布局
        val view: View = inflater.inflate(R.layout.toast_layout, null)
        val mTvMessage = view.findViewById<View>(R.id.tv_message) as TextView
        mTvMessage.text = msg
        val mToast = Toast(WidgetModule.instance.mApplication)
        val height = ScreenUtils.getHeight(WidgetModule.instance.mApplication)
        mToast.setGravity(Gravity.BOTTOM, 0, height / 6)
        mToast.duration = Toast.LENGTH_SHORT
        mToast.view = view
        mToast.show()
    }

    fun show(resId: Int) {
        val msg = ResUtils.getStringRes(WidgetModule.instance.mApplication,resId)
        if (TextUtils.isEmpty(msg)) return
        val inflater = WidgetModule.instance.mApplication?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //自定义布局
        val view: View = inflater.inflate(R.layout.toast_layout, null)
        val mTvMessage = view.findViewById<View>(R.id.tv_message) as TextView
        mTvMessage.text = msg
        val mToast = Toast(WidgetModule.instance.mApplication)
        val height = ScreenUtils.getHeight(WidgetModule.instance.mApplication)
        mToast.setGravity(Gravity.BOTTOM, 0, height / 6)
        mToast.duration = Toast.LENGTH_SHORT
        mToast.view = view
        mToast.show()
    }

    fun show(msg: CharSequence?,drawableTopResId: Int) {
        if (msg == null) return
        if (TextUtils.isEmpty(msg.toString())) return

        val inflater = WidgetModule.instance.mApplication?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //自定义布局
        val view: View = inflater.inflate(R.layout.toast_img_layout, null)
        val mTvMessage = view.findViewById<View>(R.id.tv_message) as TextView

        if (drawableTopResId != 0) {
            TextViewBoundsUtil.setTvDrawableTop(view.context,mTvMessage,drawableTopResId)
        }

        mTvMessage.text = msg
        val mToast = Toast(WidgetModule.instance.mApplication)
        mToast.setGravity(Gravity.CENTER, 0, 0)
        mToast.duration = Toast.LENGTH_SHORT
        mToast.view = view
        mToast.show()
    }
}