# 样式主题命名规范 (Style & Theme Naming Specification)

> 本文档定义 Android 样式和主题的命名规则。

## 应用主题命名

### 基础格式

```
AppTheme[.变体]
```

### 命名示例

```xml
<!-- 基础主题 -->
<style name="AppTheme" parent="Theme.MaterialComponents.DayNight.DarkActionBar.Bridge">
    <item name="colorPrimary">@color/colorPrimary</item>
    <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
    <item name="colorAccent">@color/colorAccent</item>
</style>

<!-- 无 ActionBar 主题 -->
<style name="AppTheme.NoActionBar">
    <item name="windowActionBar">false</item>
    <item name="windowNoTitle">true</item>
</style>

<!-- 全屏主题 -->
<style name="AppTheme.FullScreen" parent="AppTheme.NoActionBar">
    <item name="android:windowFullscreen">true</item>
</style>

<!-- 透明主题 -->
<style name="AppTheme.Translucent" parent="AppTheme.NoActionBar">
    <item name="android:windowBackground">@android:color/transparent</item>
    <item name="android:windowIsTranslucent">true</item>
</style>

<!-- 启动页主题 -->
<style name="AppTheme.Splash" parent="AppTheme.NoActionBar">
    <item name="android:windowBackground">@mipmap/img_splash</item>
</style>
```

## Material3 主题命名

### 基础格式

```
Base.Theme.{项目名}[.变体]
Theme.{项目名}[.变体]
```

### 命名示例

```xml
<!-- 基础主题 -->
<style name="Base.Theme.YdjDriver" parent="Theme.Material3.DayNight.NoActionBar">
    <item name="colorPrimary">@color/colorPrimary</item>
    <item name="colorPrimaryContainer">@color/colorPrimaryContainer</item>
</style>

<!-- 最终主题（继承基础主题） -->
<style name="Theme.YdjDriver" parent="Base.Theme.YdjDriver" />
```

## 对话框主题命名

### 基础格式

```
BaseDialog[变体]
```

### 命名示例

```xml
<!-- 基础对话框 -->
<style name="BaseDialog" parent="Theme.AppCompat.Light.Dialog">
    <item name="android:windowBackground">@android:color/transparent</item>
    <item name="android:windowNoTitle">true</item>
</style>

<!-- 无标题对话框 -->
<style name="BaseNoTitleDialog" parent="BaseDialog" />

<!-- 底部弹出对话框 -->
<style name="BottomSheetDialog" parent="Theme.Design.BottomSheetDialog">
    <item name="bottomSheetStyle">@style/TransparentBottomSheetStyle</item>
</style>

<!-- 透明底部样式 -->
<style name="TransparentBottomSheetStyle" parent="SheetStyle" />
```

## Toolbar 样式命名

### 命名示例

```xml
<!-- Toolbar 样式 -->
<style name="ToolbarStyle" parent="Widget.AppCompat.Toolbar">
    <item name="android:layout_width">match_parent</item>
    <item name="android:layout_height">?attr/actionBarSize</item>
    <item name="android:background">@color/white</item>
    <item name="contentInsetStart">0dp</item>
</style>

<!-- Toolbar 标题样式 -->
<style name="ToolbarTitleStyle" parent="Widget.AppCompat.TextView">
    <item name="android:textColor">@color/color_1A1A1A</item>
    <item name="android:textSize">@dimen/dp_36</item>
    <item name="android:textStyle">bold</item>
</style>

<!-- Toolbar 标题外观 -->
<style name="Toolbar.TitleText" parent="TextAppearance.Widget.AppCompat.Toolbar.Title">
    <item name="android:textSize">@dimen/dp_36</item>
</style>
```

## TabLayout 样式命名

### 命名示例

```xml
<!-- 可滚动标签样式 -->
<style name="ScrollableTabStyle">
    <item name="tabMode">scrollable</item>
    <item name="tabGravity">fill</item>
</style>

<!-- 可滚动短标签样式 -->
<style name="ScrollableShortTabStyle" parent="ScrollableTabStyle">
    <item name="tabMinWidth">@dimen/dp_80</item>
</style>

<!-- 固定标签样式 -->
<style name="FixedTabStyle">
    <item name="tabMode">fixed</item>
    <item name="tabGravity">fill</item>
</style>

<!-- 无指示器标签样式 -->
<style name="NoneIndicatorTabStyle">
    <item name="android:background">@android:color/transparent</item>
</style>

<!-- 消息固定短标签样式 -->
<style name="MessageFixedShortTabStyle" parent="ScrollableShortTabStyle" />

<!-- 反馈固定短标签样式 -->
<style name="MyFeedbackFixedShortTabStyle" parent="ScrollableShortTabStyle" />

<!-- 标签文字外观-正常 -->
<style name="TabTextNormalAppearance">
    <item name="android:textColor">@color/color_999999</item>
    <item name="android:textSize">@dimen/dp_28</item>
</style>

<!-- 标签文字外观-选中 -->
<style name="TabTextSelectedAppearance">
    <item name="android:textColor">@color/color_1A1A1A</item>
    <item name="android:textSize">@dimen/dp_32</item>
    <item name="android:textStyle">bold</item>
</style>
```

## 动画样式命名

### 对话框动画

```xml
<!-- 底部弹出动画 -->
<style name="BottomDialogAnimShow">
    <item name="android:windowEnterAnimation">@anim/anim_bottom_enter</item>
    <item name="android:windowExitAnimation">@anim/anim_bottom_exit</item>
</style>

<!-- 右侧弹出动画 -->
<style name="RightDialogAnimShow">
    <item name="android:windowEnterAnimation">@anim/anim_right_enter</item>
    <item name="android:windowExitAnimation">@anim/anim_right_exit</item>
</style>
```

### PopupWindow 样式

```xml
<!-- 基础弹窗样式 -->
<style name="BasePopWindowStyle">
    <item name="android:windowEnterAnimation">@anim/anim_base_popwindow_fade_in</item>
    <item name="android:windowExitAnimation">@anim/anim_base_popwindow_fade_out</item>
</style>

<!-- 顶部弹窗样式 -->
<style name="BasePopWindowStyleTop">
    <item name="android:windowEnterAnimation">@anim/anim_popwindow_top_in</item>
    <item name="android:windowExitAnimation">@anim/anim_popwindow_top_out</item>
</style>

<!-- 底部弹窗样式 -->
<style name="BasePopWindowStyleBottom">
    <item name="android:windowEnterAnimation">@anim/anim_bottom_enter</item>
    <item name="android:windowExitAnimation">@anim/anim_bottom_exit</item>
</style>

<!-- 透明弹窗样式 -->
<style name="BasePopWindowStyleAlpha">
    <item name="android:windowEnterAnimation">@anim/anim_popwindow_alpha_in</item>
    <item name="android:windowExitAnimation">@anim/anim_popwindow_alpha_out</item>
</style>
```

## 工具样式命名

### 分割线样式

```xml
<!-- 水平分割线 -->
<style name="base_line_horizontal">
    <item name="android:layout_width">match_parent</item>
    <item name="android:layout_height">@dimen/dp_1</item>
    <item name="android:background">@color/color_EEEEEE</item>
</style>

<!-- 垂直分割线 -->
<style name="base_line_vertical">
    <item name="android:layout_width">@dimen/dp_1</item>
    <item name="android:layout_height">match_parent</item>
    <item name="android:background">@color/color_EEEEEE</item>
</style>
```

### 底部导航标题样式

```xml
<!-- 激活状态 -->
<style name="bottom_tab_title_active">
    <item name="android:textColor">@color/color_0084FF</item>
    <item name="android:textSize">@dimen/dp_20</item>
</style>

<!-- 未激活状态 -->
<style name="bottom_tab_title_inactive">
    <item name="android:textColor">@color/color_999999</item>
    <item name="android:textSize">@dimen/dp_20</item>
</style>
```

## AppBar 覆盖样式

```xml
<!-- AppBar 覆盖 -->
<style name="AppTheme.AppBarOverlay" parent="ThemeOverlay.AppCompat.Dark.ActionBar" />

<!-- 弹窗覆盖 -->
<style name="AppTheme.PopupOverlay" parent="ThemeOverlay.AppCompat.Light" />
```

## 命名规则总结

| 规则 | 说明 |
|------|------|
| PascalCase | 样式名使用大驼峰命名 |
| 点号继承 | 使用 `.` 表示继承关系 |
| 描述清晰 | 名称应能表达样式用途 |
| 前缀统一 | 同类样式使用统一前缀 |
| 后缀规范 | 动画样式使用 `Anim` 后缀，外观使用 `Appearance` 后缀 |
