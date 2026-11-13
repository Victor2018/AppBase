---
description: 从零创建 Android 多模块工程：android-project 建骨架 → android-build-logic 补构建逻辑 → 按依赖顺序生成 lib_common、lib_coremodel、lib_pay、module_home、module_me、module_login
---

# Android 工程创建流水线

按依赖顺序从零生成一个可编译、可独立运行的多模块工程。分工固定：

| 环节 | Skill |
|---|---|
| 工程骨架、app 壳、版本目录、flavors | `android-project` |
| build-logic、约定插件、统一编译配置 | `android-build-logic` |
| 每个 `lib_` / `module_` 的目录、清单、路由、资源 | `android-module` |
| 所有命名（工程、包、模块、资源、路由、插件 id） | `android-naming` |

本命令只搭骨架：不生成业务页面内容、不生成接口链路、不修 Bug。页面与接口在骨架验收后由 `/android-feature` 接手。

## 输入

按内容识别，顺序任意：

- **绝对路径**：工程根目录。不存在则新建；已存在且非空则停下，不覆盖。
- 非路径且形如 `com.xxx` 的文本：第一个作包名域，第二个作 `applicationId`。
- **其余第一段文本**：工程名，同时作为 app 显示名候选。
- `modules=a,b,c`：覆盖默认模块集合（仅限本命令支持的 `lib_` / `module_`）。
- `-y`：不设中途确认点，但每阶段仍输出门禁摘要。

缺失项一条消息问全：

1. 工程根目录
2. 工程名（`rootProject.name`，PascalCase）
3. 包名域（如 `com.ydj`）
4. `applicationId`（默认 `<域>.<工程简写>`）
5. App 显示名
6. `VERSION_NAME` / `VERSION_CODE`
7. `isModule` 初始值（默认 `false`）
8. `lib_coremodel` 是否依赖 `lib_http`（见阶段三）

默认创建顺序，**不可乱序**：

```
1. lib_common
2. lib_coremodel
3. lib_pay
4. module_home
5. module_me
6. module_login
```

顺序原因：

- `module_*` 的约定插件会注入 `api(project(":lib_common"))`，`lib_common` 必须最先存在。
- 所有 `lib_` 先建完，业务模块再建，避免业务模块依赖缺失。
- app 的 `implementation(project(":module_xxx"))` 每建完一个模块追加一次，任何时刻都能编译。

```
/android-create-project D:\workspace\MyApp MyApp com.my com.my.app -y
/android-create-project D:\workspace\MyApp modules=lib_common,lib_coremodel,module_home
/android-create-project          # 什么都不带，问齐后按默认集合跑
```

## 前置检查（只读）

- 目标目录：为空或不存在才继续。已有 `settings.gradle(.kts)`、已有代码、或不是预期位置时停下确认。
- 读取四个 Skill 及其 references：
  - `android-project`：`project-structure.md`、`build-config.md`、`app-module.md`
  - `android-build-logic`：`build-logic-structure.md`、`convention-plugins.md`、`support-utils.md`
  - `android-module`：`business-module.md`、`library-module.md`、`build-conventions.md`
  - `android-naming`：`module-naming.md`、`package-naming.md`、`build-logic-naming.md`、`class-naming.md`
- 形成「本次工程约定」摘要：包名域、模块集合与依赖方向、`isModule` 初始值、flavor 集合、app 壳包含哪些底部导航。
- 输出执行计划（阶段、产出、验证命令）。未带 `-y` 时在此停下等我确认。

## 阶段一：工程骨架（android-project + android-build-logic）

产出：

- 根 `settings.gradle.kts`：`pluginManagement`、`dependencyResolutionManagement`、`rootProject.name`、`includeBuild("build-logic")`、`include(":app")`。
- `gradle.properties`、根 `build.gradle.kts`（只 `apply false` 外部插件）、`gradle/libs.versions.toml`（versions / libraries / bundles / plugins，含 `com.<域>.convention.*` 别名且不带 version）。
- build-logic 初版：目录、`settings.gradle.kts`、`convention/build.gradle.kts`、插件注册、`support/` 配置层，按 `android-build-logic` 的模板生成。
- app 壳：`app/build.gradle.kts`、`AndroidManifest.xml`、`MainActivity`（ARouter + ViewPager + 底部导航）、`activity_main.xml`、`Application`、基础资源、`proguard-rules.pro`。
- 版本号、`BUILD_NAME`、`isModule` 只落在 `AppBuildConfig`；app 脚本 import 使用，不写第二份。
- app 的 `dependencies {}` 只留 app 自身必需（AndroidX、ARouter 等），**不引用尚未创建的 `lib_*` / `module_*`**：提前引用会导致配置失败。后续每建一个模块追加一条。

约束：

- app 的底部导航、`ARouterPath` 引用、路由常量只指向本次要建的 `module_home`、`module_me`、`module_login`。模块尚未创建时用 ARouter 路径字符串跳转或 app 内占位 Fragment，不 import 模块类。参考工程多出的 `module_delivery`、`module_wallet`、`module_message` 不生成，相关依赖与路由一并删掉。
- 签名、分发、Bugly/推送 AppId 一律从 `local.properties` / 环境变量读取；不复制参考工程的 keystore、签名密码、fir/蒲公英 token。
- 模块脚本只写 `alias(...)` + `namespace`，不重复 `compileSdk`、`buildTypes`、`buildFeatures`。

验证：

```bash
./gradlew -p build-logic :convention:validatePlugins
./gradlew help
```

两条都通过才进入下一阶段。未带 `-y` 时停下，输出文件清单与验证结果。

## 阶段二：lib_common（android-module）

`lib_common` 是所有模块的依赖前提，必须先建，并且要包含业务模块模板所依赖的基础层：

- 目录与 namespace：`lib_common` → `<域>.lib.common`，四者一致（目录、`include`、namespace、源码包）。
- 构建脚本：`alias(libs.plugins.<域简写>.android.lib)` + `namespace`；域简写取包名域末段（`com.my` → `my`），与 build-logic 注册的插件别名一致；不自带 AGP 配置。
- 基础类：`BaseActivity`、`BaseFragment`（含 `handleBackEvent` / `freshFragData`）、需要 H5 时的 `BaseWebFragment`，以及 `ARouterPath`。以参考工程实现为准，只抽结构，不带业务代码。
- 公共资源：`colors.xml`、`dimens`、`strings`、`styles`（含 `ToolbarStyle`）、`anim_activity_*` 转场；资源命名按 `android-naming`。
- 允许的依赖：无（或仅基础 AndroidX）；不依赖任何 `lib_coremodel`、`module_*`。
- 建完后 app 追加一次 `implementation(project(":lib_common"))`，供 `ARouterPath` 与公共资源使用。

验证：

```bash
./gradlew :lib_common:assembleDevDebug
```

未带 `-y` 时停下确认基础类清单与公共资源清单。

## 阶段三：lib_coremodel、lib_pay（android-module）

### lib_coremodel

- namespace `<域>.lib.coremodel`，依赖 `:lib_common`。
- 只放网络链路骨架：`Req` / `Data` / `Api` / `ApiService` / `IDS` / `DS` / `VM` / `VMFactory` / `InjectorUtils` 的目录与基类/空实现，命名按 `android-naming/references/network-naming.md`。
- 参考工程若用 `lib_http` 承载 Retrofit/OkHttp 封装，而本次集合没有 `lib_http`：**停下问我**，二选一——补建 `lib_http`，或把网络封装直接放进 `lib_coremodel`。不擅自新增模块，也不留下指向不存在模块的依赖。

### lib_pay

- namespace `<域>.lib.pay`，依赖 `:lib_common`。
- 基础库形态：只暴露 `XxxHelper`、`data/`、`interfaces/`；不建 `view/`、不加 `@Route`、不依赖任何 `module_*`。
- 不复制参考工程的商户号、AppId、私钥；这些由调用方或 `local.properties` 注入。

验证：

```bash
./gradlew :lib_coremodel:assembleDevDebug
./gradlew :lib_pay:assembleDevDebug
```

## 阶段四：业务模块（android-module）

逐个创建 `module_home`、`module_me`、`module_login`，每个模块：

- namespace `<域>.module.<业务>`；`resourcePrefix` 由目录名推导，不手写。
- 双清单：`src/main/AndroidManifest.xml`（集成）+ `src/main/module/AndroidManifest.xml`（组件模式，含 App、launcher、`tools:replace`）。
- 页面模板参考 `module_wallet`：Activity 为 `CoordinatorLayout + Toolbar + fl_container + switchFragment`；Fragment 为 `Toolbar(MovingTextView mTvTitle)` + `TextView(mTvContent)`，基类 `BaseFragment`，实现 `handleBackEvent` / `freshFragData` / `onClick`。需要 H5 时才切 `BaseWebFragment` + `ProgressWebView`。
- 对外入口：常量追加到 `lib_common` 的 `ARouterPath`，格式 `/<业务>/module/<类名>`；Activity/Fragment 加 `@Route`，`companion object` 提供 `intentStart(activity)`。
- 模块 logo：复制 `lib_common/src/main/res/drawable/module_me_logo.xml`，只改 `fillColor`；每个模块用不同颜色，且不与参考工程已有颜色重复。
- 注册：`settings.gradle.kts` 追加 `include(":module_xxx")`；app 追加 `implementation(project(":module_xxx"))`。
- 模块私有依赖只写在模块脚本；需要共同依赖时用 `android-build-logic` 扩展插件或 bundle，不在三个模块里各抄一份。

每建完一个模块立即验证，通过后再建下一个：

```bash
./gradlew :module_xxx:assembleDevDebug
./gradlew :app:assembleDevDebug
```

任一失败先修当前模块，不带病进入下一个。

## 阶段五：收口验证

```bash
./gradlew -p build-logic :convention:validatePlugins
./gradlew :app:assembleDevDebug
```

交付：

- 工程树（`app`、`lib_common`、`lib_coremodel`、`lib_pay`、三个 `module_*`）。
- 依赖方向图：`module_* → lib_common`，`app → module_*`，`lib_` 不依赖 `module_`。
- 文件清单与每阶段验证结果。
- 未决项：`lib_http` 处置、缺失的公共资源、参考工程中未生成模块留下的引用。
- 下一步建议：用 `/android-feature` 做首个页面 + 接口，或先用 `/android-review` 审查骨架。

不提交 git，不执行 push。

## 硬性约束

- 目标目录已存在且非空时停下，不覆盖、不清理。
- 不建默认集合与 `modules=` 之外的模块，不建第二个 build-logic，不改 AGP / Kotlin / KSP 版本。
- 不复制参考工程的 keystore、签名密码、分发 token、第三方 AppKey、Bugly AppId。
- 模块脚本不重复约定插件已注入的配置；缺能力时先扩展 build-logic，再改模块。
- 每阶段必须编译通过才能继续；失败先定位修复，不跳过验证。
- 命名冲突以 `android-naming` 为准；参考工程与规范不一致时，说明差异并让我选择。
- 只做骨架，不生成业务页面、接口实现、假数据；不顺手重构参考实现。
