# 根工程与 build-logic

> 参考工程：`D:\GitProjects\ydj-driver-android`。所有模板中的 `<域>`、`<工程名>`、`<应用名>` 按 `android-naming` 替换。

## 1. settings.gradle.kts

```kotlin
pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("${rootProject.projectDir}/repo")
            name = "LocalMavenRepo"
        }
        google()
        mavenCentral()
        maven("https://www.jitpack.io")
        maven("https://developer.huawei.com/repo/")
        flatDir {
            dirs("${rootDir}/lib_coremodel/libs")
        }
    }
}

rootProject.name = "<工程名>"

include(":app")
include(":lib_http")
include(":lib_coremodel")
include(":lib_common")
include(":module_home")
```

规则：

- `includeBuild("build-logic")` 必须存在，否则约定插件无法解析。
- `FAIL_ON_PROJECT_REPOS` 表示仓库只在 settings 统一声明，模块里不写 `repositories {}`。
- 本地 `repo/`、`flatDir` 只在工程确实有本地 AAR 时保留，没有就删掉。
- `include` 写 `:目录名`，模块顺序按 `app` → 基础库 → 业务模块。

## 2. 根 build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.ksp) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
}
```

- 根脚本只登记插件，不写 `allprojects` / `subprojects` 依赖，不在根配 SDK 与混淆。

## 3. gradle.properties

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.enableJetifier=true
android.nonTransitiveRClass=true
kotlin.code.style=official
kotlin.incremental=true
kotlin.parallel.tasks.in.project=true

org.gradle.daemon=true
org.gradle.configureondemand=true
org.gradle.parallel=true
org.gradle.caching=true

ksp.incremental=true
ksp.incremental.intermodule=true
```

- 其余项目（kapt、firebase、厂商推送）按需追加，不整份照抄参考工程。

## 4. gradle/libs.versions.toml

结构固定为四段：

```toml
[versions]
agp = "8.9.0"
kotlin = "2.1.20"
ksp = "2.1.20-1.0.31"
# ... 其余版本

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "core_ktx" }
# ... 其余依赖

[bundles]
ktx = ["androidx-core-ktx", "androidx-appcompat"]
# ... 其余 bundle

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
jetbrains-kotlin-ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
androidx-room = { id = "androidx.room", version.ref = "room" }
android-library = { id = "com.android.library", version.ref = "agp" }
jetbrains-kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "jetbrainsKotlinJvm" }

# 本工程约定插件，只写在模块里
ydj-android-lint = { id = "com.ydj.convention.lint" }
ydj-android-base = { id = "com.ydj.convention.base" }
ydj-android-application = { id = "com.ydj.convention.application" }
ydj-android-lib = { id = "com.ydj.convention.lib" }
ydj-android-module = { id = "com.ydj.convention.module" }
ydj-open-source = { id = "com.ydj.convention.open.source" }
ydj-android-room = { id = "com.ydj.convention.android.room" }
ydj-jvm-library = { id = "com.ydj.convention.jvm.library" }
```

- 约定插件别名（`ydj-*`）只用于模块的 `plugins {}`，不在根脚本 `apply false`。
- 约定插件里 `libs.findLibrary(...)` / `findBundle(...)` 用到的名字必须先在版本目录存在，否则配置阶段直接失败。

## 5. build-logic

> 本节是建工程时用的最小接入模板。build-logic 的完整目录、插件职责、support 配置层与排查见 `android-build-logic`；只新增或修改插件时直接用它，不重建本节结构。

### 5.1 build-logic/settings.gradle.kts

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
```

### 5.2 build-logic/convention/build.gradle.kts

```kotlin
plugins {
    `kotlin-dsl`
}

group = "com.<域>.convention.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidBase") {
            id = libs.plugins.ydj.android.base.get().pluginId
            implementationClass = "com.<域>.convention.AndroidBaseConventionPlugin"
        }
        register("androidApplication") {
            id = libs.plugins.ydj.android.application.get().pluginId
            implementationClass = "com.<域>.convention.AndroidApplicationConventionPlugin"
        }
        register("androidLib") {
            id = libs.plugins.ydj.android.lib.get().pluginId
            implementationClass = "com.<域>.convention.AndroidLibConventionPlugin"
        }
        register("androidModule") {
            id = libs.plugins.ydj.android.module.get().pluginId
            implementationClass = "com.<域>.convention.AndroidModuleConventionPlugin"
        }
        register("openSource") {
            id = libs.plugins.ydj.open.source.get().pluginId
            implementationClass = "com.<域>.convention.OpenSourceConventionPlugin"
        }
        register("androidLint") {
            id = libs.plugins.ydj.android.lint.get().pluginId
            implementationClass = "com.<域>.convention.AndroidLintConventionPlugin"
        }
        register("jvmLibrary") {
            id = libs.plugins.ydj.jvm.library.get().pluginId
            implementationClass = "com.<域>.convention.JvmLibraryConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.ydj.android.room.get().pluginId
            implementationClass = "com.<域>.convention.AndroidRoomConventionPlugin"
        }
    }
}
```

- `group` 用 `com.<域>.convention.buildlogic`；实现类包名与目录一致。
- 注册 id 必须与版本目录 `ydj-*` 的 `id` 完全一致。

### 5.3 插件职责

下表只用于建工程时确认插件集合；单个插件的实现、support 配置层与新增插件步骤见 `android-build-logic/references/convention-plugins.md`。

| 别名 | id | 职责 |
|---|---|---|
| `ydj.android.base` | `com.ydj.convention.base` | Kotlin + KSP、基础库依赖、ARouter api/ksp |
| `ydj.android.application` | `com.ydj.convention.application` | application + base + room、签名、buildTypes、导航依赖、自动分发 |
| `ydj.android.lib` | `com.ydj.convention.lib` | library + base + `configureKotlinAndroid` |
| `ydj.android.module` | `com.ydj.convention.module` | `isModule` 切换 application/library、base + room、`resourcePrefix`、双清单、`api(:lib_common)` |
| `ydj.open.source` | `com.ydj.convention.open.source` | 第三方依赖 bundle，按模块需要显式 apply（参考工程只有 `lib_coremodel` 用） |
| `ydj.android.room` | `com.ydj.convention.android.room` | Room + KSP + `schemas` 目录 |
| `ydj.android.lint` | `com.ydj.convention.lint` | lint 报告（xml/sarif），`checkDependencies = true` |
| `ydj.jvm.library` | `com.ydj.convention.jvm.library` | JVM 模块的 Kotlin 配置 |

- `base` 只被其它约定插件 apply，模块不直接 apply。
- `application` 与 `module` 已包含 `base` 和 `room`，模块里不重复声明。

## 6. 根构建脚本

```bat
:: build_beta.bat
./gradlew assembleBetaDebug
```

```bat
:: build_test.bat
./gradlew assemble_TestDebug
```

```bat
:: build_online.bat
./gradlew assembleOnlineRelease
```

- 文件名与 flavor 名一一对应；flavor `_test` 首字符是下划线，任务名保持 `assemble_TestDebug`。
- 渠道包（huawei/xiaomi/oppo/vivo/tencent）按需追加脚本，不默认生成。

## 7. 注册新模块

1. `settings.gradle.kts` 追加 `include(":module_xxx")`。
2. `app/build.gradle.kts` 的集成分支追加 `implementation(project(":module_xxx"))`。
3. 组件模式分支只依赖 `lib_common` / `lib_coremodel`，不加业务模块。
4. `lib_` 模块按需被依赖，不默认写进 app。
