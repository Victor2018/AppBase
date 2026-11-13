# 图片资源命名规范 (Image Resource Naming Specification)

> 本文档定义 Android 图片资源的命名规则和存放规范。

## 基本格式

```
前缀_描述.png
```

## 文件前缀对照表

| 前缀 | 用途 | 示例 |
|------|------|------|
| `ic_` | 小图标 | `ic_back_black.png`, `ic_arrow_right_gray.png` |
| `img_` | 大图片/背景图 | `img_splash.png`, `img_login_bg.png` |
| `ic_label_` | 标签/徽章图标 | `ic_label_bill.png`, `ic_label_vip.png` |
| `captcha_` | 验证码相关 | `captcha_bg_default.png`, `captcha_drag_btn.png` |
| `chk_` | 复选框状态 | `chk_checked.png`, `chk_normal.png` |
| `ic_rb_` | 单选框状态 | `ic_rb_checked.png`, `ic_rb_normal.png` |
| `icon_` | 应用图标 | `icon_loading.png` |

## 图标命名 (ic_)

### 导航图标

```
ic_back_black.png          # 黑色返回箭头
ic_back_white.png          # 白色返回箭头
ic_arrow_right_gray.png    # 灰色右箭头
ic_arrow_down.png          # 向下箭头
ic_arrow_up.png            # 向上箭头
ic_close.png               # 关闭图标
ic_clear.png               # 清除图标
ic_setting.png             # 设置图标
```

### 底部导航图标

```
# 选中状态
ic_home_checked.png        # 首页-选中
ic_delivery_checked.png    # 配送-选中
ic_map_checked.png         # 地图-选中
ic_me_checked.png          # 我的-选中

# 未选中状态
ic_home_normal.png         # 首页-未选中
ic_delivery_normal.png     # 配送-未选中
ic_map_normal.png          # 地图-未选中
ic_me_normal.png           # 我的-未选中
```

### 功能图标

```
ic_right.png               # 对勾
ic_tip.png                 # 提示
ic_star_filled.png         # 实心星星
ic_star_normal.png         # 空心星星
ic_save_img.png            # 保存图片
```

## 大图片命名 (img_)

```
img_splash.png             # 启动图
img_login_bg.png           # 登录背景
img_no_data.png            # 无数据占位图
img_no_data_search.png     # 搜索无结果占位图
img_home_logo.png          # 首页 Logo
img_id_card_front.png      # 身份证正面
img_id_card_back.png       # 身份证反面
img_placeholder.png        # 图片占位符
```

## 标签图标命名 (ic_label_)

```
ic_label_bill.png          # 账单标签
ic_label_new_gold.png      # 新金牌标签
ic_label_v.png             # V认证标签
ic_label_vip.png           # VIP标签
```

## 验证码相关命名 (captcha_)

```
captcha_bg_default.png     # 验证码背景-默认
captcha_drag_btn.png       # 拖动按钮
captcha_drag_btn_error.png # 拖动按钮-错误
captcha_drag_btn_success.png # 拖动按钮-成功
```

## 复选框/单选框状态命名

```
# 复选框
chk_checked.png            # 复选框-选中
chk_normal.png             # 复选框-未选中

# 单选框
ic_rb_checked.png          # 单选框-选中
ic_rb_normal.png           # 单选框-未选中
ic_rb_grey_normal.png      # 单选框-灰色未选中
ic_rb_solid_normal.png     # 单选框-实心未选中
ic_rb_right_checked.png    # 单选框-右侧选中
```

## 存放目录规范

### 目录结构

```
res/
├── mipmap-hdpi/           # 高密度屏幕（一般不存放）
├── mipmap-mdpi/           # 中密度屏幕（一般不存放）
├── mipmap-xhdpi/          # 超高密度屏幕（一般不存放）
├── mipmap-xxhdpi/         # 超超高密度屏幕（主要存放目录）
├── mipmap-xxxhdpi/        # 超超超高密度屏幕（可选）
└── drawable/              # XML 矢量图/形状
```

### 主要规则

| 规则 | 说明 |
|------|------|
| 主要目录 | `mipmap-xxhdpi` |
| 图片格式 | PNG |
| 导出倍率 | 3x（对应 xxhdpi） |
| 文件名 | 去掉 `@3x` 后缀 |

## 图片导出流程

1. **确定项目模块路径**
   - 模块项目：`{module_name}/src/main/res/mipmap-xxhdpi/`
   - App 项目：`app/src/main/res/mipmap-xxhdpi/`

2. **从 Figma 导出**
   - 选择 3x 倍率
   - 格式选择 PNG

3. **重命名文件**
   - 去掉 `@3x` 后缀
   - 确保符合命名规范

4. **压缩图片**
   - 使用 [TinyPNG](https://tinypng.com/) 压缩
   - 确保文件大小合理

5. **放入项目**
   - 存放到 `mipmap-xxhdpi` 目录

## 命名规则总结

| 规则 | 说明 |
|------|------|
| 使用 snake_case | 全小写，下划线分隔 |
| 前缀必须 | 必须以类型前缀开头 |
| 描述清晰 | 名称应能表达图片内容 |
| 状态后缀 | 有多种状态时添加 `_checked` / `_normal` 等后缀 |
| 颜色后缀 | 有多种颜色时添加 `_black` / `_white` / `_gray` 等后缀 |
| 无 @3x | 文件名不包含倍率后缀 |
