package com.ydj.lib.common.util

import com.victor.lib.widget.util.SharedPreferencesUtils

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ConfigLocal
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

object ConfigLocal {
    private const val PLAY_SIDE_GUIDE = "PLAY_SIDE_GUIDE"
    private const val COURSE_STUDY_SHOW_GUIDE = "COURSE_STUDY_SHOW_GUIDE"

    /**
     * 是否显示播放滑动提示
     * 针对用户
     *
     * @return
     */
    fun needShowPlaySideGuide(userId: String?): Boolean {
        return SharedPreferencesUtils.getBoolean("$PLAY_SIDE_GUIDE:$userId", true)
    }

    fun updateShowPlaySideGuide(userId: String?, enable: Boolean) {
        SharedPreferencesUtils.putBoolean("$PLAY_SIDE_GUIDE:$userId", enable)
    }
}