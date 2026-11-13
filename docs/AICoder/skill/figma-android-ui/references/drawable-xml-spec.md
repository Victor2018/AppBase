# Drawable XML 编写规范 (Drawable XML Specification)

> 本文档定义 Android Drawable XML 文件的编写规则和模板。
> 文件名规则统一见 `android-naming` skill 的 `references/drawable-naming.md`；下文示例中注释标注的就是完整文件名。

## 目录

- [1. 实心圆角矩形 (shape)](#1-实心圆角矩形-shape)
- [2. 带方向圆角矩形](#2-带方向圆角矩形)
- [3. 边框圆角矩形 (border)](#3-边框圆角矩形-border)
- [4. 边框+填充圆角矩形](#4-边框填充圆角矩形)
- [5. 水波纹效果 (ripple)](#5-水波纹效果-ripple)
- [6. 渐变效果 (gradient)](#6-渐变效果-gradient)
- [7. 选择器 (selector)](#7-选择器-selector)
- [8. 分割线](#8-分割线)
- [9. 圆形](#9-圆形)
- [10. 椭圆](#10-椭圆)

---

## 1. 实心圆角矩形 (shape)

### 模板

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/white"/>
    <corners android:radius="@dimen/dp_12"/>
</shape>
```

### 示例

```xml
<!-- shape_white_radius_12.xml -->
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/white"/>
    <corners android:radius="@dimen/dp_12"/>
</shape>

<!-- shape_f9f9f9_radius_8.xml -->
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/color_F9F9F9"/>
    <corners android:radius="@dimen/dp_8"/>
</shape>

<!-- 全圆角（胶囊形） -->
<!-- shape_0084ff_radius_28.xml -->
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/color_0084FF"/>
    <corners android:radius="@dimen/dp_28"/>
</shape>
```

---

## 2. 带方向圆角矩形

### 模板

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/white"/>
    <corners
        android:topLeftRadius="@dimen/dp_12"
        android:topRightRadius="@dimen/dp_12"
        android:bottomLeftRadius="0dp"
        android:bottomRightRadius="0dp"/>
</shape>
```

### 示例

```xml
<!-- 只有顶部圆角 -->
<!-- shape_white_top_radius_24.xml -->
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/white"/>
    <corners
        android:topLeftRadius="@dimen/dp_24"
        android:topRightRadius="@dimen/dp_24"
        android:bottomLeftRadius="0dp"
        android:bottomRightRadius="0dp"/>
</shape>

<!-- 只有底部圆角 -->
<!-- shape_white_bottom_radius_18.xml -->
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/white"/>
    <corners
        android:topLeftRadius="0dp"
        android:topRightRadius="0dp"
        android:bottomLeftRadius="@dimen/dp_18"
        android:bottomRightRadius="@dimen/dp_18"/>
</shape>

<!-- 只有左侧圆角 -->
<!-- shape_f9f9f9_left_radius_12.xml -->
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/color_F9F9F9"/>
    <corners
        android:topLeftRadius="@dimen/dp_12"
        android:topRightRadius="0dp"
        android:bottomLeftRadius="@dimen/dp_12"
        android:bottomRightRadius="0dp"/>
</shape>

<!-- 左下+右上对角圆角 -->
<!-- shape_66000000_lb_rt_12.xml -->
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/color_66000000"/>
    <corners
        android:topLeftRadius="0dp"
        android:topRightRadius="@dimen/dp_12"
        android:bottomLeftRadius="@dimen/dp_12"
        android:bottomRightRadius="0dp"/>
</shape>
```

---

## 3. 边框圆角矩形 (border)

### 模板

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <stroke
        android:width="@dimen/dp_1"
        android:color="@color/color_0084FF"/>
    <corners android:radius="@dimen/dp_12"/>
</shape>
```

### 示例

```xml
<!-- border_0084ff_radius_12.xml -->
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <stroke
        android:width="@dimen/dp_1"
        android:color="@color/color_0084FF"/>
    <corners android:radius="@dimen/dp_12"/>
</shape>

<!-- 胶囊形边框 -->
<!-- border_0084ff_radius_28.xml -->
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <stroke
        android:width="@dimen/dp_1"
        android:color="@color/color_0084FF"/>
    <corners android:radius="@dimen/dp_28"/>
</shape>

<!-- 灰色边框 -->
<!-- border_e7e7e7_radius_12.xml -->
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <stroke
        android:width="@dimen/dp_1"
        android:color="@color/color_E7E7E7"/>
    <corners android:radius="@dimen/dp_12"/>
</shape>
```

---

## 4. 边框+填充圆角矩形

### 模板

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <stroke
        android:width="@dimen/dp_1"
        android:color="@color/color_0084FF"/>
    <solid android:color="@color/color_E0F0FF"/>
    <corners android:radius="@dimend/dp_12"/>
</shape>
```

### 示例

```xml
<!-- border_0084ff_e0f0ff_radius_12.xml -->
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <stroke
        android:width="@dimen/dp_1"
        android:color="@color/color_0084FF"/>
    <solid android:color="@color/color_E0F0FF"/>
    <corners android:radius="@dimen/dp_12"/>
</shape>
```

---

## 5. 水波纹效果 (ripple)

### 模板（有底色）

```xml
<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/color_0084FF">
    <item>
        <shape android:shape="rectangle">
            <solid android:color="@color/white"/>
            <corners android:radius="@dimen/dp_12"/>
        </shape>
    </item>
</ripple>
```

### 模板（无底色/透明）

```xml
<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/color_0084FF">
    <item android:id="@android:id/mask">
        <shape android:shape="rectangle">
            <solid android:color="@color/white"/>
            <corners android:radius="@dimen/dp_12"/>
        </shape>
    </item>
</ripple>
```

### 示例

```xml
<!-- ripple_0084ff_radius_12.xml -->
<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/color_0084FF">
    <item>
        <shape android:shape="rectangle">
            <solid android:color="@color/white"/>
            <corners android:radius="@dimen/dp_12"/>
        </shape>
    </item>
</ripple>

<!-- 底部圆角水波纹 -->
<!-- ripple_white_bottom_radius_12.xml -->
<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/color_1A000000">
    <item>
        <shape android:shape="rectangle">
            <solid android:color="@color/white"/>
            <corners
                android:topLeftRadius="0dp"
                android:topRightRadius="0dp"
                android:bottomLeftRadius="@dimen/dp_12"
                android:bottomRightRadius="@dimen/dp_12"/>
        </shape>
    </item>
</ripple>

<!-- 直角水波纹 -->
<!-- ripple_white_radius_0.xml -->
<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/color_1A000000">
    <item>
        <shape android:shape="rectangle">
            <solid android:color="@color/white"/>
        </shape>
    </item>
</ripple>
```

---

## 6. 渐变效果 (gradient)

### 线性渐变模板

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:startColor="@color/color_47D9A8"
        android:endColor="@color/color_00B578"
       :horizontal"/>
    <corners android:radius="@dimen/dp_12"/>
</shape>
```

### 示例

```xml
<!-- 绿色渐变 -->
<!-- gradient_47d9a8_00b578.xml -->
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:startColor="@color/color_47D9A8"
        android:endColor="@color/color_00B578"
        android:angle="0"/>
</shape>

<!-- 红色渐变带圆角 -->
<!-- gradient_ff554f_ff332d_radius_18.xml -->
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:startColor="@color/color_FF554F"
        android:endColor="@color/color_FF332D"
        android:angle="0"/>
    <corners android:radius="@dimen/dp_18"/>
</shape>

<!-- 白色到透明渐变（渐隐效果） -->
<!-- gradient_white_00ffffff.xml -->
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:startColor="@color/white"
        android:endColor="@color/color_00FFFFFF"
        android:angle="0"/>
</shape>
```

### 渐变方向

| angle 值 | 方向 |
|----------|------|
| 0 | 左→右 |
| 90 | 下→上 |
| 180 | 右→左 |
| 270 | 上→下 |

---

## 7. 选择器 (selector)

### 按钮选择器模板

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- 按下状态 -->
    <item android:state_pressed="true" android:drawable="@drawable/shape_0084ff_radius_12"/>
    <!-- 禁用状态 -->
    <item android:state_enabled="false" android:drawable="@drawable/shape_cccccc_radius_12"/>
    <!-- 默认状态 -->
    <item android:drawable="@drawable/shape_white_radius_12"/>
</selector>
```

### 示例

```xml
<!-- btn_selector.xml -->
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true" android:drawable="@drawable/shape_006ed9_radius_12"/>
    <item android:state_enabled="false" android:drawable="@drawable/shape_cccccc_radius_12"/>
    <item android:drawable="@drawable/shape_0084ff_radius_12"/>
</selector>

<!-- chk_selector.xml -->
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_checked="true" android:drawable="@mipmap/chk_checked"/>
    <item android:drawable="@mipmap/chk_normal"/>
</selector>

<!-- rb_selector.xml -->
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_checked="true" android:drawable="@mipmap/ic_rb_checked"/>
    <item android:drawable="@mipmap/ic_rb_normal"/>
</selector>

<!-- 底部导航图标选择器 -->
<!-- bottom_navigation_home_selector.xml -->
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_checked="true" android:drawable="@mipmap/ic_home_checked"/>
    <item android:drawable="@mipmap/ic_home_normal"/>
</selector>
```

---

## 8. 分割线

### 水平分割线

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/color_EEEEEE"/>
    <size android:height="@dimen/dp_1"/>
</shape>
```

### 垂直分割线

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/color_EEEEEE"/>
    <size android:width="@dimen/dp_1"/>
</shape>
```

---

## 9. 圆形

### 模板

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <solid android:color="@color/color_0084FF"/>
    <size
        android:width="@dimen/dp_48"
        android:height="@dimen/dp_48"/>
</shape>
```

### 带边框圆形

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <stroke
        android:width="@dimen/dp_2"
        android:color="@color/color_0084FF"/>
    <size
        android:width="@dimen/dp_48"
        android:height="@dimen/dp_48"/>
</shape>
```

---

## 10. 椭圆

### 模板

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <solid android:color="@color/color_0084FF"/>
    <size
        android:width="@dimen/dp_80"
        android:height="@dimen/dp_40"/>
</shape>
```

---

## 属性速查表

### shape 属性

| 属性 | 说明 |
|------|------|
| `android:shape` | 形状类型：`rectangle`、`oval`、`line`、`ring` |

### solid 属性

| 属性 | 说明 |
|------|------|
| `android:color` | 填充颜色 |

### stroke 属性

| 属性 | 说明 |
|------|------|
| `android:width` | 边框宽度 |
| `android:color` | 边框颜色 |
| `android:dashWidth` | 虚线段宽度 |
| `android:dashGap` | 虚线间隔 |

### corners 属性

| 属性 | 说明 |
|------|------|
| `android:radius` | 统一圆角 |
| `android:topLeftRadius` | 左上圆角 |
| `android:topRightRadius` | 右上圆角 |
| `android:bottomLeftRadius` | 左下圆角 |
| `android:bottomRightRadius` | 右下圆角 |

### gradient 属性

| 属性 | 说明 |
|------|------|
| `android:startColor` | 起始颜色 |
| `android:centerColor` | 中间颜色 |
| `android:endColor` | 结束颜色 |
| `android:angle` | 渐变角度（0/90/180/270） |
| `android:type` | 渐变类型：`linear`、`radial`、`sweep` |

### size 属性

| 属性 | 说明 |
|------|------|
| `android:width` | 宽度 |
| `android:height` | 高度 |

---

## Shape 创建规则

### 创建前检查

创建新 shape 前，**必须先检查 lib_common 模块是否已存在相同或相似的 drawable**：

```
检查路径：lib_common/src/main/res/drawable/
```

| 检查项 | 说明 |
|--------|------|
| 颜色是否一致 | 相同色值可直接复用 |
| 圆角是否一致 | 相同圆角可直接复用 |
| 类型是否一致 | shape/border/ripple 类型匹配 |

### 禁止创建的情况

以下情况**禁止**创建新 shape，应直接复用 lib_common 中已有的：

```xml
<!-- ❌ 禁止：lib_common 已有 shape_white_radius_12，不要重复创建 -->
<!-- 新模块中创建重复 shape -->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/white"/>
    <corners android:radius="@dimen/dp_12"/>
</shape>

<!-- ✅ 正确：直接引用 lib_common 中的 drawable -->
android:background="@drawable/shape_white_radius_12"
```

### 允许创建的情况

仅以下情况允许在 lib_common 中创建新 shape：

| 情况 | 说明 | 示例 |
|------|------|------|
| 新颜色 | 色值不存在 | 新增 `shape_FF6B35_radius_12.xml` |
| 新圆角 | 圆角值不存在 | 新增 `shape_white_radius_20.xml` |
| 新方向组合 | 方向组合不存在 | 新增 `shape_white_lt_rb_radius_12.xml` |
| 新类型 | border/ripple 组合不存在 | 新增 `ripple_FF6B35_radius_8.xml` |
| 新功能 | 渐变/选择器等不存在 | 新增 `gradient_FF6B35_FF332D.xml` |

### 创建位置

**所有 shape 必须创建在 lib_common 模块下**：

```
lib_common/src/main/res/drawable/
├── shape_white_radius_12.xml
├── shape_f9f9f9_radius_8.xml
├── border_0084ff_radius_12.xml
├── ripple_0084ff_radius_12.xml
└── ...
```

### 创建流程

```
1. 分析设计稿需求（颜色、圆角、类型）
        ↓
2. 检查 lib_common/drawable/ 是否已存在
        ↓
   ┌────┴────┐
   │ 已存在   │ 不存在
   ↓         ↓
 复用      创建新 shape
```

### 命名规范

新建 shape 的文件名遵循 `android-naming` skill 的 `references/drawable-naming.md`，本文不重复列出格式。

### 引用方式

各模块引用 lib_common 中的 shape：

```xml
<!-- 在布局中引用 -->
android:background="@drawable/shape_white_radius_12"

<!-- 在其他 drawable 中引用 -->
<item android:drawable="@drawable/shape_0084ff_radius_12"/>

<!-- 在 style 中引用 -->
<item name="android:background">@drawable/shape_f9f9f9_radius_8</item>
```

---

## 禁止创建 Vector Drawable

### 规则说明

**禁止手动创建 vector drawable XML 文件**（即使用 `<vector>` 标签的 XML）。

所有图标、图片资源必须从 Figma 设计稿导出下载，不使用代码创建矢量图。

### 禁止的写法

```xml
<!-- ❌ 禁止：手动创建 vector drawable XML -->
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#FF000000"
        android:pathData="M12,2L2,22h20L12,2z"/>
</vector>
```

### 正确做法

| 步骤 | 操作 |
|------|------|
| 1 | 从 Figma 设计稿导出图片资源（PNG/SVG/WebP） |
| 2 | 将导出的图片放入对应模块的 `mipmap-*` 或 `drawable-*` 目录 |
| 3 | 在布局中通过 `@mipmap/xxx` 或 `@drawable/xxx` 引用 |

### 图片来源

```
Figma 设计稿
    ↓
导出图片（PNG/WebP/SVG）
    ↓
放入项目资源目录
    ↓
代码中引用使用
```

### 适用范围

| 资源类型 | 来源 | 说明 |
|----------|------|------|
| 图标 `ic_*` | Figma 导出 | 所有应用图标 |
| 图片 `img_*` | Figma 导出 | 所有展示图片 |
| Logo `module_*_logo` | Figma 导出 | 模块 Logo |
| Shape 背景 | 代码创建 | 仅 shape/border/ripple/gradient 允许代码创建 |

---

## 图片下载前检查规则

### 核心原则

**下载图片前，必须先检查 lib_common 模块是否已存在该图片**，特别是共用图标（如返回箭头、关闭按钮等）。

如果 lib_common 中已有，则**禁止重复下载**，直接引用即可。

### 检查流程

```
1. 确定需要下载的图片资源
        ↓
2. 检查 lib_common/src/main/res/mipmap-* 是否已存在
        ↓
   ┌────┴────┐
   │ 已存在   │ 不存在
   ↓         ↓
 直接引用   从 Figma 下载
```

### 检查路径

```
lib_common/src/main/res/
├── mipmap-mdpi/
├── mipmap-hdpi/
├── mipmap-xhdpi/
├── mipmap-xxhdpi/
├── mipmap-xxxhdpi/
└── drawable/
```

### 常见共用图标（优先检查）

以下图标通常已在 lib_common 中定义，下载前**必须检查**：

| 图标类型 | 命名示例 | 说明 |
|----------|----------|------|
| 返回箭头 | `ic_back_black`、`ic_back_white`、`ic_arrow_back` | 标题栏返回按钮 |
| 关闭按钮 | `ic_close`、`ic_cancel` | 弹窗关闭 |
| 更多菜单 | `ic_more`、`ic_menu_more` | 右上角更多 |
| 搜索图标 | `ic_search` | 搜索入口 |
| 下拉箭头 | `ic_arrow_down`、`ic_expand` | 下拉展开 |
| 勾选图标 | `ic_check`、`ic_selected` | 选中状态 |
| 空状态图 | `img_empty`、`ic_empty` | 列表为空 |
| 加载图标 | `ic_loading`、`img_loading` | 加载中 |

### 引用方式

如果 lib_common 已有该图片，各模块直接引用：

```xml
<!-- 在布局中引用 lib_common 的图片 -->
<ImageView
    android:id="@+id/mIvBack"
    android:src="@mipmap/ic_back_black"
    ... />

<!-- 无需在各模块重复放置该图片资源 -->
```

### 禁止行为

```
❌ 禁止：lib_common 已有 ic_back_black，又在业务模块 mipmap/ 中下载放置
❌ 禁止：不检查直接下载所有图片资源
❌ 禁止：各模块重复存放相同的共用图标

✅ 正确：先检查 lib_common，已有则直接引用
✅ 正确：仅下载 lib_common 中不存在的模块专属图片
```

### 模块专属图片

以下情况允许在业务模块中放置图片：

| 类型 | 说明 | 示例 |
|------|------|------|
| 模块专属图标 | 仅该模块使用的图标 | `ic_pay_way_alipay`（支付模块专属） |
| 模块专属图片 | 仅该模块展示的图片 | `img_order_detail_banner`（订单模块专属） |
| 业务 Logo | 模块专属 Logo | `module_shop_logo`（商城模块专属） |
