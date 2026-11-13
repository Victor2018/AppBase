# 编译配置与约定插件

> 决定新模块的 `build.gradle.kts` 怎么写。原则：**工程已有配置优先**，有约定插件时只声明差异，没有插件时照抄同类模块。

## 1. 先判定工程形态

| 判定 | 结论 |
|---|---|
| 有 `build-logic/`，且 `settings.gradle.kts` 中 `pluginManagement { includeBuild("build-logic") }` | 用约定插件，模块只写 `plugins` + `namespace` + 私有依赖 |
| 无 `build-logic/`（如 AppBase） | 照抄工程现有 lib 模块的显式 AGP 配置（见第 6 节） |
| 拿不准 | 打开同类现有模块的 `build.gradle.kts`，按它的写法复制并替换名称 |

## 2. 三个约定插件的职责

| 插件 id | version catalog alias | 用于 | 注入内容 |
|---|---|---|---|
| `com.ydj.convention.base` | 不直接引用 | 由 module/lib/application 内部 apply | `kotlin-android`、KSP、`core-ktx`/`appcompat`/`material`、`arouter-api` + ARouter KSP、`libs/*.jar`、测试库、`AROUTER_MODULE_NAME` |
| `com.ydj.convention.lib` | `libs.plugins.ydj.android.lib` | 基础库 | `com.android.library` + base；release 关闭混淆并挂 `proguard-rules.pro`；**不自动依赖 `lib_common`** |
| `com.ydj.convention.module` | `libs.plugins.ydj.android.module` | 业务模块 | 按 `AppBuildConfig.isModule` 应用 `application` 或 `library`；base + room；设置 `resourcePrefix`；切换双清单；`api(project(":lib_common"))` |

基础插件 `configureKotlinAndroid` 统一注入：

- `compileSdk = 34`、`minSdk = 23`
- Java 17（`sourceCompatibility` / `targetCompatibility` / `jvmTarget`），开启 core library desugaring
- `viewBinding = true`、`buildConfig = true`
- `jniLibs.srcDirs("libs")`
- 渠道 flavors：`beta`、`dev`、`_test`、`online`、`huawei`、`xiaomi`、`oppo`、`vivo`、`tencent`，并写入 `MODEL_*`、`UMENG_CHANNEL`、Bugly 占位符
- ARouter KSP 参数 `AROUTER_MODULE_NAME = project.name`

因此业务模块与基础库都**不要**再写这些配置，否则重复且可能与插件冲突。

## 3. 业务模块：集成模式与组件模式

`com.ydj.convention.module` 依据 `build-logic` 中 `AppBuildConfig.isModule` 切换：

| 开关 | 应用的插件 | 使用的清单 | 说明 |
|---|---|---|---|
| `isModule = false`（默认，集成） | `com.android.library` | `src/main/AndroidManifest.xml` | 由 app 统一打包，模块清单只声明组件 |
| `isModule = true`（组件） | `com.android.application` | `src/main/module/AndroidManifest.xml` | 模块独立运行，主页面需 LAUNCHER |

同时 app 的依赖要分支：

```kotlin
dependencies {
    if (AppBuildConfig.isModule) {
        implementation(project(":lib_common"))
        implementation(project(":lib_coremodel"))
    } else {
        implementation(project(":module_home"))
        implementation(project(":module_business"))
    }
}
```

- 切换开关是改 `build-logic/.../AppBuildConfig.kt` 的常量；验证完按工程约定恢复默认值。
- 新业务模块加入后，集成模式分支必须补 `implementation(project(":module_<业务>"))`，否则 app 看不到页面。
- 组件模式下模块通过 `lib_common` 的 api 传递拿到 `lib_coremodel`，不要在模块里重复依赖。

## 4. 资源前缀

- `com.ydj.convention.module` 自动设置：

  ```kotlin
  resourcePrefix = "${project.name.lowercase().replace("module_", "")}_"
  ```

  例：`module_message` → `message_`。
- 它是资源命名的 lint 约束，不会自动改名，也不会让现有模块编译失败；现有模块的布局仍按 `activity_` / `fragment_` / `dlg_` / `rv_` 命名。
- 新模块规则：
  - 布局、Drawable、动画仍按 `android-naming` 的类型前缀命名。
  - 模块专属的字符串、图片、颜色加业务前缀（`message_`、`wallet_`），避免与其它模块重名。
  - `lib_` 插件不设置 `resourcePrefix`，基础库资源自行保证唯一。
  - 新增资源前先全局搜索同名资源；通用颜色、Drawable 仍放 `lib_common`。

## 5. 依赖方向

```
app ──> module_*（集成模式）
module_* ──> lib_common（插件 api 注入）
lib_common ──api──> lib_coremodel ──api──> lib_http
lib_common ──api──> lib_luban
lib_* ──> lib_common / 其它 lib_*（implementation，按需）
```

- 禁止 `lib_* → module_*`。
- 禁止 `module_* → module_*`；跨模块跳转走 ARouter。
- 业务模块的私有能力依赖（如支付）用 `implementation(project(":lib_pay"))`。
- 第三方依赖统一走 `gradle/libs.versions.toml`，不写死版本。

## 6. 无约定插件工程（AppBase 风格）

以现有 `lib_base` 为模板，基础库 `build.gradle.kts`：

```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.victor.lib.<能力>"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```

要求：

- 只加新模块真正需要的依赖，不复制模板里的示例依赖。
- 版本、插件完全沿用工程现有 catalog 与 root `build.gradle.kts`，不升级、不新增插件。
- 没有 `maven-publish` 需求就不要加发布配置。
- 业务模块在没有约定插件时：照抄 app 的 AGP 配置片段（`namespace` 换成本模块），使用单一清单 `src/main/AndroidManifest.xml`；工程没有 ARouter 就跳过 `@Route`，跨模块按现有导航方式（显式 Intent 等）接入。
- 建好后仍需 `settings.gradle.kts` 的 `include(":模块名")` 与 app 的 `implementation(project(":模块名"))`。

## 7. 验证

```bash
./gradlew :<模块名>:assembleDebug          # 单模块编译
./gradlew :app:assembleDebug               # 集成模式打包
```

组件模式额外验证：

1. 将 `AppBuildConfig.isModule` 改为 `true`。
2. `./gradlew :module_<业务>:assembleDebug`，安装后确认能从 launcher 独立启动。
3. 恢复 `isModule = false`，再跑一次 `:app:assembleDebug`，确认集成模式未受影响。
