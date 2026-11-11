---
description: Android 功能开发流水线：Figma 出 UI → 接口链路 → 数据上屏 → 双规范审查
---

# Android 功能开发流水线

按固定顺序交付一个完整功能：**UI 先行 → 接口链路 → 数据上屏 → 代码审查**。四个阶段是依赖顺序，不可跳、不可乱序；每阶段自检通过才能进入下一阶段。

## 输入

按以下优先级解析，不要求固定格式：

1. **命令后的参数**：顺序任意，按内容识别
   - 设计稿链接：`figma.com` / `mastergo.com` / `js.design` / `lanhuapp.com` 等
   - 接口文档链接：含 `swagger` / `doc.html` / `knife4j` 的地址
   - 其余第一段非链接文本：功能/页面名称
   - `-y`：一次性跑完，不设中途确认点
2. **当前对话中已出现的链接**：按同一规则识别设计稿与接口文档
3. 仍缺失的，在一条消息里一次问全，不要多轮挤牙膏

工程根目录即当前工作目录，不需要额外传路径；开始前确认存在 `settings.gradle` 或 `settings.gradle.kts`，不存在就停下来让我切换工作区，不要新建工程或目录。模块、包名、网络模块位置从代码推断，推断不出再问。

```
/android-feature 登录页 https://www.figma.com/design/xxx https://api.xx.com/doc.html#/login -y
/android-feature https://www.figma.com/design/xxx 登录页
/android-feature          # 什么都不带，问齐后直接跑
```

## 前置准备（只做一次）

- 读取两个 Skill 及 references：
  - figma-android-ui：`C:\Users\81544\.qoder-cn\skills\figma-android-ui\SKILL.md` 及 `references\` 下相关规范
  - android-api-ds：`C:\Users\81544\.qoder-cn\skills\android-api-ds\SKILL.md`、`references\naming-spec.md`、`references\code-templates.md`
- 扫描项目现有实现，形成「本项目约定」清单：布局与类命名、colors.xml / dimens、BaseRes / NetworkResponse、AbsDS、InjectorUtils、VMFactory、Adapter 写法。后续生成一律以此为准；与 Skill 默认值冲突时以项目现有约定为准并说明。
- 明确本次范围：新增文件、需要改动的既有文件、可复用的组件。只做本功能，不顺带重构。
- 若带 `-y`，仍要在每阶段输出简短的门禁摘要后继续。

## 阶段一：UI（figma-android-ui）

- 通过 Figma MCP 读取节点结构与样式；图标/图片导出为 3x PNG 到本模块 `src/main/res/mipmap-xxhdpi/`。链接不可用时要求提供切图与标注，不凭截图臆测尺寸。
- 产出：布局 XML（`activity_` / `fragment_` / `dlg_` / `rv_`）、Kotlin Activity/Fragment 骨架（`initView` / `subscribeUi` / `sendRequest`）、colors / dimens / strings / drawable / style 资源。
- 此阶段不写网络代码：列表用静态占位或空 Adapter，待阶段二、三再替换。
- 自检：
  - ConstraintLayout 嵌套必须为空（含 `<include>`、`<merge>`、自定义组合控件）
  - 控件 ID 为 `m` + 类型缩写 + 驼峰；尺寸 `@dimen/dp_*`；颜色 `@color/color_XXXXXX`
  - 文本抽到 string 资源；图片命名与目录正确；新增颜色已写入 colors.xml
  - 字号也用 dp；属性顺序与命名空间顺序符合规范
- 交付：文件清单 + 自检结果 + 与设计稿的已知偏差。未带 `-y` 时在此停下等我确认。

## 阶段二：接口链路（android-api-ds）

- 按文档生成：`{动作}{模块}Req`、`{模块}Data`、`{模块}Api`、`{模块}ApiService`、`I{模块}DS`、`{模块}DS`、`{模块}VM`、`{模块}VMFactory`，并在 InjectorUtils 补 `provide{模块}VMFactory()`。
- 已存在的文件用最小 diff 追加，不重写整个文件；新增文件放对目录、package 与 import 完整。
- 规范：字段可空且默认 `null`、类型按映射表；无返回值用 `Nothing`，禁止 `Any?`；GET 用 `@Query`、提交用 `@Body`；`suspend` + 项目统一响应封装；VM 用 `viewModelScope.launch`、LiveData 类型正确。
- 此阶段不做 UI 绑定。交付：文件清单 + 文档字段到 Kotlin 属性的映射表 + 对文档未明确处的假设（逐条列出）。未带 `-y` 时在此停下等我确认。

## 阶段三：数据上屏

- 打通链路：DS → VM（`viewModelScope`）→ UI 观察 LiveData（`subscribeUi`）。
- 必须实现四态：加载中 / 有数据 / 空 / 失败；列表接 Adapter 与阶段一的 `rv_` 布局；点击等交互回调落到 VM 方法。
- UI 层只做展示与转发，不写业务判断；空态与错误文案用字符串资源；请求时机（进入页面 / 刷新 / 分页）与设计稿交互一致。
- 生命周期：binding 释放、监听与请求取消、Fragment 视图空安全。
- 交付：数据流说明（触发 → 接口 → VM → UI）+ 改动文件清单。

## 阶段四：审查

- 按 `/android-review` 的标准审查本次全部改动：网络链路按 android-api-ds，UI 与资源按 figma-android-ui，另加正确性、性能、兼容性。
- 输出：概览（变更范围、阻塞问题数、总体判断）→ 问题表（级别 / 位置 path:line / 问题 / 影响 / 建议）→ 阻塞与重要问题的最小修复方案。
- 只审查不修改。我确认后，才按 P0 / P1 / P2 修复，且只改本次范围内的代码。

## 硬性约束

- UI 未定稿不写接口绑定；接口未就绪不得用硬编码假数据冒充真实上屏（临时占位必须集中标注、可一键清除）。
- 设计稿与接口冲突（字段缺失、交互无法实现）时立即提出并给可选方案，不自行折中。
- 每阶段结束必须给出「已完成 / 待确认 / 下一步」，不静默推进、不跨阶段返工。
- 规范类结论必须能指到 Skill 的具体文件与规则，不臆造。
- 不修改无关代码，不新增未使用的依赖、工具类或抽象层。
