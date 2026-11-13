---
name: android-build-logic
description: 创建或扩展 Android 工程的 build-logic 构建逻辑：build-logic/settings.gradle.kts、convention 子项目、约定插件类、gradlePlugin 注册、版本目录插件别名、includeBuild 接入与 validatePlugins 校验。用于搭建 build-logic、新增约定插件、统一 compileSdk/flavors/签名/资源前缀、排查插件无法解析。命名按 android-naming。
---

# 创建 build-logic 构建逻辑

把重复的 Gradle 配置收进约定插件，模块脚本只留 `alias(...)` + `namespace` + 私有依赖。

**参考工程**：`D:\GitProjects\ydj-driver-android\build-logic`（8 个约定插件 + support 配置层）。

## 前置判断

1. 工程根目录必须有 `settings.gradle(.kts)`；不存在就先由 `android-project` 建工程骨架，不单独造 build-logic。
2. 工程根目录必须已有 `gradle/libs.versions.toml`。没有版本目录时，先在根工程补目录，再建 build-logic。
3. 已经有 `build-logic/`：本 Skill 只用于**新增或修改插件**，不重建目录、不改已有插件 id。
4. 读 1~2 个现有插件和 `convention/build.gradle.kts`，**现有写法优先**于本规范。
5. 只有单模块、没有 flavor/统一编译配置诉求的小工程，不引入 build-logic。

## 目录结构

```
build-logic/
├── settings.gradle.kts                 # 独立构建：版本目录 + include(":convention")
└── convention/
    ├── .gitignore                      # /build
    ├── build.gradle.kts                # kotlin-dsl + gradlePlugin 注册
    └── src/main/java/com/<域>/
        ├── convention/
        │   ├── AndroidBaseConventionPlugin.kt
        │   ├── AndroidApplicationConventionPlugin.kt
        │   ├── AndroidLibConventionPlugin.kt
        │   ├── AndroidModuleConventionPlugin.kt
        │   ├── AndroidRoomConventionPlugin.kt
        │   ├── AndroidLintConventionPlugin.kt
        │   ├── OpenSourceConventionPlugin.kt
        │   └── JvmLibraryConventionPlugin.kt
        └── support/
            ├── AppBuildConfig.kt       # SDK、版本号、isModule、BUILD_NAME
            ├── AppSignConfig.kt        # 签名配置（凭证不写死）
            ├── KotlinAndroid.kt        # 通用编译配置与 flavors
            ├── ProjectExtensions.kt    # libs 访问器
            ├── UploadApkConfig.kt      # 分发开关与 APK 命名
            ├── UploadType.kt           # 环境枚举
            └── TestApkDistribution.kt  # 构建完成 / 手动分发
```

要求：`support/` 放可复用的配置函数与常量，`convention/` 只放插件类且只做组合与项目级配置。

## 创建顺序

1. **build-logic/settings.gradle.kts**：`pluginManagement` + `dependencyResolutionManagement`，并把根工程版本目录挂进来：

```kotlin
dependencyResolutionManagement {
    repositories {
        google { content { includeGroupByRegex("com\\.android.*"); includeGroupByRegex("androidx.*") } }
        mavenCentral()
    }
    versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
}
rootProject.name = "build-logic"
include(":convention")
```

2. **convention/build.gradle.kts**：`kotlin-dsl`、Java 17、`compileOnly` 四个 Gradle 插件依赖、`validatePlugins`、`gradlePlugin.plugins.register`。模板见 `references/build-logic-structure.md`。
3. **版本目录加别名**：每个约定插件在 `[plugins]` 里一条**不带 version** 的别名，id 以 `com.<域>.convention.` 开头。
4. **根 settings 接入**：`includeBuild("build-logic")` 必须写在 `include(":模块")` 之前。
5. **模块脚本**：只写 `alias(libs.plugins.<别名>)` + `android { namespace = ... }` + 私有依赖；不再重复 `compileSdk`、`compileOptions`、`buildTypes`、`buildFeatures`。
6. **验证**：`./gradlew :convention:validatePlugins`，再编译一个使用该插件的模块。

## 关键点

1. **一个 id 只有一份定义**：`gradlePlugin` 注册时用 `libs.plugins.<别名>.get().pluginId`，不手写 id 字符串。新增插件要同时改三处：插件类、`register(...)`、catalog 别名。
2. **compileOnly 而不是 implementation**：AGP、Kotlin、Room、KSP 的 Gradle 插件只用于编译 build-logic，打进 `implementation` 会污染构建类路径。
3. **插件只做组合**：`application` = `com.android.application` + `base` + `room`；`module` 在此基础上加 `isModule` 切换、`resourcePrefix`、双清单、`api(:lib_common)`。通用配置写进 `support/KotlinAndroid.kt` 的扩展函数，供多个插件调用。
4. **base 不直接给模块用**：`base` 只被其它约定插件 apply；`application` / `module` 已包含 `base` 和 `room`，模块不重复声明。
5. **资源前缀由目录名推导**：`project.name.replace("module_", "") + "_"`，模块目录改名时前缀随之变化，不要另设常量。
6. **双清单由开关决定**：`isModule = true` 用 `src/main/module/AndroidManifest.xml`，否则用 `src/main/AndroidManifest.xml`；切换只改 `AppBuildConfig.isModule`。
7. **凭证不进 build-logic**：签名密码、分发平台 token、Bugly/推送 AppId 不写死在 `AppSignConfig` / `UploadApkConfig`。从 `local.properties`、环境变量或 CI Secret 读取；缺失时跳过签名或分发，不提交真实值。参考工程中的明文凭证只作反面示例，不复制。
8. **版本号单一来源**：`VERSION_NAME`、`VERSION_CODE`、`BUILD_NAME` 只放 `AppBuildConfig`，app 脚本 import 使用，不在各处重复。
9. **新增插件的最小步骤**：新建 `XxxConventionPlugin.kt` → 在 `gradlePlugin` 中 `register` → 在 catalog 加别名 → 需要时在模块 apply。缺任何一步都会报插件找不到。
10. **命名**：目录、插件 id、别名、类名按 `android-naming/references/build-logic-naming.md`；资源与包命名仍回读 `android-naming`。

## 验证

| 命令 | 验证内容 |
|---|---|
| `./gradlew :convention:validatePlugins` | 插件声明是否合法（`failOnWarning = true`） |
| `./gradlew help` | build-logic 能否被合成，catalog 别名是否可解析 |
| `./gradlew :<模块>:assembleDevDebug` | 约定插件是否在模块上正确生效 |
| `./gradlew :app:assembleDevDebug` | 全链路编译、flavor 与签名是否正常 |

## 自检清单

- [ ] `build-logic/settings.gradle.kts` 挂载了根工程 `gradle/libs.versions.toml`
- [ ] 根 `settings.gradle.kts` 有 `includeBuild("build-logic")`
- [ ] `convention/build.gradle.kts` 用 Java 17，Gradle 插件依赖全部 `compileOnly`
- [ ] 每个插件都通过 `libs.plugins.<别名>.get().pluginId` 注册，没有手写 id
- [ ] catalog 里的约定插件别名没有 `version`
- [ ] 模块脚本只有 `alias` + `namespace` + 私有依赖，没有重复的编译配置
- [ ] `base` 未被模块直接 apply；`application` / `module` 未重复 apply `base`、`room`
- [ ] 签名密码、分发 token 从本地或 CI 注入，仓库内无真实凭证
- [ ] `validatePlugins` 与目标模块编译均通过

## 不要做的事

- 不新建第二个 build-logic 子项目，也不把插件实现放进 `buildSrc`
- 不在根脚本 `plugins {}` 里 `alias` 约定插件（只放外部插件 `apply false`）
- 不改已有插件的 id；不顺手升级 AGP、Kotlin、KSP 版本
- 不把业务依赖、页面级库塞进 `base`
- 不复制参考工程的 keystore、签名密码、分发平台 token、第三方 AppKey

## 参考文件

| 文件 | 内容 | 何时读 |
|---|---|---|
| `references/build-logic-structure.md` | settings、convention 构建脚本、注册模板、根工程接入、catalog 别名、验证顺序 | 首次搭建或新增插件时 |
| `references/convention-plugins.md` | 8 个插件的职责、apply 关系、代码骨架、何时新增插件 | 写插件类时 |
| `references/support-utils.md` | `AppBuildConfig`、`AppSignConfig`、`KotlinAndroid`、分发链路与凭证红线 | 改编译配置、flavors、签名、分发时 |
| `references/troubleshooting.md` | 插件找不到、catalog 无法解析、validatePlugins 失败等排查 | 构建报错时 |
| `android-naming/references/build-logic-naming.md` | 目录、插件 id、别名、类名规则 | 起名阶段 |

## 与其他 Skill 的关系

| Skill | 关系 |
|---|---|
| `android-project` | 建工程骨架时生成 build-logic 初版；本 Skill 负责插件细节与扩展 |
| `android-module` | 模块脚本只 apply 约定插件，不重复写编译配置 |
| `android-naming` | 插件 id、别名、类名按 `build-logic-naming.md`；其余命名按其规范 |
| `android-review` | 审查构建改动时按本 Skill 的自检清单核对 |
| `figma-android-ui` / `android-api-ds` | 生成代码依赖的 `compileSdk`、flavors、ARouter、Room 配置来自约定插件 |

## 更新日志

### v1.0.0
- 新增：build-logic 目录、settings、convention 构建脚本与 8 个约定插件职责
- 补充 support 配置层、凭证红线、验证命令与排查表
