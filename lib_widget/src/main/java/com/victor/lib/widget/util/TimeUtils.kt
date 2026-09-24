package com.victor.lib.widget.util

import android.widget.TextView
import com.victor.lib.widget.R
import com.victor.lib.widget.module.WidgetModule
import java.text.DateFormat
import java.text.SimpleDateFormat

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TimeUtils
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

object TimeUtils {
    private const val TIME_ONEDAY = 24 * 60 * 60 * 1000.toLong()

    private val mDataTimeFormat: DateFormat = SimpleDateFormat("昨天 HH:mm")

    private val mLiveDataFormat: DateFormat = SimpleDateFormat("MM-dd HH:mm")

    var mMessageDataFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm")

    private val mLiveTimeFormat: DateFormat = SimpleDateFormat("HH:mm")

    private val nYearMonthDay: DateFormat = SimpleDateFormat("yyyyMMdd")

    private val mMMddHHmmFormat: DateFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm")

    fun getYesterDayTime(time: Long): String? {
        return mDataTimeFormat.format(time)
    }

    fun getLiveData(times: Long): String? {
        var times = times
        if (times == 0L) {
            times = System.currentTimeMillis()
        }
        return mLiveDataFormat.format(times)
    }

    fun getShowData(times: Long?, sdf: DateFormat?): String? {
        var times = times ?: 0L
        if (times == 0L) {
            times = System.currentTimeMillis()
        }
        return sdf?.format(times)
    }

    fun getLiveTime(times: Long?): String? {
        var times = times ?: 0L
        if (times == 0L) {
            times = System.currentTimeMillis()
        }
        return mLiveTimeFormat.format(times)
    }

    fun bindShowTime(tvTime: TextView, time: Long?) {
        bindShowTime(tvTime, time, mLiveDataFormat)
    }

    fun bindShowTime(
        tvTime: TextView,
        time: Long?,
        sdf: DateFormat?
    ) {
        var text = formatImTime(time)
        tvTime.text = text
    }

    fun formatImTime(time: Long?): String? {
        var text: String? = null
        val nowTime = System.currentTimeMillis()
        val dayTime = java.lang.Long.valueOf(nYearMonthDay.format(time))
        val nowdayTime = java.lang.Long.valueOf(nYearMonthDay.format(nowTime))
        val dateTimeInterval = nowdayTime - dayTime
        //判定日期间隔
        if (dateTimeInterval == 0L) {
            //今天
            text = getLiveTime(time)
        } else if (dateTimeInterval == 1L) {
            //昨天
            text = WidgetModule.instance.mApplication?.getString(R.string.yesterday) + getLiveTime(time)
        } else {
            text = getShowData(time, mLiveDataFormat)
        }
        return text
    }

    /**
     * 显示直播时长
     *
     * @param duration 单位秒
     * @return
     */
    fun getHMS(duration: Int): String? {
        val h = (duration / 60 / 60).toInt()
        val m = (duration / 60).toInt()
        val s = (duration % 60).toInt()
        return String.format("%02d:%02d:%02d", h, m, s)
    }

    /**
     * 时间转换格式 00:00
     * @param duration 单位：秒
     */
    fun getHM(duration: Int): String? {
        val h = (duration / 60 / 60).toInt()
        val m = (duration / 60).toInt()
        return String.format("%02d:%02d", h, m)
    }

    /**
     * 时间转换格式 00:00
     * @param duration 单位：秒
     */
    fun getMs(duration: Long): String? {
        return getMs(duration.toInt())
    }

    fun getMs(duration: Int): String? {
        val m = duration / 60
        val s = duration % 60
        return String.format("%02d:%02d", m, s)
    }

    fun getMMddHHmm(time: Long): String? {
        return mMMddHHmmFormat.format(time)
    }

    /**
     * 学习时间格式化 00:00
     * @param learningTime 单位：秒
     */
    fun getStudyTime(learningTime: Long): String? {
        val hours = learningTime / 60 / 60
        val mins = (learningTime - hours * 60 * 60) / 60
        return String.format("%02d:%02d", hours, mins)
    }

    /**
     * 学习时间 小时
     * @param learningTime 单位：秒
     */
    fun getStudyHour(learningTime: Long): String {
        val hours = learningTime / 60 / 60
        if (hours > 0) {
            return "${hours}小时"
        }
        return ""
    }
    /**
     * 学习时间 分钟
     * @param learningTime 单位：秒
     */
    fun getStudyMin(learningTime: Long): String {
        val hours = learningTime / 60 / 60
        val mins = (learningTime - hours * 60 * 60) / 60
        if (mins > 0) {
            return "${mins}分钟"
        } else {
            if (hours < 1) {
                return "0分钟"
            }
        }
        return ""
    }
}