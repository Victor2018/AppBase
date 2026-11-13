# 类命名规范 (Class Naming Specification)

> 本文档定义 Kotlin/Java 类的命名规则。

## 基本规则

- 使用 **PascalCase**（大驼峰命名）
- 名词或名词短语
- 避免缩写，除非是广泛认可的缩写（如 `VM`、`DS`）

## 类类型后缀对照表

| 类型 | 后缀 | 示例 |
|------|------|------|
| Activity | `Activity` | `LoginActivity`, `HomeActivity`, `SettingActivity` |
| Fragment | `Fragment` | `HomeFragment`, `LoginFragment`, `MeFragment` |
| Adapter | `Adapter` | `MessageAdapter`, `CommentAdapter`, `EvaluateAdapter` |
| ViewHolder | `Holder` / `ContentHolder` | `CommentReplyContentHolder`, `BottomViewHolder` |
| ViewModel | `VM` | `HomeVM`, `AuthVM`, `UserVM`, `PayVM` |
| ViewModel Factory | `VMFactory` | `AuthVMFactory`, `UserVMFactory`, `HomeVMFactory` |
| DataSource | `DS` | `AuthDS`, `UserDS`, `HomeDS` |
| DataSource 接口 | `I` + 名称 + `DS` | `IAuthDS`, `IUserDS`, `IHomeDS` |
| API 路径常量 | 名称 + `Api` | `AuthApi`, `HomeApi`, `WalletApi` |
| Retrofit 接口 | 名称 + `ApiService` | `AuthApiService`, `HomeApiService` |
| 数据模型 | `Data` / `Info` / `Bean` | `LoginData`, `UserInfo`, `DriverData`, `CommentInfo` |
| 请求体 | `Req` | `LoginReq`, `BaseReq`, `SendCodeReq` |
| 参数类 | `Parm` | `WebConfigParm`, `H5LocationParm`, `PdaScanParm` |
| 事件类 | `Event` | `LoginSuccessEvent`, `KickoutEvent`, `PdaScanResultEvent` |
| 对话框 | `Dialog` | `CommonConfirmDialog`, `LoadingDialog`, `CommonTipDialog` |
| 工具类 | `Util` / `Utils` | `DateUtil`, `ToastUtils`, `SharedPreferencesUtils` |
| 辅助类 | `Helper` | `WebMediaHelper`, `TencentLocationHelper`, `PdaScanHelper` |
| 广播接收器 | `Receiver` / `BroadcastReceiver` | `PdaBroadcastReceiver`, `LocationReportReceiver` |
| 回调接口 | `On` + 动作 + `Listener` | `OnDialogOkCancelClickListener`, `OnWebViewStatusListener` |
| 常量类 | `Constant` / `Path` | `Constant`, `ARouterPath` |
| 基类（抽象） | `Base` + 类型 | `BaseActivity`, `BaseFragment`, `BaseRecycleAdapter` |
| 基类（抽象） | `Abs` + 类型 | `AbsDialog`, `AbsDialogFragment`, `AbsDS` |

## ARouter 路径常量命名

```kotlin
object ARouterPath {
    // Activity 路径：后缀用 Act
    const val LoginAct = "$LOGIN/module/LoginActivity"
    const val SettingAct = "$ME/module/SettingActivity"
    const val BankCardDetailAct = "$WALLET/module/BankCardDetailActivity"
    
    // Fragment 路径：后缀用 Fgt
    const val HomeFgt = "$HOME/module/HomeFragment"
    const val MeFgt = "$ME/module/MeFragment"
    const val DeliveryFgt = "$DELIVERY/module/DeliveryFragment"
}
```

## 数据类命名示例

```kotlin
// 响应数据 - 用 Data 后缀
data class LoginData(
    var token: String? = null,
    var extInfo: LoginExtData? = null
)

// 用户信息 - 用 Info 后缀
data class UserInfo(
    @SerializedName("user_id") val userId: String?,
    @SerializedName("nick_name") val nickName: String?,
    val avatar: String?
)

// 列表项 - 用 Bean/Info 后缀
data class CommentInfo(
    val id: String?,
    val content: String?,
    val userName: String?
)

// 请求体 - 用 Req 后缀
data class LoginReq(
    val phone: String?,
    val password: String?,
    val code: String?
)
```

## 事件类命名示例

```kotlin
// 登录成功事件
class LoginSuccessEvent<T>(val data: T? = null)

// 被踢下线事件
class KickoutEvent<T>(val data: T? = null)

// Token 失效事件
class TokenInvalidEvent(val message: String)

// PDA 扫码结果事件
class PdaScanResultEvent(val scanResult: String)
```

## 接口命名示例

```kotlin
// DataSource 接口：I + 名称 + DS
interface IAuthDS {
    val loginData: MutableLiveData<HttpResult<LoginData>>
    suspend fun login(body: LoginReq?)
}

// 回调接口：On + 动作 + Listener
interface OnDialogClickListener {
    fun OnDialogOkClick()
    fun OnDialogCancelClick()
}

interface OnWebViewStatusListener {
    fun OnPageStarted(url: String)
    fun OnPageFinished(url: String)
}
```
