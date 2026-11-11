# 包命名规范 (Package Naming Specification)

> 本文档定义 Android 项目包的命名规则。

## 基本规则

- 全小写
- 使用点号 `.` 分隔
- 反向域名开头
- 不使用下划线或驼峰

## 项目包结构

### 顶层包名

| 模块 | 包名 |
|------|------|
| App 模块 | `com.ydj.driver` |
| 基础库 | `com.ydj.lib.common` |
| 数据层 | `com.ydj.lib.coremodel` |
| 网络层 | `com.ydj.lib.http` |
| 支付库 | `com.ydj.lib.pay` |
| 图片压缩 | `com.ydj.lib.luban` |
| 插件库 | `com.ydj.lib.qmf.ppplugin.android` |
| 首页模块 | `com.ydj.module.home` |
| 登录模块 | `com.ydj.module.login` |
| 配送模块 | `com.ydj.module.delivery` |
| 我的模块 | `com.ydj.module.me` |
| 消息模块 | `com.ydj.module.message` |
| 钱包模块 | `com.ydj.module.wallet` |
| 地图模块 | `com.ydj.module.map` |
| 构建插件 | `com.ydj.convention.buildlogic` |

## 模块内包结构

### 标准业务模块包结构

```
com.ydj.module.{name}/
├── view/
│   ├── activity/          # Activity 类
│   ├── fragment/          # Fragment 类
│   ├── adapter/           # Adapter 类
│   ├── holder/            # ViewHolder 类
│   └── dialog/            # Dialog 类
├── data/
│   ├── bean/              # 数据模型（响应）
│   └── request/           # 请求参数
└── util/                  # 模块工具类（可选）
```

### 基础库包结构 (lib_common)

```
com.ydj.lib.common/
├── app/                   # Application 类
├── base/                  # 基类（BaseActivity, BaseFragment 等）
├── data/                  # 公共数据类
├── interfaces/            # 回调接口
├── module/                # 功能模块封装
│   └── pda/               # PDA 扫码相关
├── receiver/              # 广播接收器
├── util/                  # 工具类
├── view/
│   ├── activity/          # 公共 Activity
│   ├── adapter/           # 公共 Adapter
│   ├── dialog/            # 对话框
│   ├── fragment/          # 公共 Fragment
│   ├── holder/            # ViewHolder
│   └── widget/            # 自定义控件
```

### 数据层包结构 (lib_coremodel)

```
com.ydj.lib.coremodel/
├── action/                # Action 类
├── data/
│   ├── local/             # 本地数据
│   │   ├── converter/     # Room 类型转换器
│   │   ├── dao/           # 数据访问对象
│   │   ├── entity/        # 数据库实体
│   │   ├── repository/    # 数据仓库
│   │   └── vm/            # 本地 ViewModel
│   └── remote/            # 远程数据
│       ├── api/           # API 路径常量
│       ├── datasource/    # DataSource 实现
│       ├── entity/        # 数据模型
│       │   ├── bean/      # 响应数据
│       │   └── request/   # 请求数据
│       ├── interfaces/    # DataSource 接口
│       ├── service/       # Retrofit ApiService
│       └── vm/            # ViewModel
│           └── factory/   # ViewModel Factory
├── flowevent/             # FlowEventBus 事件
└── util/                  # 工具类
```

### 网络层包结构 (lib_http)

```
com.ydj.lib.http/
├── adapter/               # Retrofit 适配器
├── data/                  # HTTP 数据类
├── interceptor/           # OkHttp 拦截器
├── util/                  # 工具类
├── verifier/              # 证书验证
└── vm/                    # ViewModel 基类
```

## 命名规则总结

| 规则 | 说明 |
|------|------|
| 全小写 | 包名全部使用小写字母 |
| 点号分隔 | 使用 `.` 分隔层级 |
| 反向域名 | 以 `com.ydj` 开头 |
| 语义清晰 | 包名应能表达其内容类型 |
| 单数形式 | 使用 `util` 而非 `utils`（包名） |
| 避免缩写 | 除非是广泛认可的缩写（如 `vm`, `ds`） |
