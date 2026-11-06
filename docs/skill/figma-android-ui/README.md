# Figma to Android XML Skill

将 Figma 设计稿转换为符合 Android 项目规范的 XML 布局文件的 Qoder Skill。

**适用范围**：所有 Android 项目

## 功能特性

- 自动读取 Figma 设计稿结构和样式信息
- 生成符合项目规范的 XML 布局文件
- 生成配套的 Kotlin Activity/Fragment 代码
- 遵循项目的命名规范、代码风格和架构模式
- 支持图片资源导出和规范命名

## 安装方法

### 1. 复制 Skill 文件

将 `figma-android-ui` 文件夹复制到 Qoder 的 skills 目录：

**用户级安装**（推荐，所有项目可用）：
```bash
# Windows
copy /r figfigma-android-ui "C:\Users\81544\.qoder-cn\skills\figma-android-ui"

# macOS/Linux
cp -r figma-android-ui ~/.qoder-cn/skills/figma-android-ui
```

**项目级安装**（仅当前项目可用）：
```bash
cp -r figma-android-ui /path/to/your/project/.qoder/skills/
```

### 2. 重启 Qoder

```
/skills reload
```

### 3. 验证安装

```
/skills list
```

## 文件结构

```
figma-android-ui/
├── SKILL.md                              # 主技能文件
├── README.md                             # 说明文档
└── references/                           # 规范文档（单一职责）
    ├── class-naming.md                   # 类命名规范
    ├── variable-naming.md                # 变量命名规范
    ├── method-naming.md                  # 方法命名规范
    ├── layout-file-naming.md             # 布局文件命名规范
    ├── view-id-naming.md                 # 控件 ID 命名规范
    ├── color-naming.md                   # 颜色命名规范
    ├── drawable-naming.md                # Drawable 命名规范
    ├── drawable-xml-spec.md              # Drawable XML 编写规范
    ├── image-naming.md                   # 图片资源命名规范
    ├── package-naming.md                 # 包命名规范
    ├── style-theme-naming.md             # 样式主题命名规范
    ├── animation-naming.md               # 动画命名规范
    ├── string-naming.md                  # 字符串命名规范
    ├── xml-layout-spec.md                # XML 布局规范
    ├── kotlin-code-style.md              # Kotlin 代码风格规范
    └── architecture-spec.md              # 架构规范
```

## 规范文件说明

每个规范文件只包含**单一类型**的规则，不混入其他规则：

| 文件 | 内容 | 适用场景 |
|------|------|----------|
| `class-naming.md` | 类命名（Activity, Fragment, VM, DS, Bean, Req, Event 等） | 创建新类时 |
| `variable-naming.md` | 变量命名（成员变量 m 前缀、局部变量、常量） | 声明变量时 |
| `method-naming.md` | 方法命名（initView, subscribeUi, sendRequest 等） | 定义方法时 |
| `layout-file-naming.md` | 布局文件命名（activity_, fragment_, dlg_, rv_ 等） | 创建 XML 布局时 |
| `view-id-naming.md` | 控件 ID 命名（mTv, mIv, mBtn 等） | 编写 XML 布局时 |
| `color-naming.md` | 颜色命名（color_XXXXXX） | 定义/引用颜色时 |
| `drawable-naming.md` | Drawable 命名（shape_, ripple_, border_ 等） | 创建 Drawable 时 |
| `drawable-xml-spec.md` | Drawable XML 编写规范（shape、ripple、gradient 模板） | 编写 Drawable XML 时 |
| `image-naming.md` | 图片命名（ic_, img_ 等） | 导入图片资源时 |
| `package-naming.md` | 包命名 | 创建新包/模块时 |
| `style-theme-naming.md` | 样式/主题命名 | 定义 Style/Theme 时 |
| `animation-naming.md` | 动画命名 | 创建动画资源时 |
| `string-naming.md` | 字符串命名 | 定义字符串资源时 |
| `xml-layout-spec.md` | XML 布局编写规范（属性顺序、根布局选择等） | 编写 XML 时 |
| `kotlin-code-style.md` | Kotlin 代码风格（缩进、注释、类结构等） | 编写 Kotlin 时 |
| `architecture-spec.md` | 架构规范（MVVM 分层、网络请求、DI 等） | 设计架构时 |

## 使用方法

### 触发关键词

- **设计工具**：`Figma`、`MasterGo`、`即时设计`、`蓝湖`、`Sketch`
- **转换动作**：`设计稿转代码`、`UI 稿转 XML`、`布局生成`
- **技术术语**：`控件 ID 命名`、`dp 转换`、`ViewBinding`

### 使用示例

```
用户：请根据 Figma 设计稿生成登录页面的 XML 布局和 Kotlin 代码
      Figma 链接：https://www.figma.com/file/abc123

Skill 执行：
1. 获取 Figma 设计数据
2. 分析页面结构
3. 生成 activity_login.xml（ID 使用 mTv、mIv 规范）
4. 生成 LoginActivity.kt（继承 BaseActivity）
5. 提示导出图片资源到 mipmap-xxhdpi
```

## 规范速查

### 控件 ID 命名

| 控件类型 | 前缀 | 示例 |
|----------|------|------|
| TextView | `mTv` | `mTvTitle`, `mTvContent` |
| ImageView | `mIv` | `mIvBack`, `mIvAvatar` |
| Button | `mBtn` | `mBtnLogin`, `mBtnSubmit` |
| EditText | `mEt` | `mEtPhone`, `mEtPassword` |
| RecyclerView | `mRv` | `mRvList`, `mRvImage` |

### 尺寸规范

- 设计图：750px 宽度（xxhdpi，3x）
- 换算：Figma px = Android dp（1:1 对应）
- 单位：统一使用 dp（包括文字大小）

### 颜色规范

- 格式：`color_` + 十六进制色值（大写）
- 示例：`color_0084FF`、`color_F9F9F9`
- 禁止语义化命名

## 许可证

与 ydj-driver-android 项目相同。

---

**作者**: Victor
**创建日期**: 2026-11-05
**版本**: v1.0.0
