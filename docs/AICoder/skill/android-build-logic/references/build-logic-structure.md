# build-logic 结构与接入

## 1. 目录职责

```
build-logic/                       # 独立 Gradle 构建
├── settings.gradle.kts            # 自己的仓库、版本目录、:convention
└── convention/                    # 唯一子项目
    ├── .gitignore                 # 内容：/build
    ├── build.gradle.kts           # kotlin-dsl + 插件注册
    └── src/main/java/com/<域>/
        ├── convention/            # 插件类
        └── support/               # 配置函数、常量、任务
```

- 不放 `buildSrc`，不拆第二个子项目。
- 源码放 `src/main/java/`，Kotlin 文件由 `kotlin-dsl` 编译。
- 包名与目录一致：`com/<域>/convention`、`com/<域>/support`。

## 2. build-logic/settings.gradle.kts

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

要点：

- `versionCatalogs` 必须指向根工程目录，build-logic 才与模块共用一套版本。
- `mavenCentral()` 不能省，Room、KSP 的插件依赖从这里解析。
- `rootProject.name` 固定 `build-logic`。

## 3. convention/build.gradle.kts

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

要点：

- `register("...")` 的名字只是 Gradle 内部标识，习惯用别名的 camelCase；真正对外的是 `id`。
- 四条 `compileOnly` 对应版本目录里的四个库别名：
  `android-gradlePlugin`、`kotlin-gradlePlugin`、`room-gradlePlugin`、`ksp-gradlePlugin`。
- 新增插件只追加一个 `register` 块，不改动已有块。

## 4. 版本目录（gradle/libs.versions.toml）

```toml
[versions]
agp = "8.x.x"
kotlin = "2.x.x"
ksp = "2.x.x-x.x.x"
room = "2.x.x"
jetbrainsKotlinJvm = "2.x.x"

[libraries]
android-gradlePlugin = { group = "com.android.tools.build", name = "gradle", version.ref = "agp" }
kotlin-gradlePlugin = { group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version.ref = "kotlin" }
room-gradlePlugin = { group = "androidx.room", name = "room-gradle-plugin", version.ref = "room" }
ksp-gradlePlugin = { group = "com.google.devtools.ksp", name = "com.google.devtools.ksp.gradle.plugin", version.ref = "ksp" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
jetbrains-kotlin-ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
androidx-room = { id = "androidx.room", version.ref = "room" }
jetbrains-kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "jetbrainsKotlinJvm" }

# 本工程约定插件：不写 version
ydj-android-base = { id = "com.<域>.convention.base" }
ydj-android-application = { id = "com.<域>.convention.application" }
ydj-android-lib = { id = "com.<域>.convention.lib" }
ydj-android-module = { id = "com.<域>.convention.module" }
ydj-android-room = { id = "com.<域>.convention.android.room" }
ydj-android-lint = { id = "com.<域>.convention.lint" }
ydj-open-source = { id = "com.<域>.convention.open.source" }
ydj-jvm-library = { id = "com.<域>.convention.jvm.library" }
```

- 别名里的 `-` 在 Kotlin 脚本中变成 `.`：`ydj-android-module` → `libs.plugins.ydj.android.module`。
- 插件类里 `libs.findLibrary("...")` / `findBundle("...")` 的名字必须已存在于本文件。

## 5. 根工程接入

`settings.gradle.kts`，`includeBuild` 放在模块 `include` 之前：

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "<工程名>"

includeBuild("build-logic")

include(":app")
include(":lib_common")
include(":module_home")
```

根 `build.gradle.kts` 只声明外部插件的 `apply false`，**不写约定插件**：

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.ksp) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
}
```

## 6. 模块使用

```kotlin
// module_xxx/build.gradle.kts
plugins {
    alias(libs.plugins.ydj.android.module)
}

android {
    namespace = "com.<域>.module.<业务>"
}
```

```kotlin
// lib_xxx/build.gradle.kts
plugins {
    alias(libs.plugins.ydj.android.lib)
}

android {
    namespace = "com.<域>.lib.<能力>"
}
```

模块里不写 `compileSdk`、`minSdk`、`compileOptions`、`buildFeatures`、`buildTypes`、flavors、ARouter 依赖，这些由约定插件注入。

## 7. 新增一个约定插件的清单

1. `convention/src/main/java/com/<域>/convention/XxxConventionPlugin.kt`：实现 `Plugin<Project>`。
2. `convention/build.gradle.kts`：追加 `register("xxx") { id = libs.plugins.<别名>.get().pluginId; implementationClass = "...XxxConventionPlugin" }`。
3. `gradle/libs.versions.toml`：`[plugins]` 追加 `<域简写>-xxx = { id = "com.<域>.convention.xxx" }`。
4. 需要给模块使用时，在模块 `plugins {}` 加 `alias(libs.plugins.<别名>)`。
5. 跑 `./gradlew :convention:validatePlugins` 和目标模块编译。

## 8. 验证顺序

```bash
./gradlew :convention:validatePlugins   # 插件声明合法
./gradlew help                          # build-logic 合成、catalog 可解析
./gradlew :module_xxx:assembleDevDebug  # 单个模块生效
./gradlew :app:assembleDevDebug         # 全链路
```

首次接入或改动 catalog 后若仍报旧错，执行 `./gradlew --stop` 后重试，必要时在 IDE 里 Invalidate Caches。
