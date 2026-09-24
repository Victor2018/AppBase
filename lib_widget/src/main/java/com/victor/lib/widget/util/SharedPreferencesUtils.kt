package com.victor.lib.widget.util

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.victor.lib.widget.module.WidgetModule
import com.victor.lib.widget.util.SharedPreferencesUtils.TAG
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SharedPreferencesUtils
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

object SharedPreferencesUtils {
    val TAG = "SharedPreferencesUtils"
    /**
     * 创建 SharedPreferences 对象
     */
    var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(WidgetModule.instance.mApplication)

    /**
     * 定义 Stirng 类型的 HOST 通过 SharedPreferenceDelegates.string 将其委托给 string 方法返回的
     * ReadWriteProperty 对象
     */
    var loginData by SharedPreferenceDelegates.string(
        ""
    )

    /**
     * userinfo
     */
    var userInfo by SharedPreferenceDelegates.string(
        ""
    )
    /**
     * oss bucket config
     */
    var ossBucketConfig by SharedPreferenceDelegates.string(
        ""
    )

    var deviceInfo by SharedPreferenceDelegates.string(
        ""
    )

    var udid by SharedPreferenceDelegates.string(
        ""
    )

    var loginDeviceId by SharedPreferenceDelegates.string(
        ""
    )

    var lastConnectBleMac by SharedPreferenceDelegates.string(
        ""
    )
    var umengPushDeviceToken by SharedPreferenceDelegates.string(
        ""
    )
    var bizAccountData by SharedPreferenceDelegates.string(
        ""
    )

    /**
     * 微信门店认证弹窗显示日期
     */
    var wechatStoreAuthShowDate by SharedPreferenceDelegates.string(
        ""
    )

    var lastConnectBlePrintType by SharedPreferenceDelegates.int(
        0
    )

    /**
     * 小票打印延时关闭时间，单位：毫秒 默认值3000
     */
    var ticketPrintInterval by SharedPreferenceDelegates.long(
        3000
    )

    /**
     * 是否同意隐私政策弹窗
     */
    var agreePrivacyPolicy by SharedPreferenceDelegates.boolean(
        false
    )

    /**
     * 是否显示过引导页
     */
    var hasShowGuide by SharedPreferenceDelegates.boolean(
        false
    )

    var setJPushAlias by SharedPreferenceDelegates.boolean(false)

    /**
     * 商家入驻成功
     */
    var bizJoinSuccess by SharedPreferenceDelegates.boolean(false)

    /**
     * 推送声音是否打开
     */
    var isPushSoundOpen by SharedPreferenceDelegates.boolean(true)
    /**
     * 推送震动是否打开
     */
    var isPushVibrateOpen by SharedPreferenceDelegates.boolean(true)

    /**
     * 个性化推荐是否打开
     */
    var isRecommendOpen by SharedPreferenceDelegates.boolean(true)

    /**
     * 是否显示直播IM消息
     */
    var isShowLiveIM by SharedPreferenceDelegates.boolean(true)

    /**
     * 文件读权限被禁止
     */
    var isReadPermissionReallyDeclined by SharedPreferenceDelegates.boolean(false)
    /**
     * 文件写权限被禁止
     */
    var isWritePermissionReallyDeclined by SharedPreferenceDelegates.boolean(false)

    fun putInt(key: String, value: Int) {
        Log.e(TAG, "putInt()-$key=$value")
        val ed = preferences.edit()
        ed.putInt(key, value)
        ed.commit()
    }

    fun getInt(key: String): Int {
        val value = preferences.getInt(key, 0)
        Log.e(TAG, "putInt()-$key=$value")
        return value
    }

    fun getInt(key: String, defauleValue: Int): Int {
        val value = preferences.getInt(key, defauleValue)
        Log.e(TAG, "putInt()-$key=$value")
        return value
    }

    fun putLong(key: String, value: Long) {
        Log.e(TAG, "putLong()-$key=$value")
        val ed = preferences.edit()
        ed.putLong(key, value)
        ed.commit()
    }

    fun getLong(key: String): Long? {
        val value = preferences.getLong(key, 0)
        Log.e(TAG, "getLong()-$key=$value")
        return value
    }

    fun putString(key: String, value: String) {
        Log.e(TAG, "putString()-$key=$value")
        val ed = preferences.edit()
        ed.putString(key, value)
        ed.commit()
    }

    fun getString(key: String, defaultValue: String?): String? {
        val value = preferences.getString(key, defaultValue)
        Log.e(TAG, "getString()-$key=$value")
        return value
    }

    fun putBoolean(key: String, value: Boolean) {
        Log.e(TAG, "putBoolean()-$key=$value")
        val ed = preferences.edit()
        ed.putBoolean(key, value)
        ed.commit()
    }

    fun getBoolean(key: String): Boolean {
        val value = preferences.getBoolean(key, false)
        Log.e(TAG, "getBoolean()-$key=$value")
        return value
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val value = preferences.getBoolean(key, defaultValue)
        Log.e(TAG, "getBoolean()-$key=$value")
        return value
    }
}

/**
 * 定义类型 属性委托类
 */
private object SharedPreferenceDelegates {
    /**
     * 定义委托获取和设置对应类型的方法
     * 委托的原理,大家可以看我前面的文章
     */
    fun int(defaultValue: Int = 0) = object : ReadWriteProperty<SharedPreferencesUtils, Int> {

        override fun getValue(thisRef: SharedPreferencesUtils, property: KProperty<*>): Int {
            /**
             * 当获取值的时候,调用此方法
             * key 值是对应变量的昵称
             */
            return SharedPreferencesUtils.preferences.getInt(property.name, defaultValue)
        }

        override fun setValue(thisRef: SharedPreferencesUtils, property: KProperty<*>, value: Int) {
            /**
             * 当设置值的时候,调用此方法
             * key 值是对应变量的昵称
             */
            Log.e(TAG,"int-property.name = " + property.name)
            Log.e(TAG,"int-value = " + value)
            SharedPreferencesUtils.preferences.edit().putInt(property.name, value).apply()
        }
    }

    fun long(defaultValue: Long = 0L) = object : ReadWriteProperty<SharedPreferencesUtils, Long> {

        override fun getValue(thisRef: SharedPreferencesUtils, property: KProperty<*>): Long {
            return SharedPreferencesUtils.preferences.getLong(property.name, defaultValue)
        }

        override fun setValue(
            thisRef: SharedPreferencesUtils,
            property: KProperty<*>,
            value: Long
        ) {
            SharedPreferencesUtils.preferences.edit().putLong(property.name, value).apply()
        }
    }

    fun boolean(defaultValue: Boolean = false) =
        object : ReadWriteProperty<SharedPreferencesUtils, Boolean> {
            override fun getValue(
                thisRef: SharedPreferencesUtils,
                property: KProperty<*>
            ): Boolean {
                return SharedPreferencesUtils.preferences.getBoolean(property.name, defaultValue)
            }

            override fun setValue(
                thisRef: SharedPreferencesUtils,
                property: KProperty<*>,
                value: Boolean
            ) {
                SharedPreferencesUtils.preferences.edit().putBoolean(property.name, value).apply()
            }
        }

    fun float(defaultValue: Float = 0.0f) =
        object : ReadWriteProperty<SharedPreferencesUtils, Float> {
            override fun getValue(thisRef: SharedPreferencesUtils, property: KProperty<*>): Float {
                return SharedPreferencesUtils.preferences.getFloat(property.name, defaultValue)
            }

            override fun setValue(
                thisRef: SharedPreferencesUtils,
                property: KProperty<*>,
                value: Float
            ) {
                SharedPreferencesUtils.preferences.edit().putFloat(property.name, value).apply()
            }
        }

    fun string(defaultValue: String) = object : ReadWriteProperty<SharedPreferencesUtils, String> {
        override fun getValue(thisRef: SharedPreferencesUtils, property: KProperty<*>): String {
            return SharedPreferencesUtils.preferences.getString(property.name, defaultValue) ?: ""
        }

        override fun setValue(
            thisRef: SharedPreferencesUtils,
            property: KProperty<*>,
            value: String
        ) {
            Log.e(TAG,"string-property.name = " + property.name)
            Log.e(TAG,"string-value = " + value)
            SharedPreferencesUtils.preferences.edit().putString(property.name, value).apply()
        }
    }

}