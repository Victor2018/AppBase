---
name: figma-android-ui
description: 将 Figma 设计稿转换为符合 Android 项目规范的 XML 布局文件。使用场景：当用户提供 Figma、MasterGo、即时设计、蓝湖、Sketch、Adobe XD、Photoshop 等设计工具的链接或设计稿截图，需要生成对应的 Android XML 布局代码、Kotlin Activity/Fragment 代码时触发。
---

# figma-android-ui

将 Figma 设计稿转换为符合 Android 项目规范的 XML 布局文件。

**适用范围**：所有 Android 项目（支持自定义配置以适配不同项目规范）

## 触发关键词

当用户输入包含以下关键词时，本 skill 会自动触发：

### 设计工具名称
- `Figma`、`figma.com`、`Figma 设计稿`
- `MasterGo`、`mastergo.com`
- `即时设计`、`js.design`
- `蓝湖`、`lanhuapp.com`
- `Sketch`、`Adobe XD`、`Photoshop`

### 转换动作
- `设计稿转代码`、`UI 稿转 XML`、`设计图转布局`
- `生成 XML 布局`、`生成 Android 布局`
- `页面转换`、`界面生成`、`Activity 生成`、`Fragment 生成`

### 技术术语
- `控件 ID 命名`、`mTv 命名`、`mIv 命名`
- `尺寸适配`、`dp 转换`、`750px 适配`
- `3x 图片导出`、`PNG 导出`
- `ViewBinding 生成`

### 示例触发语句
```
✓ 请根据 Figma 设计稿生成登录页面
✓ 把这个 MasterGo 设计稿转换为 XML 布局
✓ 帮我导入蓝湖设计图并生成 Android 代码
✓ 将 750px 设计稿的尺寸转换为 dp 并生成布局
✓ 设计稿转代码，生成 Activity 和 XML
```

## 工作流程

### 1. 获取设计信息

通过 Figma MCP 工具获取设计稿数据：

```
1. 使用 mcp__figma__get_figma_data 获取节点结构和样式信息
2. 使用 mcp__figma__download_figma_images 导出图片素材
```

**图片导出路径规范**：
- 图片必须直接导出到项目的 `mipmap-xxhdpi` 目录
- 路径格式：`{项目根目录}/{模块名}/src/main/res/mipmap-xxhdpi/`

### 2. 分析设计结构

识别以下元素类型：

| Figma 元素 | Android 控件 | 备注 |
|-----------|-------------|------|
| Frame (页面级) | ConstraintLayout / CoordinatorLayout | 根布局选择 |
| Frame (列表项) | RecyclerView + item 布局 | 需单独创建 rv_ 布局 |
| Text | TextView | 注意字号、字重、颜色 |
| Rectangle/形状 | View + background | 分割线、背景块 |
| Image | ImageView | 图标、图片 |
| Input | EditText | 输入框 |
| Button | Button / TextView + clickable | 按钮 |
| Tab 导航 | TabLayout | 顶部标签栏 |
| 底部导航 | BottomNavigationView | 底部 Tab |
| Dialog 弹窗 | 独立 dlg_ 布局 | 需创建 Dialog 类 |

### 3. 确定布局类型

根据页面类型选择根布局：

```
带折叠 Toolbar 的页面 → CoordinatorLayout + AppBarLayout
普通页面 → ConstraintLayout（首选，保持单层）
简单线性结构 → LinearLayout
Fragment 容器/叠加层 → FrameLayout
```

> **硬性约束：禁止 ConstraintLayout 嵌套 ConstraintLayout。** 父节点是 ConstraintLayout 时，子节点不能再次使用 ConstraintLayout；`<include>`、`<merge>` 和自定义组合控件同样适用。该规则无例外，优先级高于“最大 2 层”等层级限制。

需要分组、分区或流式排列时，用 `Guideline`、`Barrier`、`Flow`、`Chain`、`Group` 扁平化，不要新建 ConstraintLayout 子布局。复杂子块拆成独立布局文件；如果子布局根节点是 ConstraintLayout，不能再放回 ConstraintLayout 父布局。

### 4. 生成文件

按照规范文件生成对应的 XML 布局、Kotlin 代码和资源文件。

### 5. 布局自检

输出 XML 前必须检查：

1. 搜索所有 `ConstraintLayout` 开始标签，确认没有任何一个的直接父节点也是 `ConstraintLayout`。可用 XPath 快速检查：`//androidx.constraintlayout.widget.ConstraintLayout//androidx.constraintlayout.widget.ConstraintLayout`，结果必须为空。
2. 检查 `<include>`、`<merge>` 引入的布局，确认没有间接嵌套。
3. 检查自定义组合控件，确认没有在 ConstraintLayout 父布局中再引入 ConstraintLayout 实现。
4. 发现嵌套必须扁平化后重新输出，不能以“嵌套不超过 2 层”为由保留。

## 参考文档

**重要**：在执行任何转换任务前，请先阅读以下规范文档：

### 命名规范（统一维护在 `android-naming` skill）

命名规则不再在本 skill 内重复维护，全部放在 `android-naming` skill 的 `references/` 下，按类别拆分：

| 类别 | 对应文件 |
|---|---|
| 控件 ID、布局文件、颜色、Drawable、图片、字符串 | `view-id-naming.md`、`layout-file-naming.md`、`color-naming.md`、`drawable-naming.md`、`image-naming.md`、`string-naming.md` |
| 类、变量、方法、包 | `class-naming.md`、`variable-naming.md`、`method-naming.md`、`package-naming.md` |
| 模块（`app` / `lib_` / `module_`）与 namespace | `module-naming.md` |
| 样式主题、动画 | `style-theme-naming.md`、`animation-naming.md` |

生成文件前先按 `android-naming` 的规则命名；未安装该 skill 时，以工程内同类文件的现有写法为准。

### 代码与布局规范

| 文件 | 内容 |
|------|------|
| `references/xml-layout-spec.md` | XML 布局规范（属性顺序、根布局选择、尺寸颜色引用） |
| `references/kotlin-code-style.md` | Kotlin 代码风格规范（缩进、注释、类结构、协程） |
| `references/architecture-spec.md` | 架构规范（MVVM 分层、网络请求流程、DI 模式） |

## 检查清单

生成文件后，验证以下项目：

### 布局文件检查
- [ ] 控件 ID 与布局文件名符合 `android-naming` 规范
- [ ] 根标签选择正确
- [ ] 没有 ConstraintLayout 嵌套 ConstraintLayout（含 include、merge、自定义控件）
- [ ] ConstraintLayout 保持单层，没有为分组新增 ConstraintLayout 子布局
- [ ] 命名空间声明完整且顺序正确
- [ ] 尺寸引用 `@dimen/dp_*`
- [ ] 颜色引用 `@color/color_*`
- [ ] 添加 `tools:ignore="ResourceName"`

### Kotlin 代码检查
- [ ] 文件头注释完整
- [ ] 包名符合项目结构
- [ ] 类、方法、成员变量命名符合 `android-naming` 规范
- [ ] 导入顺序正确

### 资源文件检查
- [ ] 图片导出为 3x PNG
- [ ] 图片命名符合 `android-naming` 规范
- [ ] 图片存放到 mipmap-xxhdpi
- [ ] 新颜色添加到 colors.xml

## 尺寸换算规则

- **设计图基准**：Figma 750px 宽度（xxhdpi，3x）
- **换算规则**：Figma px = Android dp（1:1 对应，无需换算）
- **统一单位**：dp（包括文字大小，不使用 sp）
- **示例**：Figma 测量 24px → Android `@dimen/dp_24`

## 更新日志

### v1.2.0
- 命名规范移出到 `android-naming` skill，本 skill 只保留代码与布局规范
- 参考文档改为指向 `android-naming`，drawable XML 规范中的命名引用同步更新

### v1.1.0
- 新增硬性约束：禁止 ConstraintLayout 嵌套 ConstraintLayout，include/merge/自定义控件同样适用
- 新增 ConstraintLayout 扁平化替代方案与生成后布局自检
- 检查清单增加 ConstraintLayout 嵌套检查项

### v1.0.0
- 初始版本
- 支持 Figma 设计稿转 XML 布局
- 支持生成 Kotlin Activity/Fragment 代码
- 所有命名规范拆分为独立文件
