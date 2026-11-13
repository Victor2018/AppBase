# 基础库模块模板（参考 lib_pay）

> 适用 `lib_<能力>`：无页面、与业务无关、可被任意模块依赖的能力库。编译配置由 `com.ydj.convention.lib` 注入。

## 1. 目录结构

```
lib_pay/
├── .gitignore
├── build.gradle.kts
├── consumer-rules.pro
├── proguard-rules.pro
└── src/
    ├── main/
    │   ├── AndroidManifest.xml                # 空清单
    │   └── java/com/ydj/lib/pay/
    │       ├── PayHelper.kt                   # 对外 API
    │       ├── data/                          # 模型，可按实现分子包
    │       │   ├── ali/
    │       │   └── wx/
    │       └── interfaces/                    # 回调接口
    ├── test/java/com/ydj/lib/pay/ExampleUnitTest.kt
    └── androidTest/java/com/ydj/lib/pay/ExampleInstrumentedTest.kt
```

- 能力入口放模块根包，类型后缀按用途：`Helper`、`Manager`、`Util`。
- 模型放 `data/`；对接多个实现时按实现分子包（`ali/`、`wx/`），不按页面或业务分。
- 回调放 `interfaces/`，命名 `On<动作>Listener`。
- 不建 `view/activity`、`view/fragment` 等页面包；没有资源就不建 `res/`。

## 2. build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.ydj.android.lib)
}

android {
    namespace = "com.ydj.lib.pay"
}

dependencies {
    implementation(project(":lib_common"))
}
```

- `com.ydj.convention.lib` 不自动依赖 `lib_common`，需要时显式加；不需要公共能力就不写 `dependencies`。
- 依赖只允许指向 `lib_common` 或其它 `lib_`；禁止依赖任何 `module_`。
- 第三方 SDK 依赖按工程 `gradle/libs.versions.toml` 引用，不写死版本号。
- 不重复声明 `compileSdk`、`minSdk`、`buildTypes`、`buildFeatures`、Java/Kotlin 版本。

## 3. AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

</manifest>
```

- 基础库默认没有组件，空清单是正常状态。
- 确有组件时再声明，并同步确认权限与 `exported`；不要为了“完整”添加 Activity/Service。

## 4. 对外 API

```kotlin
class PayHelper(var activity: AppCompatActivity?, var listener: OnPayCompleteListener?) {
    private val TAG = "PayHelper"

    fun aliPay(orderInfo: String?) {
        // 能力实现
    }
}
```

- 依赖方需要的 Context、Activity、回调通过构造函数注入。
- 对外只暴露必要方法；内部实现类保持 `private` / `internal`。
- 不在库内持有全局单例上下文，不在初始化时读取业务配置。
- 线程切换、日志等公共能力复用 `lib_common` / `lib_coremodel` 的封装，不重复实现。

## 5. 回调接口

```kotlin
package com.ydj.lib.pay.interfaces

interface OnPayCompleteListener {
    fun OnPayComplete(msg: String?, isPaySuccess: Boolean)
    fun OnAuthComplete(msg: String?, isAuthSuccess: Boolean)
}
```

- 命名：`On` + 动作 + `Listener`，方法 `On<动作>Complete`。
- 参数可空并给默认语义，布尔结果命名 `isXxxSuccess`。
- 一个接口只描述一类回调；回调多了就拆接口，不要堆成“万能 Listener”。

## 6. 数据模型

```kotlin
package com.ydj.lib.pay.data.wx

class WxPayRes {
    var appid: String? = null
    var partnerId: String? = null
    var prepayId: String? = null
    var nonceStr: String? = null
    var timestamp: String? = null
    var sign: String? = null
}
```

- 字段可空 + 默认 `null`；字段名与 SDK/接口文档一致。
- 后缀按用途：`Data`、`Info`、`Bean`、`Res`，见 `class-naming.md`。
- 库内模型不直接复用 `lib_coremodel` 的业务实体；需要转换时在库内转换。

## 7. 资源

- `com.ydj.convention.lib` 不设置 `resourcePrefix`，资源名需自行保证唯一。
- 需要资源时加能力前缀（如 `pay_`），优先复用 `lib_common` 的颜色、Drawable 和 Style。
- 不动 `lib_common` 的公共资源；确需新增公共资源时按 `android-naming` 的颜色/Drawable 规范提交。

## 8. 自检

- [ ] 目录、`include`、`namespace`、源码目录一致
- [ ] 空清单，无页面组件、无 `@Route`
- [ ] 对外 API 在模块根包，模型在 `data/`，回调在 `interfaces/`
- [ ] 只依赖 `lib_common` / 其它 `lib_`，无 `module_` 依赖
- [ ] 无重复编译配置，依赖走 version catalog
- [ ] 资源（如有）带能力前缀且不与现有资源重名
- [ ] `./gradlew :lib_<能力>:assembleDebug` 通过
