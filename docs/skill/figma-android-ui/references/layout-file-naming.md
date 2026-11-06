# 布局文件命名规范 (Layout File Naming Specification)

> 本文档定义 Android XML 布局文件的命名规则。

## 基本规则

- 使用 **snake_case**（下划线分隔小写字母）
- 必须包含类型前缀
- 文件名应清晰表达布局的用途

## 文件类型前缀对照表

| 类型 | 前缀 | 格式 | 示例 |
|------|------|------|------|
| Activity 布局 | `activity_` | `activity_{功能}.xml` | `activity_login.xml` |
| Fragment 布局 | `fragment_` | `fragment_{功能}.xml` | `fragment_home.xml` |
| Dialog 布局 | `dlg_` | `dlg_{功能}.xml` | `dlg_common_confirm.xml` |
| RecyclerView 列表项 | `rv_` | `rv_{功能}_cell.xml` | `rv_message_cell.xml` |
| 头部子布局 | `act_` + `_header` | `act_{功能}_header.xml` | `act_pay_way_header.xml` |
| 标签/段落单元格 | `label_` | `label_{功能}_cell.xml` | `label_hot_comment_cell.xml` |
| Toast 布局 | `toast_` | `toast_{类型}.xml` | `toast_layout.xml` |
| 验证码视图 | `captcha_` | `captcha_{类型}.xml` | `captcha_drag_view.xml` |
| 启动图相关 | `splash_` | `splash_{类型}.xml` | `splash_image_cell.xml` |
| 通用视图 | `view_` 或 `*_view` | `view_{类型}.xml` | `view_image_cell.xml` |
| 播放视图 | `play_` | `play_{类型}.xml` | `play_view.xml` |
| WebView 进度条 | `web_` | `web_{类型}.xml` | `web_progress_view.xml` |
| 列表底部 | `recyclerview_` | `recyclerview_{位置}.xml` | `recyclerview_foot.xml` |

## Activity 布局命名示例

```
activity_main.xml              # 主页面
activity_login.xml             # 登录页面
activity_setting.xml           # 设置页面
activity_bank_card_detail.xml  # 银行卡详情
activity_corporate_transfer.xml # 企业转账
activity_pay_way.xml           # 支付方式
activity_activate_sms_pay.xml  # 短信支付激活
activity_splash.xml            # 启动页
activity_web.xml               # WebView 页面
activity_qr_scan.xml           # 二维码扫描
activity_image_viewer.xml      # 图片查看器
```

## Fragment 布局命名示例

```
fragment_home.xml              # 首页
fragment_delivery.xml          # 配送
fragment_me.xml                # 我的
fragment_login.xml             # 登录
fragment_code_login.xml        # 验证码登录
fragment_password_login.xml    # 密码登录
fragment_message.xml           # 消息
fragment_wallet.xml            # 钱包
fragment_map.xml               # 地图
fragment_setting.xml           # 设置
```

## Dialog 布局命名示例

```
dlg_common_confirm.xml         # 通用确认对话框
dlg_common_tip.xml             # 通用提示对话框
dlg_loading.xml                # 加载对话框
dlg_role_select.xml            # 角色选择对话框
dlg_service_evaluate.xml       # 服务评价对话框
dlg_app_update.xml             # App 更新对话框
dlg_gallery_select.xml         # 相册选择对话框
dlg_captcha.xml                # 验证码对话框
dlg_privacy.xml                # 隐私协议对话框
```

## RecyclerView Item 布局命名示例

```
rv_comment_cell.xml            # 评论列表项
rv_evaluate_cell.xml           # 评价列表项
rv_message_cell.xml            # 消息列表项
rv_bank_card_cell.xml          # 银行卡列表项
rv_pay_way_cell.xml            # 支付方式列表项
rv_feedback_image_cell.xml     # 反馈图片列表项
rv_order_cell.xml              # 订单列表项
rv_delivery_cell.xml           # 配送列表项
```

## 头部子布局命名示例

```
act_password_login_header.xml      # 密码登录头部
act_corporate_transfer_header.xml  # 企业转账头部
act_pay_way_header.xml             # 支付方式头部
act_order_detail_header.xml        # 订单详情头部
```

## 命名规则总结

| 规则 | 说明 |
|------|------|
| 前缀必须 | 所有布局文件必须包含类型前缀 |
| 下划线分隔 | 单词之间用下划线连接 |
| 全小写 | 文件名全部使用小写字母 |
| 功能清晰 | 名称应能表达布局的用途 |
| cell 后缀 | 列表项布局使用 `_cell` 后缀 |
| header 后缀 | 头部子布局使用 `_header` 后缀 |
