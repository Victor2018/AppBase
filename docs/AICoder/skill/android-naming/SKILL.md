---
name: android-naming
description: 统一 Android 项目的命名规范：控件 ID、布局文件、颜色、Drawable、图片、字符串、类/变量/方法、包、模块（app/lib_/module_ 与 namespace）、样式主题、动画、网络层 Req/Data/Api/ApiService/IDS/DS/VM，以及 build-logic 约定插件与版本目录别名。生成代码、新建模块、新增插件或资源、审查命名时使用；规则以工程现有代码为准。
---

# Android 命名规范

把项目里已有的命名约定集中成一份可查、可执行的规则，供 `figma-android-ui`、`android-api-ds`、`android-review` 引用，也可以单独触发。

**适用范围**：所有 Android 项目；规则来自现有工程，换项目时按实际代码校准。

## 规则优先级

1. **工程现有代码 > 本文档**。同类文件、同类资源已经存在的写法就是事实标准。
2. 文档与代码不一致时，先判断是有意偏离还是历史遗留：有意偏离就更新本规范，历史遗留就按规范纠正，并在评审中说明。
3. 不新增规范里没有的前缀或后缀；确实需要时先补文档，再写代码。
4. 新增资源前先搜索是否已有同名或同用途资源，避免重复定义。

## 触发关键词

- 命名：`命名规范`、`命名不统一`、`按规范命名`、`重命名`
- 资源：`控件 ID`、`颜色命名`、`字符串命名`、`drawable 命名`、`图片命名`、`布局文件命名`
- 模块：`新增模块`、`模块命名`、`lib_`、`module_`、`namespace`、`包名`
- 构建：`build-logic`、`约定插件`、`plugin id`、`convention`、`版本目录别名`
- 代码：`类名`、`包名`、`常量命名`、`方法命名`、`Req/Data/Api/DS/VM 命名`
- 审查：`检查命名`、`Review 命名`、`命名是否规范`

## 速查表

| 类别 | 规则 | 示例 |
|---|---|---|
| 控件 ID | `m` + 类型缩写 + 驼峰，XML 加 `tools:ignore="ResourceName"` | `mTvTitle`、`mIvBack`、`mBtnSubmit`、`mRvList` |
| 布局文件 | snake_case + 类型前缀 | `activity_login.xml`、`fragment_home.xml`、`dlg_common_confirm.xml`、`rv_message_cell.xml` |
| 颜色 | `color_` + 大写十六进制 | `color_0084FF`、`color_1A1A1A` |
| Drawable | 类型前缀 + 描述 | `shape_white_radius_12.xml`、`border_0084ff_radius_12.xml`、`ripple_0084ff_radius_12.xml` |
| 图片 | 前缀 + 描述 | `ic_back_black.png`、`img_login_bg.png`、`ic_label_vip.png` |
| 字符串 | snake_case，可按模块加前缀 | `app_name`、`login_title` |
| 类 | PascalCase + 类型后缀 | `LoginActivity`、`HomeVM`、`IUserDS`、`LoginReq`、`LoginSuccessEvent` |
| 变量 | 成员 `m` 前缀 + 驼峰；常量大写下划线 | `mTvTitle`、`ADD`、`GET_LIST` |
| 方法 | 动词开头，按用途收敛 | `initView`、`subscribeUi`、`sendRequest` |
| 动画 | `anim_场景_方向.xml` | `anim_activity_right_in.xml`、`anim_bottom_out.xml` |
| 网络层 | `{动作}{模块}Req`、`{模块}Data`、`{模块}Api`、`{模块}ApiService`、`I{模块}DS`、`{模块}DS`、`{模块}VM`、`{模块}VMFactory` | `AddSubAccountReq`、`SubAccountVM`、`ISubAccountDS` |

## 工作流程

### 1. 确认所属类别

按任务判断要落哪一类文件：模块结构、布局或资源、Kotlin 类与成员、网络分层。一个任务可能跨多类，逐类查对应 reference。

### 2. 先看同类现有文件

在工程里找 2~3 个同类文件，确认前缀、后缀、大小写和分隔符。**现有写法优先于本规范**；两者冲突时按“规则优先级”处理。

### 3. 查对应 reference

下方索引表给出每个文件的适用场景，只读与当前任务相关的文件，不必全读。

### 4. 生成后自检

- [ ] 命名与同类现有文件一致，没有新造前缀或后缀
- [ ] 控件 ID 使用正确的类型缩写，布局加了 `tools:ignore="ResourceName"`
- [ ] 颜色、字符串、尺寸引用的资源已存在，没有为一次性值新建资源
- [ ] 布局文件名后缀与类型匹配（activity_ / fragment_ / dlg_ / rv_）
- [ ] 网络链路命名成套：Req / Data / Api / ApiService / IDS / DS / VM / VMFactory 同一模块名
- [ ] 包名与目录结构一致，全小写无下划线
- [ ] 新增或调整模块时，目录名、`include(":模块名")`、namespace 三者一致
- [ ] 与现有代码冲突处已确认并说明原因

## 参考文档

**重要**：命名规则按类别拆分，每个文件只放一类规则。执行任务前只读相关文件。

| 文件 | 内容 | 何时读 |
|---|---|---|
| `references/view-id-naming.md` | 控件 ID：`m` + 类型缩写 + 驼峰，含全部控件缩写表 | 编写 XML 布局时 |
| `references/layout-file-naming.md` | 布局文件前缀：activity_、fragment_、dlg_、rv_、act_*_header | 新建布局文件时 |
| `references/color-naming.md` | 颜色：`color_XXXXXX`、标准色与主题色保留名、新增颜色流程 | 定义或引用颜色时 |
| `references/drawable-naming.md` | Drawable：shape_、border_、ripple_、gradient_、selector、bg_ | 新建 Drawable 时 |
| `references/image-naming.md` | 图片：ic_、img_、ic_label_、captcha_、chk_、icon_ 及存放目录 | 导入图片资源时 |
| `references/string-naming.md` | 字符串：snake_case、导航标题、提示文案、权限提示等 | 新增字符串时 |
| `references/class-naming.md` | 类：类型后缀对照表、ARouter 路径常量、数据类与事件类 | 新建类时 |
| `references/variable-naming.md` | 变量：成员 m 前缀、ViewBinding、局部变量、TAG、常量 | 声明变量与常量时 |
| `references/method-naming.md` | 方法：生命周期、初始化、订阅、网络请求、懒加载、对话框 | 定义方法时 |
| `references/package-naming.md` | 包：顶层包、模块内包结构、各层子包 | 新建包或模块时 |
| `references/module-naming.md` | 工程名 `rootProject.name`、`applicationId`、模块：`app`、`lib_<能力>`、`module_<业务>`、namespace、`include` 写法、新增模块清单 | 新建工程、新建模块或调整模块结构时 |
| `references/build-logic-naming.md` | 构建：`build-logic`/`convention` 目录、`com.<域>.convention.<能力>` 插件 id、`ydj-*` 别名、`XxxConventionPlugin` 类名 | 新建或修改约定插件时 |
| `references/style-theme-naming.md` | 样式主题：应用主题、Material3、对话框、Toolbar、TabLayout | 定义 Style/Theme 时 |
| `references/animation-naming.md` | 动画：`anim_场景_方向`、转场、方向性、PopupWindow、加载动画 | 新建动画资源时 |
| `references/network-naming.md` | 网络层：模块与动作命名、各层文件命名、方法命名、动作映射表 | 生成接口链路时 |

## 与其他 Skill 的关系

| Skill | 关系 |
|---|---|
| `figma-android-ui` | 生成布局、颜色、字符串、图片、动画资源时按本规范命名 |
| `android-api-ds` | 生成 Req/Data/Api/ApiService/IDS/DS/VM/VMFactory 时按 `network-naming.md` 命名 |
| `android-project` | 新建工程时，`rootProject.name`、`applicationId`、app `namespace` 与本规范的工程命名一致 |
| `android-module` | 新建 `lib_` / `module_` 模块时按其脚手架执行，命名以本规范的 `module-naming.md`、`package-naming.md`、`class-naming.md` 为准 |
| `android-build-logic` | 新增或修改约定插件时，插件 id、别名与类名按 `build-logic-naming.md` |
| `android-review` | 审查 UI 与网络链路时按本规范逐项比对 |
| `fix-tapd-android-bug` | 修复时新增控件 ID、字符串、Drawable 或类，按本规范命名 |

## 更新日志

### v1.3.0
- 新增 `build-logic-naming.md`：`build-logic`/`convention` 目录、插件 id、版本目录别名、`XxxConventionPlugin` 类名
- 补充与 `android-build-logic` 的引用关系

### v1.2.0
- `module-naming.md` 新增工程命名：`rootProject.name`、`applicationId`、app `namespace`
- 补充与 `android-project` 的引用关系

### v1.1.0
- 新增 `references/module-naming.md`：模块目录（`app` / `lib_` / `module_`）、namespace、`include` 写法与新增模块检查清单
- `package-naming.md` 的顶层包名表移入 `module-naming.md`，只保留模块内包结构

### v1.0.0
- 从 `figma-android-ui` 与 `android-api-ds` 抽出命名规则，统一维护
- 按类别拆分为 13 份单一职责的 reference，补充规则优先级与自检清单
