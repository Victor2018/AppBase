package com.victor.lib.widget.module

import android.app.Application

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WidgetModule
 * Author: Victor
 * Date: 2026/9/24 10:37
 * Description: 
 * -----------------------------------------------------------------
 */

class WidgetModule {
    var mApplication: Application? = null

    private object Holder {
        val instance = WidgetModule()
    }

    companion object {
        val instance: WidgetModule by lazy { Holder.instance }
    }

    fun init(application: Application) {
        mApplication = application
    }
}