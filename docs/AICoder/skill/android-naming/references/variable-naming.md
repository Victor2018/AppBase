# 变量命名规范 (Variable Naming Specification)

> 本文档定义变量（成员变量、局部变量、常量）的命名规则。

## 成员变量命名

### 规则：m 前缀 + 类型缩写 + 业务名称（驼峰）

```kotlin
// Dialog 相关
private var mLoadingDialog: LoadingDialog? = null
private var mKickoutTipDialog: CommonTipDialog? = null
private var mTokenInvalidTipDialog: CommonTipDialog? = null

// Helper 相关
private var mWeChatLoginHelper: WeChatLoginHelper? = null
private var mWebSdkHelper: WebSdkHelper? = null
private var mTencentLocationHelper: TencentLocationHelper? = null
private var mPdaScanHelper: PdaScanHelper? = null

// Fragment 相关
private var mCodeLoginFragment: CodeLoginFragment? = null
private var mPasswordLoginFragment: PasswordLoginFragment? = null

// 数据相关
private var mDatas: MutableList<CommentInfo> = mutableListOf()
private var mHeaderCount: Int = 0
private var mBottomCount: Int = 0

// 回调/函数相关
private var mChildCommentClickFunction: ((CommentInfo) -> Unit)? = null
```

### 无 m 前缀的成员变量

部分成员变量不使用 m 前缀（通常是布尔标记或状态变量）：

```kotlin
private var loginPhoneData: LoginData? = null
private var currentFragment: Fragment? = null
private var statusBarTextColorBlack: Boolean = true

// 懒加载标记
private var isViewInitiated = false
private var isVisibleToUser = false
private var isDataInitiated = false
```

## ViewBinding 变量

### 标准模式：_binding / binding

```kotlin
// Fragment 中使用
private var _binding: FragmentLoginBinding? = null
private val binding get() = _binding!!

// 在 onDestroyView 中置空
override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}
```

## 局部变量命名

### 规则：camelCase，无前缀

```kotlin
val interval = 1000L
val backStackEntryCount = supportFragmentManager.backStackEntryCount
var status = false
val phone = binding.mEtPhone.text.toString()
val password = binding.mEtPassword.text.toString()
```

## TAG 命名

### 两种方式（项目中均有使用）

```kotlin
// 方式1：可变变量（项目常用）
var TAG = javaClass.simpleName

// 方式2：不可变常量
private val TAG = "LoginActivity"
```

## 常量命名

### 规则：SCREAMING_SNAKE_CASE

```kotlin
// 定义在 object 单例类中
object Constant {
    const val JD_BANK_KEY = "JD_BANK_KEY"
    const val APP_NAME = "应用名称"
    
    // 嵌套 object 分组
    object Action {
        const val COLLAPSE_INPUT_PANEL = 0x2001
        const val EXPAND_INPUT_PANEL = 0x2002
    }
    
    object RequestCode {
        const val NOTIFICATION = 0x1001
        const val CAMERA = 0x1002
        const val STORAGE = 0x1003
    }
}

// API 路径常量
object AuthApi {
    const val LOGIN = "user/front/sso-front/doLogin"
    const val LOGOUT = "user/front/sso-front/doLogout"
    const val USER_INFO = "user/front/user/getUserInfo"
}

// 状态常量（使用十六进制）
object LoadState {
    const val LOADING = 0x0001
    const val LOADING_COMPLETE = 0x0002
    const val LOADING_END = 0x0003
}

// 日期格式常量
object DateFormat {
    const val FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
    const val FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
}
```

## 属性委托变量

```kotlin
// SharedPreferences 委托
private var token by SharedPreferenceDelegates.string("")
private var isLoggedIn by SharedPreferenceDelegates.boolean(false)
private var userId by SharedPreferenceDelegates.int(0)

// Lazy 委托
private val apiService by lazy {
    ApiClient.getApiService(HomeApiService::class.java)
}

// ViewModel 委托
private val homeVM by viewModels<HomeVM> {
    InjectorUtils.provideHomeVMFactory(this)
}
```

## 命名对照速查表

| 变量类型 | 规则 | 示例 |
|----------|------|------|
| 成员变量（UI） | m + 类型缩写 + 名称 | `mTvTitle`, `mIvBack` |
| 成员变量（对象） | m + 名称 | `mLoadingDialog`, `mWeChatLoginHelper` |
| 成员变量（状态） | 无 m 前缀 | `isViewInitiated`, `currentFragment` |
| 局部变量 | camelCase | `interval`, `phone`, `status` |
| 常量 | SCREAMING_SNAKE | `LOGIN_URL`, `REQUEST_CODE` |
| ViewBinding | _binding / binding | `_binding`, `binding.root` |
| TAG | 大写 | `TAG` |
