# 颜色命名规范 (Color Naming Specification)

> 本文档定义 Android 颜色资源的命名规则。

## 基本格式

```
color_ + 十六进制色值（大写）
```

## 命名规则

### 不透明颜色（6位十六进制）

```xml
<!-- 格式：color_ + RRGGBB -->
<color name="color_1A1A1A">#1A1A1A</color>
<color name="color_333333">#333333</color>
<color name="color_666666">#666666</color>
<color name="color_777777">#777777</color>
<color name="color_999999">#999999</color>
<color name="color_EEEEEE">#EEEEEE</color>
<color name="color_F9F9F9">#F9F9F9</color>
<color name="color_F8F8F8">#F8F8F8</color>
<color name="color_FFFFFF">#FFFFFF</color>
<color name="color_000000">#000000</color>
```

### 品牌色/功能色（6位十六进制）

```xml
<!-- 品牌蓝色 -->
<color name="color_0084FF">#0084FF</color>
<color name="color_027CFB">#027CFB</color>
<color name="color_007DF2">#007DF2</color>

<!-- 警告/错误红色 -->
<color name="color_FF554F">#FF554F</color>
<color name="color_FF332D">#FF332D</color>
<color name="color_EB4F3A">#EB4F3A</color>
```

### 带透明度的颜色（8位十六进制）

```xml
<!-- 格式：color_ + AARRGGBB（Alpha + RGB） -->

<!-- 白色透明度 -->
<color name="color_8CFFFFFF">#8CFFFFFF</color>  <!-- 55% 透明度 -->
<color name="color_33FFFFFF">#33FFFFFF</color>  <!-- 20% 透明度 -->
<color name="color_1AEEEEEE">#1AEEEEEE</color>  <!-- 10% 透明度 -->

<!-- 黑色透明度 -->
<color name="color_901D1C21">#901D1C21</color>  <!-- 56% 透明度 -->
<color name="color_80000000">#80000000</color>  <!-- 50% 透明度 -->
<color name="color_CC000000">#CC000000</color>  <!-- 80% 透明度 -->
<color name="color_4D000000">#4D000000</color>  <!-- 30% 透明度 -->
<color name="color_66000000">#66000000</color>  <!-- 40% 透明度 -->
<color name="color_99000000">#99000000</color>  <!-- 60% 透明度 -->
<color name="color_1A000000">#1A000000</color>  <!-- 10% 透明度 -->
<color name="color_E6000000">#E6000000</color>  <!-- 90% 透明度 -->
<color name="color_33000000">#33000000</color>  <!-- 20% 透明度 -->
```

## 标准颜色（保留名称）

以下标准颜色名称可以使用：

```xml
<color name="white">#FFFFFF</color>
<color name="black">#000000</color>
<color name="transparent">#00000000</color>
<color name="half_alpha">#80000000</color>
```

## 主题色（保留名称）

```xml
<color name="colorPrimary">@color/color_027CFB</color>
<color name="colorPrimaryDark">@color/color_007DF2</color>
<color name="colorAccent">@color/color_0084FF</color>
```

## 禁止的命名方式

```xml
<!-- ❌ 错误：不要使用语义化命名 -->
<color name="textPrimary">#1A1A1A</color>
<color name="textSecondary">#666666</color>
<color name="background">#F9F9F9</color>
<color name="brandBlue">#0084FF</color>
<color name="errorRed">#FF554F</color>

<!-- ✅ 正确：使用色值命名 -->
<color name="color_1A1A1A">#1A1A1A</color>
<color name="color_666666">#666666</color>
<color name="color_F9F9F9">#F9F9F9</color>
<color name="color_0084FF">#0084FF</color>
<color name="color_FF554F">#FF554F</color>
```

## 新增颜色流程

1. 从设计稿中提取颜色值（如 `#F9F9FB`）
2. 转换为大写十六进制（如 `F9F9FB`）
3. 在 `lib_common/src/main/res/values/colors.xml` 中添加颜色定义：

```xml
<color name="color_F9F9FB">#F9F9FB</color>
```

4. 在布局文件中通过 `@color/color_F9F9FB` 引用

## 颜色文件位置

**所有颜色统一定义在 lib_common 模块下**，各业务模块和 app 模块直接引用，不重复定义。

| 位置 | 说明 |
|------|------|
| `lib_common/src/main/res/values/colors.xml` | **唯一颜色定义位置**，所有颜色统一存放 |
| `app/src/main/res/values/colors.xml` | 不定义颜色，仅引用 lib_common |
| `module_*/src/main/res/values/colors.xml` | 不定义颜色，仅引用 lib_common |

### 规则

- 新增颜色必须添加到 `lib_common/colors.xml`
- 禁止在各业务模块中单独定义颜色
- 禁止在 app 模块中定义颜色
- 所有模块通过 `@color/color_*` 引用 lib_common 中的颜色

## 命名规则总结

| 规则 | 说明 |
|------|------|
| 前缀 | 必须以 `color_` 开头 |
| 色值大写 | 十六进制字母必须大写（A-F） |
| 6位格式 | 不透明颜色：`color_RRGGBB` |
| 8位格式 | 带透明度：`color_AARRGGBB` |
| 禁止语义 | 不得使用语义化命名 |
