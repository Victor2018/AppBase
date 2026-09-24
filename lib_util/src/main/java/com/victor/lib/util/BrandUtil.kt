package com.victor.lib.util

import android.os.Build

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BrandUtil
 * Author: Victor
 * Date: 2022/5/19 19:54
 * Description: 
 * -----------------------------------------------------------------
 */

object BrandUtil {
    /**
     * 判断是否为小米设备
     */
    fun isBrandXiaoMi(): Boolean {
        return ("xiaomi".equals(getBuildBrand(), ignoreCase = true)
                || "xiaomi".equals(getBuildManufacturer(), ignoreCase = true))
    }

    /**
     * 判断是否为华为设备
     */
    fun isBrandHuawei(): Boolean {
        return "huawei".equals(getBuildBrand(), ignoreCase = true) ||
                "huawei".equals(getBuildManufacturer(), ignoreCase = true) ||
                "honor".equals(getBuildBrand(), ignoreCase = true) ||
                "honor".equals(getBuildManufacturer(), ignoreCase = true)
    }

    /**
     * 判断是否为魅族设备
     */
    fun isBrandMeizu(): Boolean {
        return ("meizu".equals(getBuildBrand(), ignoreCase = true)
                || "meizu".equals(getBuildManufacturer(), ignoreCase = true)
                || "22c4185e".equals(getBuildBrand(), ignoreCase = true))
    }

    /**
     * 判断是否是 oppo 设备, 包含子品牌
     *
     * @return
     */
    fun isBrandOppo(): Boolean {
        return "oppo".equals(getBuildBrand(), ignoreCase = true) ||
                "realme".equals(getBuildBrand(), ignoreCase = true) ||
                "oneplus".equals(getBuildBrand(), ignoreCase = true) ||
                "oppo".equals(getBuildManufacturer(), ignoreCase = true) ||
                "realme".equals(getBuildManufacturer(), ignoreCase = true) ||
                "oneplus".equals(getBuildManufacturer(), ignoreCase = true)
    }

    /**
     * 判断是否是vivo设备
     *
     * @return
     */
    fun isBrandVivo(): Boolean {
        return ("vivo".equals(getBuildBrand(), ignoreCase = true)
                || "vivo".equals(getBuildManufacturer(), ignoreCase = true))
    }

    fun getBuildBrand(): String? {
        return Build.BRAND
    }

    fun getBuildManufacturer(): String? {
        return Build.MANUFACTURER
    }

    fun getBuildModel(): String? {
        return Build.MODEL
    }

    fun getBuildVersionRelease(): String? {
        return Build.VERSION.RELEASE
    }

    fun getBuildVersionSDKInt(): Int {
        return Build.VERSION.SDK_INT
    }
}