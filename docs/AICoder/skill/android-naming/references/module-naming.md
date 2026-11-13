# 模块命名规范 (Module Naming Specification)

> 本文档定义工程名、Gradle 模块的目录名、`include` 写法和模块包名（namespace）。模块内部的包分层见 `package-naming.md`，类与资源命名见各自文件。

## 工程命名

| 对象 | 规则 | 示例 |
|------|------|------|
| 工程名 `rootProject.name` | PascalCase，描述产品 | `YdjDriver`、`AppBase` |
| app 目录 | 固定 `app` | `app` |
| `applicationId` | 反向域名，全小写，无 `lib` / `module` 段 | `com.ydj.driver`、`com.victor.app.base` |
| app `namespace` | 与 `applicationId` 同值 | `com.ydj.driver` |
| app 源码包 | 与 `namespace` 一致 | `com/ydj/driver/` |

规则：

- `applicationId` 全工程唯一，只在 `app` 声明，发布后不可修改。
- 工程名用 PascalCase，不用连字符、下划线或全小写。
- 同一产品线多个端用后缀区分（如 `YdjDriver`、`YdjMerchant`），不要用 `App1`、`NewApp` 这类无信息名。

## 模块类型与目录名

| 类型 | 目录名格式 | 说明 |
|------|-----------|------|
| 应用入口 | `app` | 唯一打包出口，不放业务代码 |
| 基础库 | `lib_<能力>` | 与业务无关，可被任意模块依赖 |
| 业务模块 | `module_<业务>` | 页面、交互与业务逻辑 |
| 构建插件 | `build-logic` | 约定插件，不参与打包 |

规则：

- 全小写 + 下划线分隔，不用驼峰、连字符和大写。
- 前缀决定分层：基础能力进 `lib_`，业务进 `module_`，两者不混用（`lib_order`、`module_util` 都不合法）。
- 一个模块一个职责，名字描述能力或业务；不用 `common2`、`new_*`、`temp`、`utils` 这类无信息名。
- 目录名去掉前缀后就是包名末段，不再另起一个名字。
- 新增公共能力时下沉到 `lib_`，不复制到多个 `module_` 里。

## 模块包名（namespace）

规则：

1. 反向域名开头，全小写、点分隔，与模块目录名一一对应。
2. 基础库：`<域>.lib.<能力>`，如 `com.ydj.lib.http`、`com.victor.lib.widget`。
3. 业务模块：`<域>.module.<业务>`，如 `com.ydj.module.home`、`com.ydj.module.order`。
4. 应用入口：直接用应用包名，不出现 `lib` / `module` 段，如 `com.ydj.driver`、`com.victor.app.base`。
5. 目录名中的下划线在包名里转成 `.` 或去掉，不保留：`lib_coremodel` → `com.ydj.lib.coremodel`；`lib_qmf_ppplugin` → `com.ydj.lib.qmf.ppplugin.android`。
6. `namespace` 必须与 `src/main/java/` 下的包目录一致；`applicationId` 只在 app 模块声明。

| 工程 | 模块 | namespace |
|------|------|-----------|
| AppBase | `app` | `com.victor.app.base` |
| AppBase | `lib_base` | `com.victor.lib.base` |
| AppBase | `lib_util` | `com.victor.lib.util` |
| AppBase | `lib_widget` | `com.victor.lib.widget` |
| AppBase | `lib_aar` | `com.victor.lib.aar` |
| 参考工程 | `app` | `com.ydj.driver` |
| 参考工程 | `lib_common` | `com.ydj.lib.common` |
| 参考工程 | `lib_coremodel` | `com.ydj.lib.coremodel` |
| 参考工程 | `lib_http` | `com.ydj.lib.http` |
| 参考工程 | `lib_pay` | `com.ydj.lib.pay` |
| 参考工程 | `lib_luban` | `com.ydj.lib.luban` |
| 参考工程 | `lib_qmf_ppplugin` | `com.ydj.lib.qmf.ppplugin.android` |
| 参考工程 | `module_home` | `com.ydj.module.home` |
| 参考工程 | `module_login` | `com.ydj.module.login` |
| 参考工程 | `module_delivery` | `com.ydj.module.delivery` |
| 参考工程 | `module_me` | `com.ydj.module.me` |
| 参考工程 | `module_message` | `com.ydj.module.message` |
| 参考工程 | `module_wallet` | `com.ydj.module.wallet` |
| 参考工程 | `module_map` | `com.ydj.module.map` |
| 参考工程 | `build-logic` | `com.ydj.convention.buildlogic` |

## settings.gradle 写法

```kotlin
rootProject.name = "AppBase"

include(":app")
include(":lib_base")
include(":lib_util")
include(":lib_widget")
include(":module_order")
```

- `include` 统一写 `:模块目录名`，不写文件路径，不加多余斜杠。
- 模块间依赖写 `implementation(project(":lib_base"))`，不用本地 AAR 路径替代模块。
- 改模块目录时，`include`、`namespace`、源码包目录三处同步修改，否则编译失败。

## 新增模块检查清单

- [ ] 目录名符合 `app` / `lib_<能力>` / `module_<业务>`，全小写下划线
- [ ] `settings.gradle(.kts)` 已 `include(":模块名")`
- [ ] `namespace` 符合 `<域>.lib|module.<名称>`，源码目录与 namespace 对应
- [ ] app 模块已依赖新业务模块，基础库按需依赖
- [ ] 与现有模块职责不重叠（`lib_http` 已存在就不再建 `lib_network`）
- [ ] 模块专属资源放在本模块，颜色与通用 Drawable 仍走 `lib_common`

## 与其它规范

| 关注点 | 对应文件 |
|--------|----------|
| 模块内部的包分层（view/data/util） | `package-naming.md` |
| 网络链路文件落在哪个模块 | `network-naming.md`，落点以工程现有 `lib_coremodel` 为准 |
| 模块依赖方向与 DI | `figma-android-ui/references/architecture-spec.md` |
| Drawable / 颜色 / 图片的存放位置 | `drawable-naming.md`、`color-naming.md`、`image-naming.md` |
