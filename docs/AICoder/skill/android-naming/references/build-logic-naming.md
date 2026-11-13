# 构建逻辑命名（build-logic）

约束 `build-logic` 目录、约定插件 id、版本目录别名与插件类文件的命名。资源、包、类命名见各自文件。

## 目录与子项目

| 对象 | 规则 | 示例 |
|---|---|---|
| 根目录 | 固定 `build-logic`，不叫 `buildSrc`、`gradle-plugins` | `build-logic/` |
| 子项目 | 固定 `convention`，构建逻辑只放这一个子项目 | `build-logic/convention/` |
| 源码根 | `src/main/java/`，Kotlin 文件也放这里，由 `kotlin-dsl` 编译 | `convention/src/main/java/` |

## 包名

| 对象 | 规则 | 示例 |
|---|---|---|
| 插件与 support 包 | `<域>.convention`、`<域>.support`，与源码目录一致 | `com.ydj.convention`、`com.ydj.support` |
| 子项目 group | `<域>.convention.buildlogic` | `com.ydj.convention.buildlogic` |

- 包名全小写，不用下划线，不出现 `plugin`、`utils` 这类泛化末段。
- 插件类放 `convention` 包，通用配置与常量放 `support` 包，不混放。

## 插件 id

| 对象 | 规则 | 示例 |
|---|---|---|
| id 前缀 | `com.<域>.convention.` | `com.ydj.convention.` |
| 能力段 | 小写点分，与能力对应；多词用点分隔，不用驼峰或下划线 | `base`、`application`、`lib`、`module`、`android.room`、`android.lint`、`open.source`、`jvm.library` |
| 内部插件 | 只被其它约定插件 apply 的用短名，如 `base`；对外给模块用的带平台/主题段，如 `android.module` | `com.ydj.convention.base` |

- id 只在 `gradle/libs.versions.toml` 定义一次，`convention/build.gradle.kts` 引用 `libs.plugins.<alias>.get().pluginId`，不手写第二份字符串。
- 新增 id 后不改名；确需改名时同步 catalog、注册代码和所有模块的 `alias`。

## 版本目录别名

| 对象 | 规则 | 示例 |
|---|---|---|
| 别名 | `<域简写>-<能力>`，kebab-case；能力段与 id 的能力段对应 | `ydj-android-module` ↔ `com.ydj.convention.module` |
| 访问器 | Gradle 自动把 `-` 转成 `.` | `libs.plugins.ydj.android.module` |
| 版本 | 约定插件别名不写 `version` | `ydj-android-module = { id = "com.ydj.convention.module" }` |

## 类与文件

| 对象 | 规则 | 示例 |
|---|---|---|
| 插件类 | `<能力>ConventionPlugin`，PascalCase，能力段转驼峰 | `AndroidModuleConventionPlugin`、`AndroidRoomConventionPlugin` |
| 注册名 | 插件类去掉 `ConventionPlugin` 后的 camelCase，仅作 Gradle 内部标识 | `register("androidModule")` |
| 配置对象 | `AppBuildConfig`、`AppSignConfig`、`UploadApkConfig`：一类配置一个 object | `AppBuildConfig` |
| 枚举 | `<用途>Type` | `UploadType` |
| 扩展函数文件 | `XxxAndroid.kt` 或按能力命名，如 `KotlinAndroid.kt` | `KotlinAndroid.kt` |

## 三处一致

新增或调整插件时，下面三处必须同名同义：

```
gradle/libs.versions.toml        ydj-android-module = { id = "com.ydj.convention.module" }
convention/build.gradle.kts      register("androidModule") { id = libs.plugins.ydj.android.module.get().pluginId; implementationClass = "...AndroidModuleConventionPlugin" }
模块 build.gradle.kts             alias(libs.plugins.ydj.android.module)
```

实现类文件路径 `.../convention/AndroidModuleConventionPlugin.kt` 与 `implementationClass` 的包名、类名一致。
