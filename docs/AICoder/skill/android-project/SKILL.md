---
name: android-project
description: 创建 Android 多模块工程骨架：app 壳、build-logic 约定插件、版本目录与根构建脚本；工程与模块命名按 android-naming，lib_/module_ 模块按 android-module 生成。用于新建工程、搭建 app 壳、补齐 build-logic、配置 flavors 与组件化开关。触发：新建工程、创建项目、搭建 app 壳、初始化 Android 项目、build-logic、约定插件、applicationId、flavors。
---

# 创建 Android 工程（app 壳）

交付「能编译、能切组件化模式」的工程骨架：根构建脚本、`build-logic` 约定插件、版本目录、`app` 壳。模块本身交给 `android-module`，命名交给 `android-naming`，本 Skill 只负责工程级装配。

## 前置判断

1. 根目录已有 `settings.gradle(.kts)`：不是新建工程。缺模块用 `android-module`，改配置直接改现有文件，不要重建工程。
2. 参考工程 `D:\GitProjects\ydj-driver-android`：`build-logic` + `gradle/libs.versions.toml` + `app` 壳就是标准形态。**现有写法优先于本规范**。
3. 先定三项不可逆信息：`rootProject.name`、`applicationId`、应用显示名。`applicationId` 发布后不可修改。
4. `app` 壳模板依赖 `lib_common` 的 `App`、`BaseActivity`、`ARouterPath`。新工程按「根 → build-logic → lib_common → app 壳 → 业务模块」推进；顺序颠倒时先建壳，再用 `android-module` 补 `lib_common` 并接线。

## 命名

| 对象 | 规则 | 示例 |
|---|---|---|
| 工程名 `rootProject.name` | PascalCase | `YdjDriver` |
| app 目录 | 固定 `app` | `app` |
| `namespace` / `applicationId` | 反向域名，全小写，无 `lib` / `module` 段 | `com.ydj.driver` |
| app 内包 | `com.<域>.<应用名>`，按功能分包 | `com.ydj.driver` |
| 模块目录 | `lib_<能力>` / `module_<业务>` | 由 `android-module` 生成 |

- 一个工程只允许一个 `applicationId`，只在 `app` 声明；`namespace` 必须与 `src/main/java` 包目录一致。
- 模块、包、类的完整规则见 `android-naming/references/module-naming.md`、`package-naming.md`、`class-naming.md`。

## 工程结构

```
<Project>/
├── settings.gradle.kts              # includeBuild("build-logic") + include 模块
├── build.gradle.kts                 # 仅声明插件 apply false
├── gradle.properties                # AndroidX / KSP / 构建缓存
├── gradle/
│   ├── libs.versions.toml           # 版本目录：唯一版本来源
│   └── wrapper/
├── build-logic/
│   ├── settings.gradle.kts
│   └── convention/
│       ├── build.gradle.kts         # gradlePlugin 注册
│       └── src/main/java/com/<域>/
│           ├── convention/          # 约定插件
│           └── support/             # AppBuildConfig / AppSignConfig / KotlinAndroid
├── app/                             # 壳：打包入口，不放业务代码
├── lib_common/  lib_coremodel/  lib_http/ ...
├── module_home/ module_me/ ...      # android-module 生成
├── repo/                            # 本地 Maven（可选）
├── docs/<工程>.jks                  # 签名文件（不入库）
└── build_beta.bat / build_test.bat / build_online.bat
```

## 创建顺序

1. **根三件套**：`settings.gradle.kts`（`includeBuild("build-logic")` + 仓库 + 模块 `include`）、根 `build.gradle.kts`（插件 `apply false`）、`gradle.properties`；`gradle wrapper` 固定版本。
2. **build-logic**：`settings.gradle.kts` 复用根版本目录，`convention/build.gradle.kts` 注册插件，`com/<域>/convention/` 下实现 base / application / lib / module / room / lint / openSource / jvm。
3. **版本目录**：先补约定插件需要的 `versions`、`plugins`、`libraries`、`bundles`，插件别名必须与 `gradlePlugin` 注册的 id 一致。
4. **app 壳**：`build.gradle.kts`、`AndroidManifest.xml`、`MainActivity`、`activity_main.xml`、基础资源、`proguard-rules.pro`、测试模板。
5. **lib_common**：用 `android-module` 建基础库，提供 `App`、`BaseActivity`、`ARouterPath`。
6. **业务模块**：用 `android-module` 逐个创建，不手写模块骨架。
7. **注册**：`settings.gradle.kts` 追加 `include`；`app/build.gradle.kts` 按 `isModule` 分支补依赖。

## 关键点

1. **编译配置只有一个来源**：SDK、Java 版本、ViewBinding、flavors、签名、混淆、ARouter KSP 全部在 `build-logic`。`app/build.gradle.kts` 只留 `namespace`、`applicationId`、壳依赖与 APK 命名。
2. **组件化开关**：`AppBuildConfig.isModule` 为 `true` 时 app 只依赖 `lib_common`/`lib_coremodel`，业务模块各自作为 application 运行；为 `false` 时 app 依赖全部 `module_*`。交付时保持工程约定值（参考工程为 `false`）。
3. **flavors 固定维度**：`ydj` 维度下 `dev` / `_test` / `beta` / `online` / `huawei` / `xiaomi` / `oppo` / `vivo` / `tencent`，每个 flavor 注入 `MODEL_*` 与 `UMENG_CHANNEL`；不得在 `app` 里重复声明。
4. **app 清单只放壳组件**：启动页、`WebActivity`、扫码页、`FileProvider`、Scheme、推送服务等公共组件来自 `lib_common`；业务页面在各自模块清单。权限按实际功能裁剪，不整份照抄参考工程。
5. **MainActivity 只做壳**：底部导航 + `ViewPager` + ARouter 取 Fragment，不写业务逻辑；Tab 数量与顺序按产品定义。
6. **签名与凭据不进仓库**：`AppSignConfig` 从本地未提交文件或环境变量读取；不复制参考工程的 keystore、密码、地图 key、推送 appId。
7. **APK 命名固定**：`<BUILD_NAME>_<flavor>_<buildType>_v<versionName>_<versionCode>_<buildDate>_<BUILD_NUMBER>.apk`，在 `app` 里统一配置。

## 自检

- [ ] `rootProject.name`、`app` 的 `namespace` / `applicationId` 符合命名表
- [ ] `settings.gradle.kts` 有 `includeBuild("build-logic")`，所有模块已 `include`
- [ ] 根 `build.gradle.kts` 只声明插件，没有 `allprojects` 依赖
- [ ] 版本目录集中管理版本，模块内无硬编码版本号
- [ ] `app/build.gradle.kts` 没有重复声明 SDK、flavors、签名、KSP
- [ ] `isModule` 两种模式的依赖分支齐全
- [ ] `MainActivity` 通过 ARouter 取 Fragment，无跨模块直接 import
- [ ] 清单权限按功能裁剪，无参考工程的第三方 key / appId
- [ ] 签名配置未硬编码密码，keystore 已加入忽略规则
- [ ] `./gradlew :app:assembleDevDebug` 通过；组件模式再验一次 `:module_xxx:assembleDevDebug`

## 不要做的事

- 工程已存在时重建 `settings.gradle.kts` / `build-logic`
- 把业务页面、网络链路写进 `app`
- 在 `app` 或模块里重复声明约定插件已注入的配置
- 复制参考工程的签名文件、密码、SDK key、推送 appId
- 一次创建多个职责重叠的模块；模块一律走 `android-module`
- 顺手升级 AGP / Kotlin / Gradle wrapper

## 参考文件

| 文件 | 内容 | 何时读 |
|---|---|---|
| `references/project-structure.md` | 根脚本、`build-logic`、版本目录、根构建脚本的完整模板 | 搭根工程与约定插件时 |
| `references/build-config.md` | `AppBuildConfig`、`AppSignConfig`、`KotlinAndroid`、flavors、`isModule`、APK 命名、验证命令 | 写编译配置时 |
| `references/app-module.md` | `app` 的 build 脚本、清单、`MainActivity`、布局与资源、测试模板 | 搭 app 壳时 |
| `android-naming/references/module-naming.md` | 工程名、模块目录、namespace | 起名阶段 |
| `android-naming/references/package-naming.md` | 模块内包结构 | 建包阶段 |

## 与其他 Skill 的关系

| Skill | 关系 |
|---|---|
| `android-naming` | 命名规则的唯一来源，本 Skill 不自建命名规则 |
| `android-module` | 本 Skill 建完根工程与 app 壳后，所有 `lib_` / `module_` 交给它生成 |
| `android-build-logic` | 本 Skill 只生成 build-logic 初版；新增、修改约定插件与 support 配置交给它 |
| `/android-feature` | 新工程要落地业务时，先执行本 Skill 与 `android-module`，再进入 UI/接口阶段 |
| `android-review` | 审查工程结构与编译配置时按本 Skill 自检清单核对 |

## 更新日志

### v1.1.0
- 明确 build-logic 的分工：本 Skill 只出初版骨架，插件细节交由 `android-build-logic`

### v1.0.0
- 新增工程脚手架：根脚本、`build-logic` 约定插件、版本目录、`app` 壳
- 归纳 `isModule` 双模式、flavors、APK 命名与凭据处理规则
- 与 `android-naming`、`android-module` 建立分工：命名、模块各归其 Skill
