# 编译配置（build-logic）

> 参考工程：`build-logic/convention/src/main/java/com/ydj/support/`。本文件给出模板与规则，具体版本号以目标工程版本目录为准。

## 1. AppBuildConfig

```kotlin
package com.<域>.support

import org.gradle.api.JavaVersion
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object AppBuildConfig {
    /** 集成开发模式 / 组件开发模式切换开关 */
    const val isModule = false

    const val compileSdk = 34
    const val minSdk = 23
    const val targetSdk = 34
    val javaVersion = JavaVersion.VERSION_17

    /** 版本号与编译号 */
    const val VERSION_NAME = "1.0.0"
    const val VERSION_CODE = 1
    const val BUILD_NUMBER = 1

    /** APK 文件名前缀 */
    const val BUILD_NAME = "<应用名小写下划线>"

    fun getBuildDate(): String {
        return LocalDateTime.now(ZoneId.of("GMT+8"))
            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd"))
    }
}
```

规则：

- `isModule` 是唯一模式开关，默认 `false`；验证组件模式后改回。
- `compileSdk` / `minSdk` / `targetSdk` / `javaVersion` 只在这里定义，模块不重复。
- `VERSION_NAME` / `VERSION_CODE` / `BUILD_NUMBER` 每次发版在此递增，不在 app 里写死。

## 2. AppSignConfig

```kotlin
package com.<域>.support

object AppSignConfig {
    const val STORE_FILE = "../docs/<工程>_keystore.jks"
    const val KEY_ALIAS = "<alias>"

    val KEY_PASSWORD: String = System.getenv("KEY_PASSWORD") ?: ""
    val STORE_PASSWORD: String = System.getenv("STORE_PASSWORD") ?: ""
}
```

规则：

- 密码不进仓库：走环境变量、`~/.gradle/gradle.properties` 或本地未提交的 `keystore.properties`。
- keystore 放 `docs/` 并加入 `.gitignore`；不复制参考工程的签名文件与密码。
- 参考工程把密码写在 `AppSignConfig` 常量里，属于历史做法，新工程不要照抄。

## 3. configureKotlinAndroid

放在 `support/KotlinAndroid.kt`，被 `application` / `lib` / `module` 三个约定插件复用：

```kotlin
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileSdk = AppBuildConfig.compileSdk

        defaultConfig {
            minSdk = AppBuildConfig.minSdk
            splits {
                abi {
                    isEnable = false
                    reset()
                    include("arm64-v8a", "armeabi-v7a")
                }
            }
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                buildConfigField("Boolean", "MODEL_DEBUG", "false")
            }
            getByName("debug") {
                buildConfigField("Boolean", "MODEL_DEBUG", "true")
            }
        }

        compileOptions {
            sourceCompatibility = AppBuildConfig.javaVersion
            targetCompatibility = AppBuildConfig.javaVersion
            isCoreLibraryDesugaringEnabled = true
        }

        buildFeatures {
            viewBinding = true
            buildConfig = true
        }

        sourceSets.getByName("main").jniLibs.srcDirs("libs")

        flavorDimensions += "ydj"
        productFlavors {
            create("dev") { dimension = "ydj" /* MODEL_* / UMENG_CHANNEL */ }
            create("_test") { dimension = "ydj" }
            create("beta") { dimension = "ydj" }
            create("online") { dimension = "ydj" }
            create("huawei") { dimension = "ydj" }
            create("xiaomi") { dimension = "ydj" }
            create("oppo") { dimension = "ydj" }
            create("vivo") { dimension = "ydj" }
            create("tencent") { dimension = "ydj" }
        }
    }

    configureKotlin()
    configureArouterModule()

    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    dependencies {
        add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
    }
}
```

- 每个 flavor 注入 `MODEL_ONLINE` / `MODEL_BETA` / `MODEL_DEV` / `MODEL_TEST` 与 `UMENG_CHANNEL`；推送相关 `manifestPlaceholders` 按项目需要补。
- `configureArouterModule()` 统一设置 `ksp { arg("AROUTER_MODULE_NAME", project.name) }`。
- Kotlin 编译参数统一在 `configureKotlin()` 设置 `jvmTarget`，不在模块里写 `kotlinOptions`。

## 4. flavors

| flavor | 用途 | MODEL_ONLINE | MODEL_DEV | MODEL_TEST | 任务示例 |
|---|---|---|---|---|---|
| `dev` | 开发联调 | false | true | false | `assembleDevDebug` |
| `_test` | 测试环境 | false | false | true | `assemble_TestDebug` |
| `beta` | 灰度 | false | false | false | `assembleBetaDebug` |
| `online` | 正式 | true | false | false | `assembleOnlineRelease` |
| `huawei` / `xiaomi` / `oppo` / `vivo` / `tencent` | 渠道包 | true | false | false | `assembleHuaweiRelease` |

- flavor 名全小写，只有 `_test` 带前导下划线（历史约定，任务名随之变化）。
- 环境判断只读 `BuildConfig.MODEL_*`，不在代码里判断 flavor 名。

## 5. isModule 双模式

| 位置 | `isModule = true`（组件模式） | `isModule = false`（集成模式） |
|---|---|---|
| `module_*` 插件 | 应用 `com.android.application`，业务模块各自运行 | 应用 `com.android.library` |
| 模块清单 | `src/main/module/AndroidManifest.xml` | `src/main/AndroidManifest.xml` |
| `app` 依赖 | 只依赖 `lib_common`、`lib_coremodel` | 依赖全部 `module_*` |
| 启动入口 | 各模块自己的 launcher Activity | `app` 的主入口 |

- 切换只改 `AppBuildConfig.isModule`，不改模块脚本。
- 组件模式验证完必须切回工程约定值。

## 6. APK 输出命名

在 `app/build.gradle.kts` 中统一配置：

```kotlin
android {
    applicationVariants.all {
        val flavor = flavorName
        val buildType = this.buildType.name
        outputs.all {
            val outputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            outputImpl.outputFileName = "${AppBuildConfig.BUILD_NAME}_${flavor}_${buildType}" +
                    "_v${AppBuildConfig.VERSION_NAME}_${AppBuildConfig.VERSION_CODE}" +
                    "_${AppBuildConfig.getBuildDate()}_${AppBuildConfig.BUILD_NUMBER}.apk"
        }
    }
}
```

## 7. 验证

```bash
./gradlew :app:assembleDevDebug        # 集成模式
./gradlew :app:assembleOnlineRelease   # 正式包（含混淆配置）
```

- 改 `isModule = true` 后：`./gradlew :module_xxx:assembleDevDebug` 验证组件模式可独立运行。
- 配置阶段报 `findLibrary/findBundle` 找不到：版本目录缺条目，先补 `libs.versions.toml`。
- 报插件 id 无法解析：`settings.gradle.kts` 缺 `includeBuild("build-logic")` 或 `gradlePlugin` 未注册。
