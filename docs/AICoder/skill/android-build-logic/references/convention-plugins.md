# 约定插件：职责与代码骨架

## 1. apply 关系

```
模块 plugins {}
├── ydj.android.application   → com.android.application + base + room
│                                + 签名、buildTypes、导航依赖、构建完成分发
├── ydj.android.module        → isModule ? application : library
│                                + base + room + resourcePrefix + 双清单 + api(:lib_common)
├── ydj.android.lib           → com.android.library + base
├── ydj.open.source           → 第三方依赖集合（按需显式 apply）
├── ydj.android.room          → androidx.room + KSP
├── ydj.android.lint          → lint 报告
└── ydj.jvm.library           → 纯 JVM 模块 Kotlin 配置

base（内部插件）              → kotlin.android + KSP + 基础依赖 + ARouter
```

- `base` 不被模块直接 apply，必须由 `application` / `lib` / `module` 组合进来。
- `application` 与 `module` 已包含 `base`、`room`，模块不重复声明。
- 通用编译配置（SDK、flavors、desugaring、ViewBinding）在 `configureKotlinAndroid`，多个插件共享。

## 2. AndroidBaseConventionPlugin

```kotlin
class AndroidBaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.android")
            apply(plugin = "com.google.devtools.ksp")

            dependencies {
                "api"(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
                "api"(libs.findLibrary("androidx.core.ktx").get())
                "api"(libs.findLibrary("androidx.appcompat").get())
                "api"(libs.findLibrary("material").get())
                "testApi"(libs.findLibrary("junit").get())
                "androidTestApi"(libs.findLibrary("androidx.junit").get())
                "androidTestApi"(libs.findLibrary("androidx.espresso.core").get())
                "api"(libs.findLibrary("arouter.api").get())
                "ksp"(libs.findLibrary("arouter.ksp.compiler").get())
            }
        }
    }
}
```

- 只放所有模块都需要的：Kotlin、KSP、AndroidX 基础、ARouter。
- 业务库、页面库不进 `base`。

## 3. AndroidApplicationConventionPlugin

```kotlin
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "com.<域>.convention.base")
            apply(plugin = "com.<域>.convention.android.room")

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.minSdk = AppBuildConfig.minSdk
                defaultConfig.targetSdk = AppBuildConfig.targetSdk
                defaultConfig.versionName = AppBuildConfig.VERSION_NAME
                defaultConfig.versionCode = AppBuildConfig.VERSION_CODE

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

            dependencies {
                "implementation"(libs.findLibrary("androidx.constraintlayout").get())
                "implementation"(libs.findLibrary("androidx.navigation.fragment.ktx").get())
                "implementation"(libs.findLibrary("androidx.navigation.ui.ktx").get())
            }
        }
    }
}
```

- 参考工程在 `buildFinished` 里调用 `autoDistributeTestApk(taskNames)`；只在确实需要自动分发时保留，否则去掉，避免每次构建都触发上传。
- release 的 `isMinifyEnabled` 与 `proguardFiles` 已由 `configureKotlinAndroid` 统一设置，这里不重复写。
- 签名取不到时不要硬编码密码，按 `support-utils.md` 的凭证规则处理。
- 版本号只从 `AppBuildConfig` 读，app 脚本不再写第二份。

## 4. AndroidLibConventionPlugin

```kotlin
class AndroidLibConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "com.<域>.convention.base")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
            }
        }
    }
}
```

## 5. AndroidModuleConventionPlugin

```kotlin
class AndroidModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            if (AppBuildConfig.isModule) {
                apply(plugin = "com.android.application")
            } else {
                apply(plugin = "com.android.library")
            }
            apply(plugin = "com.<域>.convention.base")
            apply(plugin = "com.<域>.convention.android.room")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)

                // 资源前缀由目录名推导，避免跨模块资源重名
                resourcePrefix =
                    "${project.name.lowercase(Locale.getDefault()).replace("module_", "")}_"

                sourceSets {
                    getByName("main") {
                        jniLibs.srcDirs("libs")
                        if (AppBuildConfig.isModule) {
                            manifest.srcFile("src/main/module/AndroidManifest.xml")
                        } else {
                            manifest.srcFile("src/main/AndroidManifest.xml")
                        }
                    }
                }
            }

            dependencies {
                "api"(project(":lib_common"))
            }
        }
    }
}
```

- `isModule = true` 时模块以 application 运行，必须有 `src/main/module/AndroidManifest.xml` 且带 launcher。
- `api(project(":lib_common"))` 让业务模块通过 `lib_common` 传递依赖使用网络链路，不直接依赖 `lib_coremodel`。
- 模块私有依赖写在模块脚本，不写进这个插件。

## 6. AndroidRoomConventionPlugin

```kotlin
class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "androidx.room")
            apply(plugin = "com.google.devtools.ksp")

            extensions.configure<KspExtension> {
                arg("room.generateKotlin", "true")
            }

            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/schemas")
            }

            dependencies {
                "implementation"(libs.findLibrary("room.runtime").get())
                "implementation"(libs.findLibrary("room.ktx").get())
                "ksp"(libs.findLibrary("room.compiler").get())
            }
        }
    }
}
```

- `schemas` 目录要提交仓库，Room 自动迁移依赖它。

## 7. AndroidLintConventionPlugin

```kotlin
class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.hasPlugin("com.android.application") ->
                    configure<ApplicationExtension> { lint(Lint::configure) }

                pluginManager.hasPlugin("com.android.library") ->
                    configure<LibraryExtension> { lint(Lint::configure) }

                else -> {
                    apply(plugin = "com.android.lint")
                    configure<Lint>(Lint::configure)
                }
            }
        }
    }
}

private fun Lint.configure() {
    xmlReport = true
    sarifReport = true
    checkDependencies = true
    disable += "GradleDependency"
}
```

- 自适应 Android 应用、Android 库、纯 JVM 三种宿主；`JvmLibraryConventionPlugin` 会 apply 它。

## 8. OpenSourceConventionPlugin

把一组通用第三方依赖收进一个可选插件，避免每个模块重复抄：

```kotlin
class OpenSourceConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                "api"(libs.findBundle("ktx").get())
                "api"(libs.findBundle("coroutines").get())
                "api"(libs.findBundle("lifecycle").get())
                "api"(libs.findBundle("retrofit").get())
                "api"(libs.findBundle("okhttp3").get())
                "api"(libs.findLibrary("glide.runtime").get())
                "ksp"(libs.findLibrary("glide.compiler").get())
                "testApi"(libs.findLibrary("coroutines.test").get())
                // 其余按工程实际增减：多 dex、下拉刷新、友盟、CameraX、二维码、分享、图片选择、裁剪、OSS、定位、支付
            }
        }
    }
}
```

- 只有确实需要这组能力的模块才 apply，例如网络层与图片较多的 `lib_coremodel`。
- 依赖用 `libs.find*`，不写死坐标和版本。

## 9. JvmLibraryConventionPlugin

```kotlin
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.<域>.convention.lint")
            configureKotlinJvm()
        }
    }
}
```

- `configureKotlinJvm()` 只设置 Java 版本和 Kotlin 编译参数，不 apply `org.jetbrains.kotlin.jvm`。
- 使用前确认模块自己已 apply `org.jetbrains.kotlin.jvm`；若工程希望插件自包含，就在插件里 `apply("org.jetbrains.kotlin.jvm")`，二选一并在注释里写清。
- 参考工程当前由模块侧声明，插件里对应 apply 被注释掉，属于历史写法，不要照抄成“无 Kotlin 插件也能编译”的假设。

## 10. 什么时候新增插件

| 诉求 | 做法 |
|---|---|
| 多个模块需要相同的插件 + 相同的依赖组合 | 新增约定插件 |
| 只是微调已有配置（SDK、flavor、编译参数） | 改 `support/KotlinAndroid.kt`，不加插件 |
| 只服务一个模块 | 写在模块脚本，不新增插件 |
| 一组可选三方依赖 | 加 bundle 并扩展现有 `open.source`，不再拆新插件 |

新增插件前先检查是否能用「已有插件 + 模块私有依赖」解决；能解决就不加。
