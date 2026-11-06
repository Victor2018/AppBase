---
name: android_api_ds
description: 根据接口文档生成符合 Android 项目规范的网络请求代码。使用场景：当用户提供 Swagger/Knife4j 接口文档链接或截图，需要生成请求实体类、响应实体类、API接口、DataSource、ViewModel 等完整网络请求链路代码时触发。适用于所有采用 Retrofit + MVVM 架构的 Android 项目。
version: 2.0.0
---

# Android API 数据连接生成器

根据接口文档生成符合项目规范的网络请求代码。

**适用范围**：所有采用 Retrofit + MVVM 架构的 Android 项目

## 触发关键词

当用户输入包含以下关键词时，本 skill 会自动触发：

### 文档来源
- `接口文档`、`API文档`、`Swagger`、`Knife4j`
- `doc.html`、`接口地址`、`API地址`

### 动作意图
- `生成请求代码`、`创建接口连接`、`新增网络请求`
- `对接接口`、`接入API`、`请求数据`
- `生成实体类`、`生成Req`、`生成Bean`

### 技术术语
- `Retrofit接口`、`ApiService`、`DataSource`
- `ViewModel`、`VMFactory`、`LiveData`
- `请求参数`、`响应数据`、`网络请求`

### 示例触发语句
```
✓ 请根据接口文档生成网络请求代码
✓ 帮我对接这个API，生成实体类和ViewModel
✓ 根据Swagger文档生成SubAccount模块的请求代码
✓ 新增一个添加子账号的接口调用
✓ 生成这个接口的Req和Data类
```

## 前置准备

### 1. 获取接口信息

从用户提供的接口文档中提取以下信息：

| 信息项 | 说明 | 示例 |
|--------|------|------|
| 模块名称 | 业务模块中英文 | 子账号管理 (SubAccount) |
| 接口动作 | 操作类型 | 添加、列表、详情、更新、删除 |
| 接口路径 | API URL | user/front/subaccount/add |
| 请求方法 | HTTP方法 | POST / GET / PUT / DELETE |
| 请求参数 | 字段列表 | phone: String, realName: String |
| 响应数据 | 返回字段 | id: Long, name: String |

### 2. 确认项目配置

**首次使用时，需要向用户确认以下项目配置：**

```
请提供以下信息（或使用默认值）：

1. 项目根目录路径
   默认：当前工作目录

2. 网络模块位置（存放 api/service/datasource 等文件的模块）
   示例：lib_coremodel 或 app 或 data

3. 基础包名
   示例：com.example.app.data

4. 网络请求目录结构（如已有项目规范，请告知）
   默认结构：
   {模块}/data/remote/
   ├── api/           # API路径常量
   ├── service/       # Retrofit接口
   ├── interfaces/    # DataSource接口
   ├── datasource/    # DataSource实现
   ├── entity/
   │   ├── request/   # 请求实体类
   │   └── bean/      # 响应实体类
   └── vm/
       └── factory/   # ViewModel工厂
```

### 3. 检测项目现有规范

如果用户指定了已有项目路径，自动检测：
- 现有的实体类命名风格
- 现有的包结构
- 基类命名（如 BaseReq、BaseRes、AbsDS 等）
- 响应封装类型（如 NetworkResponse、HttpResult 等）

## 工作流程

### Step 1: 解析接口文档

提取接口关键信息：
- 请求路径和方法
- 请求参数（字段名、类型、必填、说明）
- 响应结构（data字段内容）

### Step 2: 确定命名

根据命名规范（见 `references/naming-spec.md`）确定：
- 模块名：大驼峰（如 `SubAccount`）
- 动作名：小驼峰（如 `add`、`getList`）
- 各类文件名称

### Step 3: 生成代码文件

按以下顺序生成（模板见 `references/code-templates.md`）：

1. **请求实体类** - `{动作}{模块}Req.kt`
2. **响应实体类** - `{模块}Data.kt`（如有数据返回）
3. **API路径常量** - 追加或新建 `{模块}Api.kt`
4. **Retrofit接口** - 追加或新建 `{模块}ApiService.kt`
5. **DataSource接口** - 追加或新建 `I{模块}DS.kt`
6. **DataSource实现** - 追加或新建 `{模块}DS.kt`
7. **ViewModel** - 追加或新建 `{模块}VM.kt`
8. **ViewModel工厂** - 新建 `{模块}VMFactory.kt`（如不存在）
9. **InjectorUtils** - 在已有 `InjectorUtils.kt` 中追加 `provide{模块}VMFactory()` 方法

### Step 4: 输出结果

- 列出所有生成的文件路径
- 显示完整代码
- 提示需要手动合并的内容（如文件已存在）

## 参考文档

**重要**：生成代码前，请先阅读以下规范文档：

| 文件 | 内容 |
|------|------|
| `references/naming-spec.md` | 命名规范（类名、方法名、常量名） |
| `references/code-templates.md` | 代码模板（各层级的完整模板） |

## 类型映射表

| JSON类型 | Kotlin类型 | 说明 |
|---------|-----------|------|
| string | String? | 字符串 |
| integer / int | Int? | 整数 |
| long | Long? | 长整数 |
| boolean / bool | Boolean? | 布尔值 |
| number | Double? | 浮点数 |
| array\<T\> | List\<T\>? | 数组 |
| object | 对应实体类? | 嵌套对象 |

## 特殊处理

### GET请求
使用 `@Query` 注解：
```kotlin
@GET(SubAccountApi.LIST)
suspend fun getSubAccountList(
    @Query("page") page: Int?,
    @Query("pageSize") pageSize: Int?
): NetworkResponse<BaseRes<List<SubAccountData>>, HttpError>
```

### 列表接口
返回类型为 `List<{模块}Data>`：
```kotlin
NetworkResponse<BaseRes<List<SubAccountData>>, HttpError>
```

### 无数据返回
使用 `Any?` 作为泛型：
```kotlin
NetworkResponse<BaseRes<Any?>, HttpError>
```

## 检查清单

生成文件后，验证以下项目：

### 文件结构检查
- [ ] 所有文件创建在正确的目录
- [ ] package 声明正确
- [ ] import 语句完整

### 命名规范检查
- [ ] 请求类：`{动作}{模块}Req`
- [ ] 响应类：`{模块}Data` 或 `{模块}Bean`
- [ ] API常量：大写下划线（如 `ADD`、`GET_LIST`）
- [ ] 方法名：小驼峰动词（如 `addSubAccount`）

### 代码风格检查
- [ ] 字段使用可空类型 `?`
- [ ] 默认值为 `null`
- [ ] 每个字段有注释说明
- [ ] 类注释包含说明和日期

### 架构一致性检查
- [ ] ApiService 使用 `suspend` 函数
- [ ] 返回类型使用项目统一的响应封装
- [ ] DataSource 继承项目基类（如有）
- [ ] ViewModel 使用 `viewModelScope.launch`
- [ ] LiveData 类型正确

## 适配不同项目的关键点

### 基类适配
不同项目可能有不同的基类命名：
- 请求基类：`BaseReq` / `BaseRequest` / `Request`
- 响应基类：`BaseRes` / `BaseResponse` / `Response`
- DataSource基类：`AbsDS` / `BaseDataSource` / `BaseRepository`

### 响应封装适配
不同项目可能使用不同的响应封装：
- `NetworkResponse<T, E>`
- `Result<T>`
- `ApiResponse<T>`
- 直接使用 `T`

### 错误处理适配
- `HttpError` / `ApiError` / `ErrorResponse`
- `HttpResult<T>` / `Result<T>` / `Resource<T>`

## 更新日志

### v2.0.0
- 移除项目特定依赖，改为通用架构
- 支持自动检测项目现有规范
- 添加多项目适配说明

### v1.0.0
- 初始版本
- 支持根据接口文档生成完整网络请求链路代码
