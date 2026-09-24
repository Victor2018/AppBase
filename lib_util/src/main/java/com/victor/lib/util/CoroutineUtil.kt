package com.victor.lib.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun runOnMain(block: suspend CoroutineScope.() -> Unit) = CoroutineScope(Dispatchers.Main).launch {
    try {
      block()
    } catch (e: Exception) {
      e.printStackTrace()
    }
}

fun CoroutineScope.runOnMain(block: suspend CoroutineScope.() -> Unit) = launch(Dispatchers.Main) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun runOnIO(block: suspend CoroutineScope.() -> Unit) = CoroutineScope(Dispatchers.IO).launch {
    try {
      block()
    } catch (e: Exception) {
      e.printStackTrace()
    }
}

fun CoroutineScope.runOnIO(block: suspend CoroutineScope.() -> Unit) = launch(Dispatchers.IO) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

