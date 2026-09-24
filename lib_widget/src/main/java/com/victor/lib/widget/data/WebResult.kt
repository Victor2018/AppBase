package com.victor.lib.widget.data

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WebResult
 * Author: Victor
 * Date: 2026/2/24 14:58
 * Description: 
 * -----------------------------------------------------------------
 */

data class WebResult<T>(
    val status: String, // 200-发送请求成功 400-发送请求失败 0-调用方法成功 1-调用方法失败
    val message: String?,
    val data: T?
) {
    companion object {
        fun <T> requestSuccess(data: T? = null, message: String? = "Request Success"): WebResult<T> {
            return WebResult("200", message, data)
        }

        fun <T> requestFailure(message: String? = "Request Failure", data: T? = null): WebResult<T> {
            return WebResult("400", message, data)
        }

        fun <T> methodSuccess(data: T? = null, message: String? = "Method Success"): WebResult<T> {
            return WebResult("0", message, data)
        }

        fun <T> methodFailure(message: String? = "Method Failure", data: T? = null): WebResult<T> {
            return WebResult("1", message, data)
        }

        // 通用方法，根据状态码返回对应的 Result
        fun <T> of(status: String, data: T? = null, message: String? = "Success"): WebResult<T> {
            return WebResult(status, message, data)
        }
    }
}

