# Kotlin 代码风格规范 (Kotlin Code Style Specification)

> 本文档定义 Kotlin 代码的编写风格，不涉及命名。

## 文件头注释

每个 Kotlin 文件必须包含版权头注释：

```kotlin
/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: {FileName}
 * Author: Victor
 * Date: {YYYY/MM/DD} {HH:mm}
 * Description: {文件描述}
 * -----------------------------------------------------------------
 */
```

## 导入顺序

```kotlin
// 1. android.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View

// 2. androidx.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*

// 3. java.* / kotlin.*
import java.io.Serializable
import kotlin.collections.MutableList

// 4. 第三方库
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.annotations.SerializedName
import okhttp3.*
import retrofit2.*

// 5. 项目模块
import com.ydj.lib.common.base.BaseActivity
import com.ydj.lib.common.util.ToastUtils
import com.ydj.lib.http.data.HttpResult

// 6. 当前模块
import com.ydj.module.home.view.adapter.MessageAdapter

// 7. ViewBinding
import com.ydj.module.home.databinding.ActivityHomeBinding

// 8. R 类
import com.ydj.module.home.R
```

## 代码格式

### 缩进

```kotlin
// 使用 4 个空格缩进，不使用 Tab
fun initView() {
    binding.mRvList.adapter = mAdapter
    binding.mRvList.layoutManager = LinearLayoutManager(this)
}
```

### 大括号

```kotlin
// 左大括号跟在声明后面
fun initView() {
    // ...
}

if (condition) {
    // ...
}

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    // ...
}
```

### 空格

```kotlin
// 函数名和括号之间可以有空格（项目风格）
fun initializeSuper () {
    // ...
}

fun popBackStack () {
    // ...
}

// 标准 Kotlin 风格也可以
fun initView() {
    // ...
}

// 操作符两边加空格
val sum = a + b
val result = list.filter { it > 0 }
```

### 换行

```kotlin
// 参数过多时换行，使用尾随逗号
fun showDialog(
    title: String,
    content: String,
    positiveText: String,
    negativeText: String,
) {
    // ...
}
```

## 注释规范

### 行内注释

```kotlin
// 使用中文注释
//状态栏背景及字体颜色适配
private fun setupStatusBar() { ... }

//禁止app录屏和截屏
private fun disableScreenCapture() { ... }

//数据加载完毕,恢复标记,防止重复加载
private fun resetLoadingFlag() { ... }
```

### KDoc 注释

```kotlin
/**
 * 是否初始化过布局
 */
private var isViewInitiated = false

/**
 * 发送登录请求
 * @param phone 手机号
 * @param password 密码
 */
private fun sendLoginRequest(phone: String, password: String) { ... }
```

### 日志

```kotlin
// 使用自定义 Loger 包装类
// 格式：Loger.e(TAG, "methodName()......description = $value")
Loger.e(TAG, "sendLoginRequest()......phone = $phone")
Loger.e(TAG, "onResponse()......code = ${response.code}")
```

## 类结构顺序

```kotlin
class ExampleActivity : BaseActivity<ActivityExampleBinding>(ActivityExampleBinding::inflate) {

    // 1. Companion Object（静态方法/常量）
    companion object {
        fun intentStart(context: Context) { ... }
    }

    // 2. 成员变量（m前缀）
    private var mLoadingDialog: LoadingDialog? = null

    // 3. ViewModel（by viewModels 委托）
    private val homeVM by viewModels<HomeVM> {
        InjectorUtils.provideHomeVMFactory(this)
    }

    // 4. 生命周期方法
    override fun onCreate(savedInstanceState: Bundle?) { ... }
    override fun onDestroy() { ... }

    // 5. 初始化方法
    private fun initView() { ... }
    private fun initData() { ... }

    // 6. 订阅方法
    private fun subscribeUi() { ... }
    private fun subscribeEvent() { ... }

    // 7. 业务方法
    private fun sendLoginRequest() { ... }
    private fun showLogoutDlg() { ... }

    // 8. 内部类/接口
    inner class InnerClass { ... }
}
```

## Kotlin 特性使用

### 字符串模板

```kotlin
// 统一使用字符串模板
val message = "token = ${mLoginData?.token}"
val url = "${BASE_URL}/api/$endpoint"

// 不推荐字符串拼接
// ❌ val message = "token = " + mLoginData?.token
```

### 空安全

```kotlin
// 使用 ?. 和 ?: 处理空值
mLoginData?.let { data ->
    mTvNickName.text = data.nickName
}

val name = userInfo?.name ?: "未知用户"
```

### 作用域函数

```kotlin
// apply - 配置对象属性
val dialog = LoadingDialog(this).apply {
    setCancelable(false)
    setMessage("加载中...")
}

// let - 非空判断
mLoginData?.let { data ->
    mTvNickName.text = data.nickName
}

// also - 链式调用
val result = fetchData()
    .also { Loger.d(TAG, "fetchData result: $it") }

// run - 计算并返回
val status = run {
    if (condition) STATE_A else STATE_B
}
```

### 协程

```kotlin
// ViewModel 中发起请求
fun fetchUserInfo() {
    viewModelScope.launch {
        dataSource.getUserInfo()
    }
}

// DataSource 中切换线程
override suspend fun getUserInfo() {
    withContext(Dispatchers.Main) {
        userInfoData.value = fetchUserInfoFromApi()
    }
}

private suspend fun fetchUserInfoFromApi(): HttpResult<UserInfo> {
    return withContext(Dispatchers.IO) {
        handleResponse(
            ApiClient.getApiService(UserApiService::class.java).getUserInfo()
        )
    }
}
```

## 错误处理

```kotlin
// 简单捕获并打印日志
try {
    val result = riskyOperation()
} catch (e: Exception) {
    e.printStackTrace()
}

// 带错误提示
try {
    parseData(json)
} catch (e: JSONException) {
    e.printStackTrace()
    ToastUtils.show("数据解析失败")
}
```

## 代码格式速查表

| 项目 | 规范 |
|------|------|
| 缩进 | 4 空格 |
| 大括号 | 左括号跟声明 |
| 字符串 | 使用模板 `${var}` |
| 空安全 | 使用 `?.` 和 `?:` |
| 注释语言 | 中文 |
| 日志格式 | `Loger.e(TAG, "method()......msg")` |
| 协程 | `viewModelScope.launch` |
| 线程切换 | `withContext(Dispatchers.IO/Main)` |
