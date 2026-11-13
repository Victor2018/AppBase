# Android API 数据连接生成器

根据接口文档自动生成符合项目规范的网络请求代码。

**适用范围**：所有采用 Retrofit + MVVM 架构的 Android 项目

## 功能特性

- 解析 Swagger/Knife4j 接口文档
- 生成完整的网络请求链路代码（8种文件类型）
- 严格遵循项目命名规范和代码风格
- 支持增量更新（已存在的文件会追加而非覆盖）

## 生成的文件类型

| 文件类型 | 命名规则 | 用途 |
|---------|---------|------|
| 请求实体类 | `{动作}{模块}Req.kt` | 封装请求参数 |
| 响应实体类 | `{模块}Data.kt` | 封装响应数据 |
| API路径常量 | `{模块}Api.kt` | 定义接口URL |
| Retrofit接口 | `{模块}ApiService.kt` | HTTP接口定义 |
| DataSource接口 | `I{模块}DS.kt` | 数据源抽象 |
| DataSource实现 | `{模块}DS.kt` | 网络调用实现 |
| ViewModel | `{模块}VM.kt` | 业务逻辑层 |
| ViewModel工厂 | `{模块}VMFactory.kt` | VM创建工厂 |
| InjectorUtils | `InjectorUtils.kt`（追加已有） | 依赖注入入口，暴露VM工厂给上层 |

## 使用方法

1. 提供接口文档链接或截图
2. 说明模块名称和业务动作
3. Skill 自动生成所有代码文件

### 示例

```
请根据以下接口生成代码：
- 模块：子账号管理 (SubAccount)
- 接口：添加子账号
- 路径：user/front/subaccount/add
- 方法：POST
- 请求参数：
  - phone: String, 必填, 手机号
  - realName: String, 必填, 真实姓名
  - roleId: Int, 必填, 角色ID
```

## 项目结构

```
D:\android_api_ds\
├── SKILL.md                      # Skill 主文件
├── README.md                     # 本文件
└── references\
    └── code-templates.md         # 代码模板
```

命名规范统一维护在 `android-naming` skill 的 `references/network-naming.md`。

## 依赖的项目结构（默认，可自定义）

```
{项目根目录}\
└── {网络模块}\
    └── src\main\java\{包名}\
        └── data\remote\
            ├── api\              # API路径常量
            ├── service\          # Retrofit接口
            ├── interfaces\       # DataSource接口
            ├── datasource\       # DataSource实现
            ├── entity\
            │   ├── request\      # 请求实体类
            │   └── bean\         # 响应实体类
            └── vm\
                └── factory\      # ViewModel工厂
```

## 技术栈

- **网络库**：Retrofit2 + OkHttp3
- **序列化**：Gson
- **异步**：Kotlin Coroutines
- **架构**：MVVM + LiveData

## 版本

v2.0.1
