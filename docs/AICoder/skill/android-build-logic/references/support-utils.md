# support 配置层

`support/` 放所有插件共用的常量、配置函数和构建后任务；插件类只调用它们。

## 1. AppBuildConfig

```kotlin
object AppBuildConfig {
    /** 集成开发 / 组件开发切换开关 */
    const val isModule = false

    const val compileSdk = 34
    const val minSdk = 23
    const val targetSdk = 34
    val javaVersion = JavaVersion.VERSION_17

    const val BUILD_NAME = "<工程简写>"
    const val VERSION_NAME = "1.0.0"
    const val VERSION_CODE = 1
    const val BUILD_NUMBER = 1

    fun getBuildDate(): String =
        LocalDateTime.now(ZoneId.of("GMT+8"))
            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd"))
}
```

| 字段 | 用途 | 修改注意 |
|---|---|---|
| `isModule` | 业务模块以 application 还是 library 参与构建 | 切换后确认模块双清单都存在；验证完按工程约定切回 |
| `compileSdk` / `minSdk` / `targetSdk` | 全工程统一 SDK | 不在模块脚本重复声明 |
| `javaVersion` | Java/Kotlin 目标版本，默认 17 | 与 `convention/build.gradle.kts` 的 Java 17 保持一致 |
| `BUILD_NAME` | APK 文件名前缀、分发日志 | 全小写，与工程名对应 |
| `VERSION_NAME` / `VERSION_CODE` | 版本 | 只在发布时改，app 脚本 import 读取 |
| `BUILD_NUMBER` | 每次编译 +1，用于区分包 | 由人或 CI 递增，不自动写入 |

`isModule`、`VERSION_*` 是编译期常量，改动后需要重新 Sync。

## 2. AppSignConfig

参考工程把 keystore 路径、别名、**明文密码**写在 `AppSignConfig`，这是反面示例。新工程按下述方式实现：

```kotlin
object AppSignConfig {
    const val STORE_FILE = "../docs/<工程>_keystore.jks"
    const val KEY_ALIAS = "<alias>"

    val keyPassword: String get() = secret("KEY_PASSWORD")
    val storePassword: String get() = secret("STORE_PASSWORD")

    private fun secret(name: String): String =
        System.getenv(name) ?: ""
}
```

插件侧按“有凭证才配签名”处理：

```kotlin
extensions.configure<ApplicationExtension> {
    val store = rootProject.file(AppSignConfig.STORE_FILE)
    if (store.exists() && AppSignConfig.storePassword.isNotBlank()) {
        signingConfigs {
            create("release") {
                storeFile = store
                keyAlias = AppSignConfig.KEY_ALIAS
                keyPassword = AppSignConfig.keyPassword
                storePassword = AppSignConfig.storePassword
            }
        }
        buildTypes {
            getByName("debug") { signingConfig = signingConfigs.getByName("release") }
            getByName("release") { signingConfig = signingConfigs.getByName("release") }
        }
    }
}
```

红线：

- 不提交 keystore、`storePassword`、`keyPassword`、微信/推送签名摘要。
- 本地可用 `local.properties`（已 gitignore）承载，CI 用 Secret 注入同名环境变量。
- 文档、截图、示例代码中只出现占位符。

## 3. KotlinAndroid

一个文件承载三块共享配置：

### 3.1 configureKotlinAndroid(commonExtension)

| 配置 | 内容 | 说明 |
|---|---|---|
| `compileSdk` | `AppBuildConfig.compileSdk` | 统一 SDK |
| `defaultConfig.minSdk` | `AppBuildConfig.minSdk` | application 插件另设 targetSdk |
| `splits.abi` | 关闭 ABI 分包 | `isEnable = false` |
| `buildTypes` | `MODEL_DEBUG` 布尔字段 | debug `true`，release `false` |
| `compileOptions` | Java 17 + `isCoreLibraryDesugaringEnabled = true` | 依赖在函数末尾加 `coreLibraryDesugaring` |
| `buildFeatures` | `viewBinding = true`、`buildConfig = true` | 模块直接用 Binding 和 BuildConfig |
| `sourceSets.main.jniLibs` | `srcDirs("libs")` | 本地 so 目录 |
| `flavorDimensions` | `"ydj"` | 单维度多渠道 |
| `productFlavors` | `dev`、`_test`、`beta`、`online`、`huawei`、`xiaomi`、`oppo`、`vivo`、`tencent` | 见下表 |
| `manifestPlaceholders` | Bugly 的 `BUGLY_APPID` 等 | AppId 从本地或 CI 注入，不写死 |
| 函数结尾 | `configureKotlin()`、`configureArouterModule()` | Kotlin 参数与 ARouter 模块名 |

### 3.2 flavor 对照

| flavor | `MODEL_ONLINE` | `MODEL_BETA` | `MODEL_DEV` | `MODEL_TEST` | `UMENG_CHANNEL` |
|---|---|---|---|---|---|
| `dev` | false | false | true | false | `dev` |
| `_test` | false | false | false | true | `_test` |
| `beta` | false | true | false | false | `uat` |
| `online` | true | false | false | false | `online` |
| `huawei` | true | false | false | false | `huawei` |
| `xiaomi` | true | false | false | false | `xiaomi` |
| `oppo` | true | false | false | false | `oppo` |
| `vivo` | true | false | false | false | `vivo` |
| `tencent` | true | false | false | false | `tencent` |

业务代码只读 `BuildConfig.MODEL_ONLINE / MODEL_BETA / MODEL_DEV / MODEL_TEST`，不再自己判断渠道名。

### 3.3 configureKotlinJvm 与 configureKotlin

- `configureKotlinJvm()`：给纯 JVM 模块设 Java 版本并调用 `configureKotlin()`。
- `configureKotlin()`：`tasks.withType<KotlinCompile>` 设 `jvmTarget`、`allWarningsAsErrors`（读 `warningsAsErrors` 属性）、`-opt-in=kotlin.RequiresOptIn` 等编译参数。

### 3.4 configureArouterModule

```kotlin
fun Project.configureArouterModule() {
    extensions.configure<KspExtension> {
        arg("AROUTER_MODULE_NAME", project.name)
    }
}
```

模块名进 KSP 参数，ARouter 生成的路由表才能按模块归组；不要在模块里另写一份。

## 4. ProjectExtensions

```kotlin
val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")
```

- 插件里用 `libs.findLibrary("...")` / `findBundle("...")`，不直连坐标。
- 找不到时先检查 build-logic 的 `versionCatalogs` 是否挂载根工程目录。

## 5. 测试包分发

| 对象 | 作用 |
|---|---|
| `UploadApkConfig` | 分发开关、APK 文件名、手动分发的环境编号 |
| `UploadType` | 环境枚举：code、channel、buildType、taskName、平台 key |
| `TestApkDistribution` | 生成构建日志、加固、上传 fir / 蒲公英、构建结束自动分发 |

APK 文件名规则：

```
<BUILD_NAME>_<flavor>_<buildType>_v<VERSION_NAME>_<VERSION_CODE>_<BUILD_DATE>_<BUILD_NUMBER>.apk
```

自动分发：`AndroidApplicationConventionPlugin` 在 `buildFinished` 比较 `gradle.startParameter.taskNames` 与 `UploadType` 的 `taskName`，命中才上传。
手动分发：app 脚本注册任务：

```kotlin
tasks.register("apkUpload") {
    doLast { manualDistributeTestApk() }
}
```

红线：

- 参考工程 `UploadApkConfig` 中的 fir token、蒲公英 key 是真实值，**不复制**。
- 新工程改成 `System.getenv("FIR_TOKEN")` / `PGYER_KEY` 读取；为空时打印提示并跳过上传。
- 分发脚本路径（`docs/upToFir.py` 等）按工程实际调整，不假设 Python 环境一定存在；缺失时任务应失败并给出明确提示。
