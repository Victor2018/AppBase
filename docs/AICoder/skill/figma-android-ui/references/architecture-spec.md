# 架构规范 (Architecture Specification)

> 本文档定义项目的架构模式和分层规范。

## 架构模式：MVVM

```
View (Activity/Fragment)
    ↓ observe LiveData
ViewModel
    ↓ call
DataSource (DS)
    ↓ request
ApiService (Retrofit)
```

## 分层职责

| 层级 | 职责 | 所在模块 |
|------|------|----------|
| View | UI 展示、用户交互 | module_* |
| ViewModel | 业务逻辑、数据转换 | lib_coremodel |
| DataSource | 数据获取、网络请求 | lib_coremodel |
| ApiService | Retrofit 接口定义 | lib_coremodel |
| 网络层 | HTTP 客户端封装 | lib_http |
| 基础层 | Base 类、工具类 | lib_common |

## BaseActivity 使用

```kotlin
class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    override fun initView() {
        // 初始化视图
    }

    override fun initData() {
        // 初始化数据
    }

    override fun subscribeUi() {
        // 订阅数据变化
    }
}
```

## BaseFragment 使用

```kotlin
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    override fun initView() {
        // 初始化视图
    }

    override fun initData() {
        // 初始化数据
    }

    override fun lazyLoad() {
        // 懒加载数据
    }
}
```

## ViewModel + DataSource 模式

### ViewModel

```kotlin
class AuthVM(private val dataSource: IAuthDS) : ViewModel() {

    val loginData: MutableLiveData<HttpResult<LoginData>> = MutableLiveData()
    val logoutData: MutableLiveData<HttpResult<Any>> = MutableLiveData()

    fun login(body: LoginReq?) {
        viewModelScope.launch {
            dataSource.login(body)
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataSource.logout()
        }
    }
}
```

### DataSource 接口

```kotlin
interface IAuthDS {
    val loginData: MutableLiveData<HttpResult<LoginData>>
    suspend fun login(body: LoginReq?)
}
```

### DataSource 实现

```kotlin
class AuthDS : AbsDS(), IAuthDS {

    override val loginData: MutableLiveData<HttpResult<LoginData>> = MutableLiveData()

    override suspend fun login(body: LoginReq?) {
        withContext(Dispatchers.Main) {
            loginData.value = loginReq(body)
        }
    }

    private suspend fun loginReq(body: LoginReq?): HttpResult<LoginData> {
        return withContext(Dispatchers.IO) {
            handleResponse(
                ApiClient.getApiService(AuthApiService::class.java).login(body)
            )
        }
    }
}
```

### ViewModel Factory

```kotlin
class AuthVMFactory(owner: SavedStateRegistryOwner) : BaseVMFactory(owner) {
    override fun getVM(): ViewModel {
        return AuthVM(AuthDS())
    }
}
```

### InjectorUtils

```kotlin
object InjectorUtils {
    fun provideAuthVMFactory(owner: SavedStateRegistryOwner): AuthVMFactory {
        return AuthVMFactory(owner)
    }
}
```

## 网络请求规范

### ApiService 定义

```kotlin
interface AuthApiService {

    @POST(AuthApi.LOGIN)
    suspend fun login(@Body body: LoginReq?): NetworkResponse<BaseRes<LoginData>, HttpError>

    @POST(AuthApi.LOGOUT)
    suspend fun logout(): NetworkResponse<BaseRes<Any>, HttpError>

    @GET(AuthApi.USER_INFO)
    suspend fun getUserInfo(@Query("userId") userId: String?): NetworkResponse<BaseRes<UserInfo>, HttpError>
}
```

### API 路径常量

```kotlin
object AuthApi {
    const val LOGIN = "user/front/sso-front/doLogin"
    const val LOGOUT = "user/front/sso-front/doLogout"
    const val USER_INFO = "user/front/user/getUserInfo"
}
```

### 请求/响应模型

```kotlin
// 请求体
data class LoginReq(
    val phone: String?,
    val password: String?,
    val code: String?
)

// 响应包装
open class BaseRes<T> {
    var data: T? = null
    var code: String? = null
    var message: String? = null
    var success: Boolean? = null
}

// 业务数据
data class LoginData(
    var token: String? = null,
    var extInfo: LoginExtData? = null
)

// 错误响应
data class HttpError(
    var statusCode: String,
    var statusMessage: String
)
```

### Activity 中调用

```kotlin
class LoginActivity : BaseActivity<ActivityLoginBinding>(...) {

    private val authVM by viewModels<AuthVM> {
        InjectorUtils.provideAuthVMFactory(this)
    }

    override fun subscribeUi() {
        authVM.loginData.observe(this, Observer {
            when (it) {
                is HttpResult.Success -> {
                    App.instance.saveLoginData(it.value)
                    HomeActivity.intentStart(this)
                    finish()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    private fun sendLoginRequest() {
        val req = LoginReq(
            phone = binding.mEtPhone.text.toString(),
            password = binding.mEtPassword.text.toString(),
            code = mVerifyCode
        )
        authVM.login(req)
    }
}
```

## ARouter 路由

### Activity 注册路由

```kotlin
@Route(path = ARouterPath.LoginAct)
class LoginActivity : BaseActivity<ActivityLoginBinding>(...) {
    // ...
}
```

### 路由跳转

```kotlin
ARouter.getInstance()
    .build(ARouterPath.LoginAct)
    .withString("phone", phone)
    .navigation(context)
```

### 接收参数

```kotlin
@Autowired
@JvmField
var phone: String? = null
```

## FlowEventBus 事件

### 定义事件

```kotlin
class LoginSuccessEvent<T>(val data: T? = null)
class KickoutEvent<T>(val data: T? = null)
class TokenInvalidEvent(val message: String)
```

### 发送事件

```kotlin
postEvent(LoginSuccessEvent(loginData))
postEvent(TokenInvalidEvent("登录已过期"))
```

### 监听事件

```kotlin
observeEvent<LoginSuccessEvent<LoginData>> { value ->
    finish()
}

observeEvent<KickoutEvent<String>>(minActiveState = Lifecycle.State.CREATED) { value ->
    showKickoutTipDlg(value.data)
}
```

## 错误处理

### AbsDS 统一处理

```kotlin
abstract class AbsDS {

    protected fun <T : Any> handleResponse(
        response: NetworkResponse<BaseRes<T>, HttpError>
    ): HttpResult<T> {
        return when (response) {
            is NetworkResponse.Success -> {
                val baseRes = response.body
                when (baseRes.code) {
                    "0" -> HttpResult.Success(baseRes.data!!)
                    "401" -> {
                        postEvent(TokenInvalidEvent(baseRes.message ?: "登录已过期"))
                        HttpResult.Error(baseRes.code, baseRes.message)
                    }
                    "409" -> {
                        postEvent(KickoutEvent(baseRes.message ?: "其他设备登录"))
                        HttpResult.Error(baseRes.code, baseRes.message)
                    }
                    else -> HttpResult.Error(baseRes.code, baseRes.message)
                }
            }
            is NetworkResponse.ApiError -> {
                HttpResult.Error(response.code.toString(), response.body?.statusMessage)
            }
            is NetworkResponse.NetworkError -> {
                HttpResult.Error(HttpErrorType.NETWORK, "网络异常")
            }
            is NetworkResponse.UnknownError -> {
                HttpResult.Error(HttpErrorType.UNKNOWN, "未知错误")
            }
            is NetworkResponse.ServerError -> {
                HttpResult.Error(response.code.toString(), response.error)
            }
        }
    }
}
```

## 依赖注入

项目使用手动依赖注入（无 Hilt/Dagger/Koin）：

```
InjectorUtils (单例)
    ↓ 提供
*VMFactory (继承 BaseVMFactory)
    ↓ 创建
ViewModel(DataSource)
    ↓ 获取
Activity/Fragment (by viewModels 委托)
```

## 组件化架构

### 双模式支持

| 模式 | isModule | 说明 |
|------|----------|------|
| 集成模式 | false | 业务模块作为 library 被 app 依赖 |
| 独立模式 | true | 业务模块作为 application 独立运行 |

### 模块依赖关系

```
app
├── lib_common
│   ├── lib_coremodel
│   │   └── lib_http
│   └── lib_luban
├── module_home → lib_common
├── module_delivery → lib_common
├── module_me → lib_common
├── module_login → lib_common
├── module_message → lib_common
├── module_wallet → lib_common, lib_pay
└── module_map → lib_common
```

## 架构速查表

| 项目 | 规范 |
|------|------|
| 架构模式 | MVVM |
| 异步方式 | Kotlin 协程 |
| 网络请求 | Retrofit + OkHttp |
| 路由 | ARouter |
| 事件总线 | FlowEventBus |
| 依赖注入 | 手动（InjectorUtils + VMFactory） |
| 数据绑定 | ViewBinding |
| 本地存储 | Room + SharedPreferences |
| 图片加载 | Glide |
