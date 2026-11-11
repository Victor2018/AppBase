# 命名规范 (Naming Specification)

> 本文档定义 API 数据连接相关代码的命名规则。
> 适用于所有采用 Retrofit + MVVM 架构的 Android 项目。

## 模块命名

| 项目 | 规则 | 示例 |
|------|------|------|
| 模块名 | 大驼峰，业务含义 | `SubAccount`、`User`、`Order` |
| 动作名 | 小驼峰，动词开头 | `add`、`getList`、`getDetail`、`update`、`delete` |

## 文件命名

### 实体类

| 文件类型 | 命名规则 | 示例 |
|---------|---------|------|
| 请求实体类 | `{动作}{模块}Req.kt` | `AddSubAccountReq.kt` |
| 响应实体类 | `{模块}Data.kt` 或 `{模块}Bean.kt` | `SubAccountData.kt` |
| 列表项响应 | `{模块}Item.kt` 或复用 `{模块}Data.kt` | `SubAccountItem.kt` |
| 子参数类 | `{含义}Parm.kt` 或 `{含义}Param.kt` | `PageParm.kt` |

### 接口层

| 文件类型 | 命名规则 | 示例 |
|---------|---------|------|
| API路径常量 | `{模块}Api.kt` | `SubAccountApi.kt` |
| Retrofit接口 | `{模块}ApiService.kt` | `SubAccountApiService.kt` |
| DataSource接口 | `I{模块}DS.kt` | `ISubAccountDS.kt` |
| DataSource实现 | `{模块}DS.kt` | `SubAccountDS.kt` |

### ViewModel层

| 文件类型 | 命名规则 | 示例 |
|---------|---------|------|
| ViewModel | `{模块}VM.kt` | `SubAccountVM.kt` |
| ViewModel工厂 | `{模块}VMFactory.kt` | `SubAccountVMFactory.kt` |

### 依赖注入层

| 文件类型 | 命名规则 | 示例 |
|---------|---------|------|
| 注入工具类 | `InjectorUtils.kt` | `InjectorUtils.kt`（项目唯一） |
| 工厂提供方法 | `provide{模块}VMFactory` | `provideSubAccountVMFactory` |

## 类内部命名

### 常量命名（API路径）

```kotlin
object SubAccountApi {
    // 规则：大写下划线
    const val ADD = "user/front/subaccount/add"
    const val GET_LIST = "user/front/subaccount/list"
    const val GET_DETAIL = "user/front/subaccount/detail"
    const val UPDATE = "user/front/subaccount/update"
    const val DELETE = "user/front/subaccount/delete"
}
```

### 方法命名

| 层级 | 规则 | 示例 |
|------|------|------|
| ApiService | `{动作}{模块}` | `addSubAccount()`、`getSubAccountList()` |
| DataSource接口/实现 | `{动作}` | `add()`、`getList()`、`getDetail()` |
| ViewModel | `{动作}` | `add()`、`getList()` |
| 私有请求方法 | `{动作}Req` | `addReq()`、`getListReq()` |

### 属性命名

| 层级 | 规则 | 示例 |
|------|------|------|
| 请求实体类字段 | 小驼峰，与JSON一致 | `phone`、`realName`、`roleId` |
| 响应实体类字段 | 小驼峰，与JSON一致 | `id`、`accountName`、`createTime` |
| DataSource LiveData | `{动作}Data` | `addData`、`listData`、`detailData` |
| ViewModel LiveData | `{动作}Result` | `addResult`、`listResult` |

## 动作映射表

| 接口动作 | 常量名 | ApiService方法 | DS方法 | VM方法 | LiveData |
|---------|--------|---------------|--------|--------|----------|
| 添加 | `ADD` | `add{模块}` | `add` | `add` | `addResult` |
| 列表 | `GET_LIST` / `LIST` | `get{模块}List` | `getList` | `getList` | `listResult` |
| 详情 | `GET_DETAIL` / `DETAIL` | `get{模块}Detail` | `getDetail` | `getDetail` | `detailResult` |
| 更新 | `UPDATE` | `update{模块}` | `update` | `update` | `updateResult` |
| 删除 | `DELETE` | `delete{模块}` | `delete` | `delete` | `deleteResult` |
| 启用 | `ENABLE` | `enable{模块}` | `enable` | `enable` | `enableResult` |
| 禁用 | `DISABLE` | `disable{模块}` | `disable` | `disable` | `disableResult` |

## 完整示例

### 模块：子账号管理 (SubAccount)

```
文件清单：
├── entity/
│   ├── request/
│   │   ├── AddSubAccountReq.kt      # 添加请求
│   │   ├── UpdateSubAccountReq.kt   # 更新请求
│   │   └── SubAccountListReq.kt     # 列表查询请求（如有复杂参数）
│   └── bean/
│       └── SubAccountData.kt        # 子账号数据
├── api/
│   └── SubAccountApi.kt             # 路径常量
├── service/
│   └── SubAccountApiService.kt      # Retrofit接口
├── interfaces/
│   └── ISubAccountDS.kt             # 数据源接口
├── datasource/
│   └── SubAccountDS.kt              # 数据源实现
├── vm/
│   ├── SubAccountVM.kt              # ViewModel
│   └── factory/
│       └── SubAccountVMFactory.kt   # 工厂
└── utils/
    └── InjectorUtils.kt             # 注入入口（追加，追加provideSubAccountVMFactory）
```

## 注意事项

1. **模块已存在时**：追加新接口到已有文件，不要覆盖
2. **动作命名一致**：同一模块的不同动作应保持命名风格一致
3. **字段名与后端一致**：实体类字段名必须与接口文档的JSON字段名完全一致
4. **使用可空类型**：所有实体类字段使用 `?` 可空类型，默认值为 `null`
