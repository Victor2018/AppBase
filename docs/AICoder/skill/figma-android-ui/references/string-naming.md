# 字符串命名规范 (String Naming Specification)

> 本文档定义 Android 字符串资源的命名规则。

## 基本格式

```
snake_case（下划线分隔小写）
```

## 命名规则

- 使用 **snake_case**（全小写，下划线分隔）
- 名称应清晰表达字符串的用途
- 可以使用模块前缀区分来源

## 应用名称

```xml
<string name="app_name">优大集掌柜</string>
```

## 导航标题

```xml
<string name="title_home">首页</string>
<string name="title_delivery">配送</string>
<string name="title_map">地图</string>
<string name="title_me">我的</string>
<string name="title_message">消息</string>
<string name="title_wallet">钱包</string>
<string name="title_setting">设置</string>
```

## 模块标识

```xml
<string name="home_module">首页模块</string>
<string name="me_module">我的模块</string>
<string name="login_module">登录模块</string>
<string name="delivery_module">配送模块</string>
```

## 通用提示文本

```xml
<string name="loading">加载中...</string>
<string name="network_error">网络错误</string>
<string name="connectivity">网络连接失败</string>
<string name="all_presented_for_you">已为您呈现全部内容</string>
```

## 权限提示

```xml
<string name="permission_camera_rationale_message">需要相机权限才能拍摄照片</string>
<string name="permission_rationale_message">需要存储权限才能保存图片</string>
<string name="permission_location_rationale_message">需要定位权限才能获取位置信息</string>
<string name="permission_storage_rationale_message">需要存储权限才能访问文件</string>
```

## 协议内容

```xml
<string name="login_privacy_policy_content">隐私政策内容...</string>
<string name="real_name_auth_agreement">实名认证协议内容...</string>
<string name="register_agreement">注册协议内容...</string>
<string name="sms_pay_agreement">短信支付协议内容...</string>
<string name="wallet_activation_agreement">钱包开通协议内容...</string>
<string name="pay_service_agreement">支付服务协议内容...</string>
```

## 分享通知

```xml
<string name="share_success">分享成功</string>
<string name="notify_share_cancel">分享已取消</string>
<string name="notify_share_auth_denied">分享授权被拒绝</string>
<string name="notify_share_failed">分享失败</string>
```

## 星期名称

```xml
<string name="week">星期</string>
<string name="sunday">周日</string>
<string name="monday">周一</string>
<string name="tuesday">周二</string>
<string name="wednesday">周三</string>
<string name="thursday">周四</string>
<string name="friday">周五</string>
<string name="saturday">周六</string>
```

## 工具类字符串

```xml
<string name="sticky">置顶</string>
<string name="img_transition_name">img_transition</string>
```

## 命名前缀建议

| 前缀 | 用途 | 示例 |
|------|------|------|
| `title_` | 页面标题 | `title_home` |
| `hint_` | 输入提示 | `hint_phone` |
| `btn_` | 按钮文本 | `btn_submit` |
| `msg_` | 消息文本 | `msg_success` |
| `tip_` | 提示文本 | `tip_error` |
| `label_` | 标签文本 | `label_status` |
| `dialog_` | 对话框 | `dialog_confirm` |
| `error_` | 错误信息 | `error_network` |
| `permission_` | 权限相关 | `permission_camera` |

## 命名规则总结

| 规则 | 说明 |
|------|------|
| 使用 snake_case | 全小写，下划线分隔 |
| 语义清晰 | 名称应能表达字符串用途 |
| 可使用前缀 | 使用 `title_`、`hint_` 等前缀分类 |
| 避免过长 | 名称简洁但清晰 |
