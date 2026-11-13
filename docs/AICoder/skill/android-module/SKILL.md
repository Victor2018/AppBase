---
name: android-module
description: 创建 Android Gradle 模块：判定 module_<业务> 业务模块或 lib_<能力> 基础库，按 android-naming 生成目录、namespace 与包结构，套用工程现有约定插件或手写 AGP 配置，生成双 AndroidManifest、ARouter 对外入口、监听接口与资源文件，完成 settings.gradle 注册和 app 依赖。用于新建模块、模块拆分、组件化独立运行。触发：新建模块、创建模块、新增 module、module_xxx、lib_xxx、模块化、组件化。
---

# 创建 Android 模块

交付一个**可编译、可独立运行、命名合规**的 Gradle 模块，不止建目录：注册、编译配置、清单、入口、资源、依赖一次配齐。

## 前置判断

1. 确认工程根目录存在 `settings.gradle(.kts)`；不存在就停下，新工程先用 `android-project` 建骨架，不新建工程。
2. 读 1~2 个同类现有模块（参考工程为 `lib_pay`、`module_message`），**现有写法优先**于本规范。
3. 判定模块类型：

| 需求 | 类型 | 目录名 | namespace |
|---|---|---|---|
| 页面、交互、业务流程 | 业务模块 | `module_<业务>` | `<域>.module.<业务>` |
| 无页面、可被任意模块依赖的能力 | 基础库 | `lib_<能力>` | `<域>.lib.<能力>` |
| 打包入口 | 仅 `app` | `app` | `<域>` |

规则：

- 页面进 `module_`，通用能力下沉 `lib_`，两者不混用（`lib_order`、`module_util` 都不合法）。
- 全小写 + 下划线；一个模块一个职责；不用 `new_`、`temp`、`utils` 这类无信息名。
- 目录名去掉前缀后的名称就是包名末段，不另起名字。
- 命名细则以 `android-naming/references/module-naming.md`、`package-naming.md` 为准。

## 判定编译方式

先看工程是否有约定插件：存在 `build-logic/` 且 `settings.gradle.kts` 里有 `includeBuild("build-logic")`。

| 工程形态 | 写法 |
|---|---|
| 有约定插件（参考工程） | `build.gradle.kts` 只写 `plugins { alias(...) }` + `namespace` + 模块私有依赖 |
| 无约定插件（如 AppBase） | 照抄工程现有 lib 模块的显式 AGP 配置 |

有约定插件时**禁止重复声明**插件已注入的 `compileSdk`、`buildTypes`、`buildFeatures`、`compileOptions`、KSP、ARouter 依赖。细节见 `references/build-conventions.md`。

## 业务模块（结构参考 module_message，页面参考 module_wallet）

```
module_<业务>/
├── .gitignore
├── build.gradle.kts
├── consumer-rules.pro
├── proguard-rules.pro
└── src/
    ├── main/
    │   ├── AndroidManifest.xml            # 集成模式
    │   ├── module/AndroidManifest.xml     # 组件模式（isModule=true 时生效）
    │   ├── java/com/<域>/module/<业务>/
    │   │   ├── view/activity/XxxActivity.kt
    │   │   ├── view/fragment/XxxFragment.kt
    │   │   ├── view/adapter|dialog|holder/   # 按需
    │   │   ├── data/                          # 按需：模块内常量、UI 模型
    │   │   └── interfaces/                    # 按需：模块内回调
    │   └── res/
    │       ├── layout/activity_<业务>.xml     # module_wallet 模板
    │       ├── layout/fragment_<业务>.xml     # module_wallet 模板
    │       ├── values/strings.xml
    │       └── mipmap-xxhdpi/                 # 切图
    ├── test/…/ExampleUnitTest.kt
    └── androidTest/…/ExampleInstrumentedTest.kt
```

按 `references/business-module.md` 生成，关键点：

1. **build.gradle.kts**：`alias(libs.plugins.ydj.android.module)` + `namespace`，没有私有依赖就不写 `dependencies`。
2. **双清单**：`src/main/AndroidManifest.xml` 只声明本模块组件；`src/main/module/AndroidManifest.xml` 配 `App`、launcher、`tools:replace`，保证组件模式能单独启动。组件清单的 `android:icon` 引用 `@drawable/module_<业务>_logo`，该 logo **必须建在 `lib_common/src/main/res/drawable/`**，缺失会导致构建失败。
   - 生成方式：复制 `lib_common/src/main/res/drawable/module_me_logo.xml` 为 `module_<业务>_logo.xml`，**只把 `android:fillColor` 换成新颜色**，尺寸、viewport、`group` 变换与 `pathData` 全部保留，不重画图标。新颜色不要与现有 logo 重复（已占用 `#2196F3`、`#795548`、`#9c27b0`、`#AEB443`、`#C99C28`、`#E91E63`、`#FF9800`）。
3. **资源**：`values/strings.xml` 至少放 `<string name="<业务>_module">module_<业务></string>`；布局与模块资源放本模块 `res/`，命名按 `android-naming`，模块专属资源带业务前缀；模块 logo 是唯一例外，统一放 `lib_common`。
4. **页面模板（参考 module_wallet）**：
   - Activity 布局用 `CoordinatorLayout + AppBarLayout/CollapsingToolbarLayout + Toolbar + FrameLayout(fl_container)`；Activity 用 `switchFragment` 在 `onResume` 加载 Fragment，动画用 `lib_common` 的 `anim_fragment_enter/exit`。
   - Fragment 布局用 `CoordinatorLayout + AppBarLayout/CollapsingToolbarLayout + Toolbar(含 MovingTextView mTvTitle)`，内容区为 `TextView(mTvContent)` 占位；需要 H5 时才换成 `ProgressWebView(mPWeb)`。
   - Fragment 类默认继承 `BaseFragment`，实现 `handleBackEvent` / `freshFragData` / `onClick`，`initView` 调 `subscribeUi()`；H5 页面才改回 `BaseWebFragment` 并补 `OnWebViewStatusListener` / `OnWebMediaProcess`。`newInstance` 必备。
5. **对外入口（模块接口）**：
   - Activity 加 `@Route(path = ARouterPath.XxxAct)`，Fragment 加 `@Route(path = ARouterPath.XxxFgt)`。
   - 常量追加到 `lib_common` 的 `ARouterPath.kt`，格式 `"/<业务>/module/<类名>"`。
   - Activity 的 `companion object` 提供 `intentStart(activity)`；跨模块调用只走 `ARouterPath`/`NavigationUtils`，不直接 import 对方类。
   - 模块内回调放 `interfaces/On<动作>Listener.kt`。
6. **网络链路不落业务模块**：`Req/Data/Api/ApiService/IDS/DS/VM/VMFactory` 放 `lib_coremodel`，业务模块通过 `lib_common` 的 api 传递依赖使用，命名按 `android-naming/references/network-naming.md`。

## 基础库模块（参考 lib_pay）

```
lib_<能力>/
├── .gitignore
├── build.gradle.kts
├── consumer-rules.pro
├── proguard-rules.pro
└── src/main/
    ├── AndroidManifest.xml                # 空清单
    └── java/com/<域>/lib/<能力>/
        ├── <能力>Helper.kt                # 对外 API
        ├── data/                          # 模型，可按实现分子包
        └── interfaces/                    # OnXxxListener
```

按 `references/library-module.md` 生成，关键点：

1. **build.gradle.kts**：`alias(libs.plugins.ydj.android.lib)` + `namespace` + 依赖（通常 `implementation(project(":lib_common"))`）。
2. **空清单**：不声明组件，不为“看起来完整”加 Activity。
3. **只暴露能力**：顶层 `XxxHelper`、`data/`、`interfaces/`；不写 `@Route`，不建 `view/` 页面包。
4. **依赖方向**：`lib_` 只能依赖 `lib_common` 或其它 `lib_`；禁止依赖 `module_`。

## 注册与依赖

1. `settings.gradle.kts` 追加 `include(":模块名")`，写在现有模块之后。
2. `app/build.gradle.kts` 追加 `implementation(project(":module_xxx"))`；`lib_` 按需，不默认加。
3. 组件模式：`build-logic` 中 `AppBuildConfig.isModule = true` 时，app 只依赖 `lib_common`/`lib_coremodel`，业务模块各自以 application 运行；验证完按工程约定切回（默认 `false`）。

## 自检清单

- [ ] 目录名、`include`、`namespace`、源码包目录四者一致
- [ ] `build.gradle.kts` 没有重复声明约定插件已注入的配置
- [ ] 双清单齐全，组件模式清单有 launcher 且能独立启动
- [ ] `lib_common/src/main/res/drawable/` 已存在 `module_<业务>_logo`，组件清单的 `android:icon` 可解析；内容为 `module_me_logo.xml` 模板 + 仅改 `fillColor`
- [ ] Activity/Fragment 按 module_wallet 模板：Toolbar + `fl_container` + `switchFragment`；Fragment 布局含 `mTvTitle` + `mTvContent`
- [ ] Fragment 继承 `BaseFragment`，实现 `handleBackEvent` / `freshFragData` / `onClick`，`initView` 调 `subscribeUi()`
- [ ] Fragment 具备 `newInstance` / `@Route`
- [ ] 业务模块路由常量已入 `ARouterPath`，类上有 `@Route`，跨模块无直接类依赖
- [ ] 页面在业务模块，网络链路在 `lib_coremodel`
- [ ] 基础库无页面、无路由、不反向依赖业务模块
- [ ] 新增字符串/颜色/Drawable 未与现有资源重名，命名符合 `android-naming`
- [ ] `./gradlew :<模块名>:assembleDebug` 通过；集成模式 `./gradlew :app:assembleDebug` 通过

## 不要做的事

- 不新建工程、不改插件与依赖版本、不顺手升级 AGP/Kotlin
- 不复制现有模块后留下旧包名、旧资源、旧路由
- 不为一次性需求新增 `lib_`；不把同一能力复制进多个 `module_`
- 不修改无关模块，不在本次范围内重构

## 参考文件

| 文件 | 内容 | 何时读 |
|---|---|---|
| `references/business-module.md` | `module_message` 全量文件模板与逐项说明 | 创建业务模块时 |
| `references/library-module.md` | `lib_pay` 全量文件模板与逐项说明 | 创建基础库时 |
| `references/build-conventions.md` | 约定插件职责、双清单切换、资源前缀、无插件工程配置 | 写 `build.gradle.kts` 或排查编译问题时 |
| `android-naming/references/module-naming.md` | 模块目录、namespace、`include` 写法 | 起名阶段 |
| `android-naming/references/package-naming.md` | 模块内包分层 | 建包阶段 |
| `android-naming/references/class-naming.md` | Helper、Listener、路由常量命名 | 写类与接口时 |
| `android-naming/references/network-naming.md` | Req/Data/Api/DS/VM 命名与落点 | 模块需要接接口时 |

## 与其他 Skill 的关系

| Skill / Command | 关系 |
|---|---|
| `android-naming` | 命名规则的唯一来源，本 Skill 不自建命名规则 |
| `android-project` | 新工程先由它建根工程、`build-logic` 与 app 壳，再用本 Skill 建 `lib_` / `module_`；本 Skill 不新建工程 |
| `android-build-logic` | 模块需要的 `compileSdk`、flavors、双清单切换、资源前缀由约定插件提供，本 Skill 不重复写编译配置 |
| `android-api-ds` | 新模块要接接口时，链路生成到 `lib_coremodel`，命名按其模板 |
| `figma-android-ui` | 先由本 Skill 建模块骨架，再生成页面 UI |
| `android-review` | 审查新模块时按本 Skill 的自检清单核对 |
| `/android-feature` | 功能需要新模块时，先执行本 Skill 再进入 UI/接口阶段 |

## 更新日志

### v1.6.0
- 明确与 `android-build-logic` 的分工：编译配置、双清单切换、资源前缀由约定插件提供，模块脚本只留 alias、namespace 与私有依赖

### v1.5.1
- 前置判断与关系表指向 `android-project`：新工程先建骨架，本 Skill 只负责模块

### v1.5.0
- Fragment 布局内容区由 `ProgressWebView` 改为 `TextView(mTvContent)` 占位，标题栏 `mTvTitle` 保留
- Fragment 默认基类改为 `BaseFragment`，自检项同步；需要 H5 时再切回 `BaseWebFragment` + `ProgressWebView`

### v1.4.0
- 页面模板参考对象由 `module_map` 改为 `module_wallet`
- Fragment 布局补充 `mTvTitle` 标题栏 + `mPWeb` WebView 结构
- Fragment 类补充 `OnClickListener`、`subscribeUi()`/`subscribeEvent()`（含 `BackH5PageEvent`）、`OnWebMediaProcess` 转发

### v1.3.0
- 模块 logo 改为固定模板：复制 `lib_common/src/main/res/drawable/module_me_logo.xml`，只替换 `android:fillColor`，尺寸、viewport、group 变换与 `pathData` 原样保留
- 补充颜色避重清单与自检项

### v1.2.0
- 业务模块页面改为 `module_map` 模板：Activity 使用 `CoordinatorLayout + Toolbar + fl_container`，Fragment 使用 `BaseWebFragment + ProgressWebView`
- 补充 Fragment 路由常量 `XxxFgt`、`newInstance` 与页面自检项

### v1.1.0
- 明确业务模块 logo 必须创建在 `lib_common/src/main/res/drawable/module_<业务>_logo`，组件清单引用缺失会构建失败
- 自检清单与业务模块模板补充 logo 校验项

### v1.0.0
- 新增：业务模块（`module_message`）与基础库（`lib_pay`）两套脚手架
- 归纳约定插件编译配置、双清单切换、资源前缀与依赖方向
- 补充注册流程、自检清单与工程无约定插件时的降级写法
