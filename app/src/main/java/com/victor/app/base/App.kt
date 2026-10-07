package com.victor.app.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatDelegate
import com.victor.lib.base.BaseApplication
import com.victor.lib.widget.util.Loger
import com.victor.lib.widget.util.SharedPreferencesUtils
import com.victor.screen.match.library.BuildConfig
import java.lang.ref.WeakReference

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: App
 * Author: Victor
 * Date: 2026/10/7 14:54
 * Description: 
 * -----------------------------------------------------------------
 */

open class App : BaseApplication(), Application.ActivityLifecycleCallbacks {
    val TAG = "App"

    //为避免内存泄漏使用弱引用
    var mCurrentActivity: WeakReference<Activity>? = null
    var allActivitys = ArrayList<Activity>()


    companion object {
        private lateinit var instance: App

        @JvmStatic
        fun get() = instance
    }

    override fun onCreate() {
     super.onCreate()
     instance = this
     //关闭黑夜模式
     AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
     registerActivityLifecycleCallbacks(this)

    }

    override fun onActivityPaused(activity: Activity) {
        Loger.e(TAG, "onActivityPaused()...simpleName = " + activity.javaClass.simpleName)
    }

    override fun onActivityStarted(activity: Activity) {
        Loger.e(TAG, "onActivityStarted()...simpleName = " + activity.javaClass.simpleName)
    }

    override fun onActivityDestroyed(activity: Activity) {
        Loger.e(TAG, "onActivityDestroyed()...simpleName = " + activity.javaClass.simpleName)
        if (TextUtils.equals("LoginAuthActivity", activity.javaClass.simpleName)) {
//            postEvent(LoginActions.ONE_KEY_LOGIN_CLOSE)
        }
        allActivitys.remove(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
        Loger.e(TAG, "onActivityStopped()...simpleName = " + activity.javaClass.simpleName)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Loger.e(TAG, "onActivityCreated()...simpleName = " + activity.javaClass.simpleName)
        if (!allActivitys.contains(activity)) {
            allActivitys.add(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        Loger.e(TAG, "onActivityResumed()...simpleName = " + activity.javaClass.simpleName)
        mCurrentActivity = WeakReference(activity)
    }

    /**
     * 判断当前页面是不是主界面
     */
    fun isCurrentActivityMain(): Boolean {
        val simpleName = mCurrentActivity?.get()?.javaClass?.simpleName
        return TextUtils.equals("MainActivity", simpleName)
    }

    /**
     * 判断当前页面是不是一键登录
     */
    fun isCurrentLoginActivity(): Boolean {
        return isCurrentLoginAuthActivity() || isCurrentCodeLoginActivity() || isCurrentBindPhoneActivity()
    }

    /**
     * 判断当前页面是不是一键登录
     */
    fun isCurrentLoginAuthActivity(): Boolean {
        val simpleName = mCurrentActivity?.get()?.javaClass?.simpleName
        return TextUtils.equals("LoginAuthActivity", simpleName)
    }

    /**
     * 判断当前页面是不是一键登录
     */
    fun isCurrentCodeLoginActivity(): Boolean {
        val simpleName = mCurrentActivity?.get()?.javaClass?.simpleName
        return TextUtils.equals("CodeLoginActivity", simpleName)
    }

    /**
     * 判断当前页面是不是一键登录
     */
    fun isCurrentBindPhoneActivity(): Boolean {
        val simpleName = mCurrentActivity?.get()?.javaClass?.simpleName
        return TextUtils.equals("BindPhoneActivity", simpleName)
    }

    /**
     * 判断主界面是否已启动
     */
    fun isMainActivityLaunched(): Boolean {
        allActivitys.forEach {
            val simpleName = it.javaClass.simpleName
            if (TextUtils.equals("MainActivity", simpleName)) return true
        }
        return false
    }

    /**
     * 判断主界面是否已启动
     */
    fun isLoginActivityLaunched(): Boolean {
        allActivitys.forEach {
            val simpleName = it.javaClass.simpleName
            if (TextUtils.equals("LoginActivity", simpleName)) return true
        }
        return false
    }

    /**
     * 关闭SplashActivity,MainActivity 以外的activity
     */
    fun finishOtherThenSplashAndMainActivity() {
        allActivitys.forEach {
            val simpleName = it.javaClass.simpleName
            if (!TextUtils.equals("MainActivity", simpleName) &&
                !TextUtils.equals("SplashActivity", simpleName)
            ) {
                it.finish()
            }
        }
    }

    /**
     * 关闭LoginActivity 以外的activity
     */
    fun finishOtherThenLoginActivity() {
        allActivitys.forEach {
            val simpleName = it.javaClass.simpleName
            if (!TextUtils.equals("LoginActivity", simpleName)) {
                Loger.e(TAG, "finishOtherThenLoginActivity()...simpleName = " + simpleName)
                it.finish()
            }
        }
    }

    fun finishLoginAuthActivity() {
        allActivitys.forEach {
            val simpleName = it.javaClass.simpleName
            if (TextUtils.equals("LoginAuthActivity", simpleName)) {
                it.finish()
            }
        }
    }

    fun finishVideoWebActivity() {
        allActivitys.forEach {
            val simpleName = it.javaClass.simpleName
            if (TextUtils.equals("VideoWebActivity", simpleName)) {
                it.finish()
            }
        }
    }

    fun finishWebActivity() {
        allActivitys.forEach {
            val simpleName = it.javaClass.simpleName
            if (TextUtils.equals("WebActivity", simpleName)) {
                it.finish()
            }
        }
    }

    fun finishAllActivity() {
        allActivitys.forEach {
            it.finish()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        mCurrentActivity = null
        allActivitys.clear()
    }
}