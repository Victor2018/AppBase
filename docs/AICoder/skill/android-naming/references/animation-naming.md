# 动画命名规范 (Animation Naming Specification)

> 本文档定义 Android 动画资源文件的命名规则。

## 基本格式

```
anim_场景_方向.xml
```

## 动画类型前缀

| 前缀 | 场景 | 说明 |
|------|------|------|
| `anim_activity_` | Activity 转场 | Activity 进出动画 |
| `anim_fragment_` | Fragment 转场 | Fragment 进出动画 |
| `anim_bottom_` | 底部弹出 | 从底部进入/退出 |
| `anim_top_` | 顶部弹出 | 从顶部进入/退出 |
| `anim_right_` | 右侧弹出 | 从右侧进入/退出 |
| `anim_left_` | 左侧弹出 | 从左侧进入/退出 |
| `anim_alpha_` | 透明度 | 淡入淡出动画 |
| `anim_popwindow_` | PopupWindow | 弹窗动画 |
| `anim_base_popwindow_` | 基础弹窗 | 基础弹窗动画 |
| `anim_loading` | 加载动画 | 旋转加载动画 |
| `dd_` | 下拉菜单 | Dropdown 菜单动画 |

## Activity 转场动画

```xml
<!-- 进入动画 -->
anim_activity_enter.xml

<!-- 退出动画 -->
anim_activity_exit.xml

<!-- 进入返回动画（按返回键） -->
anim_activity_enter_back.xml

<!-- 退出返回动画 -->
anim_activity_exit_back.xml
```

## Fragment 转场动画

```xml
<!-- 进入动画 -->
anim_fragment_enter.xml

<!-- 退出动画 -->
anim_fragment_exit.xml
```

## 方向性动画

### 底部弹出

```xml
anim_bottom_enter.xml      # 从底部进入
anim_bottom_exit.xml       # 退出到底部
```

### 右侧弹出

```xml
anim_right_enter.xml       # 从右侧进入
anim_right_exit.xml        # 退出到右侧
```

### 顶部弹出

```xml
anim_top_enter.xml         # 从顶部进入
anim_top_exit.xml          # 退出到顶部
```

## 透明度动画

```xml
anim_alpha_in.xml          # 淡入
anim_alpha_out.xml         # 淡出
```

## PopupWindow 动画

### 基础弹窗

```xml
anim_base_popwindow_fade_in.xml     # 淡入
anim_base_popwindow_fade_out.xml    # 淡出
```

### 透明度变化

```xml
anim_popwindow_alpha_in.xml         # 透明度增加
anim_popwindow_alpha_out.xml        # 透明度减少
```

### 位置变化

```xml
anim_popwindow_top_in.xml           # 从顶部进入
anim_popwindow_top_out.xml          # 退出到顶部
```

## 加载动画

```xml
anim_loading.xml           # 旋转加载动画
```

## 下拉菜单动画

```xml
dd_mask_in.xml             # 遮罩进入
dd_mask_out.xml            # 遮罩退出
dd_menu_in.xml             # 菜单进入
dd_menu_out.xml            # 菜单退出
```

## 登录页切换动画

```xml
anim_enter_code_login.xml      # 进入验证码登录
anim_enter_pwd_login.xml       # 进入密码登录
anim_exit_code_login.xml       # 退出资登录
anim_exit_pwd_login.xml        # 退出密码登录
```

## 动画方向后缀

| 后缀 | 含义 |
|------|------|
| `_enter` | 进入 |
| `_exit` | 退出 |
| `_in` | 进入（淡入） |
| `_out` | 退出（淡出） |
| `_back` | 返回 |

## 使用示例

### 在样式中引用

```xml
<!-- 底部弹出动画样式 -->
<style name="BottomDialogAnimShow">
    <item name="android:windowEnterAnimation">@anim/anim_bottom_enter</item>
    <item name="android:windowExitAnimation">@anim/anim_bottom_exit</item>
</style>

<!-- Activity 转场 -->
overridePendingTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
```

### 在代码中引用

```kotlin
// Fragment 事务动画
supportFragmentManager.beginTransaction()
    .setCustomAnimations(
        R.anim.anim_fragment_enter,
        R.anim.anim_fragment_exit
    )
    .replace(R.id.fl_container, fragment)
    .commit()
```

## 命名规则总结

| 规则 | 说明 |
|------|------|
| 使用 snake_case | 全小写，下划线分隔 |
| 场景前缀 | 必须以场景前缀开头 |
| 方向后缀 | 使用 `_enter` / `_exit` / `_in` / `_out` |
| 语义清晰 | 名称应能表达动画效果 |
