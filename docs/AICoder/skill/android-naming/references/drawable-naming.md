# Drawable 命名规范 (Drawable Naming Specification)

> 本文档定义 Android Drawable 资源文件的命名规则。

## 基本格式

```
类型_描述[_属性]
```

## 类型前缀对照表

| 类型 | 前缀 | 命名格式 | 示例 |
|------|------|----------|------|
| 实心形状 | `shape_` | `shape_颜色_radius_圆角` | `shape_white_radius_12.xml` |
| 带方向圆角 | `shape_` | `shape_颜色_方向_radius_圆角` | `shape_white_top_radius_24.xml` |
| 边框形状 | `border_` | `border_颜色_radius_圆角` | `border_0084ff_radius_12.xml` |
| 边框+填充 | `border_` | `border_边框色_填充色_radius_圆角` | `border_0084ff_e0f0ff_radius_12.xml` |
| 水波纹 | `ripple_` | `ripple_颜色_radius_圆角` | `ripple_0084ff_radius_12.xml` |
| 渐变 | `gradient_` | `gradient_起始色_结束色[_radius_圆角]` | `gradient_47d9a8_00b578.xml` |
| 选择器 | - | `功能_selector` | `btn_selector.xml` |
| 背景 | `bg_` | `bg_用途` | `bg_loading_dlg.xml` |
| 图标 | `ic_` | `ic_描述` | `ic_arrow_right_gray.xml` |
| 进度条 | `pb_` | `pb_用途` | `pb_update_download.xml` |
| 模块Logo | `module_` | `module_模块名_logo` | `module_home_logo.xml` |
| 评分条 | `rating_bar_` | `rating_bar_用途` | `rating_bar_style.xml` |

## 实心形状 (shape_)

### 基础格式

```
shape_颜色_radius_圆角值.xml
```

### 命名示例

```xml
<!-- 白色背景，12dp 圆角 -->
shape_white_radius_12.xml

<!-- 白色背景，24dp 圆角 -->
shape_white_radius_24.xml

<!-- 灰色背景（#F9F9F9），12dp 圆角 -->
shape_f9f9f9_radius_12.xml

<!-- 灰色背景（#F9F9F9），18dp 圆角 -->
shape_f9f9f9_radius_18.xml

<!-- 灰色背景（#F9F9F9），8dp 圆角 -->
shape_f9f9f9_radius_8.xml
```

### 带方向的圆角

```xml
<!-- 只有顶部圆角 -->
shape_white_top_radius_24.xml
shape_f9f9f9_top_radius_12.xml

<!-- 只有底部圆角 -->
shape_white_bottom_radius_18.xml

<!-- 只有左侧圆角 -->
shape_f9f9f9_left_radius_12.xml

<!-- 只有右侧圆角 -->
shape_f9f9f9_right_radius_12.xml

<!-- 左下+右上对角圆角 -->
shape_66000000_lb_rt_12.xml

<!-- 左上+右对角圆角 -->
shape_ff3141_lt_r_radius_16.xml

<!-- 左下+右上圆角 -->
shape_cce6ff_lb_rt_18.xml
```

### 方向缩写

| 缩写 | 含义 |
|------|------|
| `top` | 顶部（左上+右上） |
| `bottom` | 底部（左下+右下） |
| `left` | 左侧（左上+左下） |
| `right` | 右侧（右上+右下） |
| `lt` | 左上 (left-top) |
| `lb` | 左下 (left-bottom) |
| `rt` | 右上 (right-top) |
| `rb` | 右下 (right-bottom) |

## 边框形状 (border_)

### 基础格式

```
border_边框颜色_radius_圆角值.xml
border_边框颜色_填充颜色_radius_圆角值.xml
```

### 命名示例

```xml
<!-- 蓝色边框，12dp 圆角 -->
border_0084ff_radius_12.xml

<!-- 蓝色边框，28dp 圆角（胶囊形） -->
border_0084ff_radius_28.xml

<!-- 灰色边框，12dp 圆角 -->
border_e7e7e7_radius_12.xml

<!-- 灰色边框，28dp 圆角 -->
border_e7e7e7_radius_28.xml

<!-- 蓝色边框 + 浅蓝填充，12dp 圆角 -->
border_0084ff_e0f0ff_radius_12.xml

<!-- 灰色边框 + 灰色填充，20dp 圆角 -->
border_e7e7e7_f9f9f9_radius_20.xml
```

## 水波纹 (ripple_)

### 基础格式

```
ripple_颜色_radius_圆角值.xml
```

### 命名示例

```xml
<!-- 蓝色水波纹，12dp 圆角 -->
ripple_0084ff_radius_12.xml

<!-- 蓝色水波纹，8dp 圆角 -->
ripple_0084ff_radius_8.xml

<!-- 蓝色水波纹，18dp 圆角 -->
ripple_0084ff_radius_18.xml

<!-- 蓝色水波纹，28dp 圆角（胶囊） -->
ripple_0084ff_radius_28.xml

<!-- 白色水波纹，18dp 圆角 -->
ripple_white_radius_18.xml

<!-- 白色水波纹，0dp 圆角（直角） -->
ripple_white_radius_0.xml

<!-- 白色水波纹，16dp 圆角 -->
ripple_white_radius_16.xml

<!-- 白色水波纹，底部圆角 -->
ripple_white_bottom_radius_12.xml
ripple_white_bottom_radius_18.xml

<!-- 白色水波纹，左侧圆角 -->
ripple_white_left_radius_40.xml

<!-- 灰色水波纹 -->
ripple_f9f9f9_radius_12.xml
ripple_cce6ff_radius_12.xml
```

## 渐变 (gradient_)

### 基础格式

```
gradient_起始色_结束色.xml
gradient_起始色_结束色_radius_圆角值.xml
```

### 命名示例

```xml
<!-- 绿色渐变 -->
gradient_47d9a8_00b578.xml

<!-- 红色渐变，18dp 圆角 -->
gradient_ff554f_ff332d_radius_18.xml

<!-- 白色到透明渐变 -->
gradient_white_00ffffff.xml
gradient_00ffffff_white.xml

<!-- 蓝紫渐变，12dp 圆角 -->
gradient_556386_8597bd_radius_12.xml
```

## 选择器 (selector)

### 命名示例

```xml
<!-- 按钮选择器 -->
btn_selector.xml
btn_grey_selector.xml

<!-- 复选框选择器 -->
chk_selector.xml

<!-- 单选框选择器 -->
rb_selector.xml
rb_enable_selector.xml

<!-- 指示器选择器 -->
indicator_selector.xml

<!-- 底部导航选择器 -->
bottom_navigation_home_selector.xml
bottom_navigation_delivery_selector.xml
bottom_navigation_map_selector.xml
bottom_navigation_me_selector.xml

<!-- 日期筛选选择器 -->
rb_supply_date_filter_selector.xml
rb_trade_date_selector.xml
```

## 背景 (bg_)

### 命名示例

```xml
<!-- 对话框背景 -->
bg_loading_dlg.xml
bg_common_tip_dlg.xml

<!-- Toast 背景 -->
bg_toast.xml

<!-- 启动图指示器 -->
bg_splash_indicator_focus.xml
bg_splash_indicator_normal.xml

<!-- 优惠券标记 -->
bg_coupon_marker.xml
```

## 命名规则总结

| 规则 | 说明 |
|------|------|
| 使用 snake_case | 全小写，下划线分隔 |
| 类型前缀必须 | 必须以类型前缀开头 |
| 颜色用色值 | 使用十六进制色值（小写） |
| 圆角标注 | 有圆角时必须标注 `radius_值` |
| 方向标注 | 部分圆角时标注方向 |

## Drawable 文件位置

**所有 shape、border、ripple、gradient、selector、bg 等 Drawable 资源统一定义在 lib_common 模块下**，各业务模块和 app 模块直接引用，不重复定义。

| 位置 | 说明 |
|------|------|
| `lib_common/src/main/res/drawable/` | **唯一 Drawable 定义位置**，所有 shape/border/ripple/gradient/selector/bg 统一存放 |
| `app/src/main/res/drawable/` | 不定义 Drawable，仅引用 lib_common |
| `module_*/src/main/res/drawable/` | 不定义 Drawable，仅引用 lib_common |

### 规则

- 新增 shape/border/ripple/gradient/selector/bg 必须添加到 `lib_common/drawable/`
- 禁止在各业务模块中单独定义 Drawable 资源
- 禁止在 app 模块中定义 Drawable 资源
- 所有模块通过 `@drawable/xxx` 引用 lib_common 中的 Drawable

### 例外

以下资源可以放在对应模块中：

| 资源类型 | 位置 | 说明 |
|----------|------|------|
| `ic_*` 图标 | 各模块 `mipmap-*` | 模块专属图标 |
| `img_*` 图片 | 各模块 `mipmap-*` | 模块专属图片 |
| `module_*_logo.xml` | 各模块 `drawable/` | 模块 Logo 矢量图 |
