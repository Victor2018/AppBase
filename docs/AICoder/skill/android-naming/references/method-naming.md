# 方法命名规范 (Method Naming Specification)

> 本文档定义 Kotlin 方法的命名规则和常用模式。

## 基本规则

- 使用 **camelCase**（小驼峰命名）
- 动词或动词短语开头
- 名称应清晰表达方法的功能

## 生命周期方法

```kotlin
// Activity 生命周期
override fun onCreate(savedInstanceState: Bundle?)
override fun onStart()
override fun onResume()
override fun onPause()
override fun onStop()
override fun onDestroy()

// Fragment 生命周期
override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
override fun onViewCreated(view: View, savedInstanceState: Bundle?)
override fun onDestroyView()

// 其他
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
```

## 初始化方法

| 方法名 | 用途 | 调用时机 |
|--------|------|----------|
| `initView()` | 初始化视图组件 | onCreate / onViewCreated |
| `initData()` | 初始化数据 | onCreate / onViewCreated |
| `initSuperView()` | 初始化父类视图 | initView 之前调用 |

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initSuperView()
    initView()
    initData()
    subscribeUi()
    subscribeEvent()
}

private fun initView() {
    binding.mRvList.adapter = mAdapter
    binding.mRvList.layoutManager = LinearLayoutManager(this)
}

private fun initData() {
    sendUserInfoRequest()
}

private fun initSuperView() {
    // 父类视图初始化
}
```

## 订阅方法

| 方法名 | 用途 |
|--------|------|
| `subscribeUi()` | 订阅 UI 事件（LiveData 观察） |
| `subscribeEvent()` | 订阅 FlowEventBus 事件 |
| `subscribeSuperEvent()` | 订阅父类事件 |

```kotlin
private fun subscribeUi() {
    homeVM.bannerData.observe(this, Observer {
        when (it) {
            is HttpResult.Success -> showBannerData(it.value)
            is HttpResult.Error -> ToastUtils.show(it.message)
        }
    })
    
    homeVM.userInfoData.observe(this, Observer {
        when (it) {
            is HttpResult.Success -> showUserInfo(it.value)
            is HttpResult.Error -> ToastUtils.show(it.message)
        }
    })
}

private fun subscribeEvent() {
    observeEvent<LoginSuccessEvent<LoginData>> { value ->
        finish()
    }
    
    observeEvent<KickoutEvent<String>>(minActiveState = Lifecycle.State.CREATED) { value ->
        showKickoutTipDlg(value.data)
    }
}
```

## 网络请求方法

### 发送请求：send + 动作 + Request

```kotlin
private fun sendLoginRequest() {
    val req = LoginReq(
        phone = binding.mEtPhone.text.toString(),
        password = binding.mEtPassword.text.toString(),
        code = mVerifyCode
    )
    authVM.login(req)
}

private fun sendLogoutRequest() {
    authVM.logout()
}

private fun sendDriverInfoRequest() {
    userVM.fetchDriverInfo()
}

private fun sendBannerRequest() {
    homeVM.fetchBanner()
}
```

### 获取数据：fetch + 实体名

```kotlin
// ViewModel 中
fun fetchUserInfo() {
    viewModelScope.launch {
        dataSource.getUserInfo()
    }
}

fun fetchDriverInfo() {
    viewModelScope.launch {
        dataSource.getDriverInfo()
    }
}

fun fetchBanner() {
    viewModelScope.launch {
        dataSource.getBanner()
    }
}
```

## 懒加载方法

```kotlin
// Fragment 懒加载
override fun lazyLoad() {
    if (!isViewInitiated || !isVisibleToUser || isDataInitiated) return
    isDataInitiated = true
    fetchData()
}

// 刷新 Fragment 数据
fun freshFragData() {
    isDataInitiated = false
    lazyLoad()
}
```

## 对话框显示方法

### 格式：show + 功能 + Dlg

```kotlin
private fun showLogoutDlg() {
    CommonConfirmDialog(this).apply {
        setTitle("提示")
        setContent("确认退出登录？")
        setOnOkClickListener { doLogout() }
        show()
    }
}

private fun showKickoutTipDlg(message: String?) {
    CommonTipDialog(this).apply {
        setTitle("提示")
        setContent(message ?: "您已被踢下线")
        show()
    }
}

private fun showLoadingDialog() {
    if (mLoadingDialog == null) {
        mLoadingDialog = LoadingDialog(this)
    }
    mLoadingDialog?.show()
}

private fun hideLoadingDialog() {
    mLoadingDialog?.dismiss()
}
```

## 静态启动方法

### Activity：intentStart

```kotlin
companion object {
    fun intentStart(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
    }
    
    fun intentStart(context: Context, orderId: String) {
        val intent = Intent(context, OrderDetailActivity::class.java)
        intent.putExtra("orderId", orderId)
        context.startActivity(intent)
    }
}
```

### Fragment：newInstance

```kotlin
companion object {
    fun newInstance(): HomeFragment {
        return HomeFragment().apply {
            arguments = Bundle()
        }
    }
    
    fun newInstance(id: Int): DetailFragment {
        return DetailFragment().apply {
            arguments = Bundle().apply {
                putInt("id", id)
            }
        }
    }
}
```

## 回调接口方法

### 格式：On + 动作 + Click/Process（PascalCase）

```kotlin
interface OnDialogClickListener {
    fun OnDialogOkClick()
    fun OnDialogCancelClick()
    fun OnDialogTitleClick()
}

interface OnWebMediaProcessListener {
    fun OnWebMediaProcess(type: Int, data: String?)
}

interface OnScrollListener {
    fun OnScrollToTop()
    fun OnScrollToBottom()
}
```

## 数据展示方法

```kotlin
private fun showBannerData(bannerList: List<BannerData>) {
    mAdapter.setNewData(bannerList)
}

private fun showUserInfo(userInfo: UserInfo) {
    binding.mTvNickName.text = userInfo.nickName
    binding.mTvPhone.text = userInfo.phone
    ImageLoaderUtils.loadImage(this, userInfo.avatar, binding.mIvAvatar)
}

private fun showEmptyView() {
    binding.mLlEmpty.visible()
    binding.mRvList.gone()
}

private fun hideEmptyView() {
    binding.mLlEmpty.gone()
    binding.mRvList.visible()
}
```

## 权限相关方法

```kotlin
@AfterPermissionGranted(REQUEST_CODE_STORAGE_PERMISSION)
private fun requestRWPermission() {
    if (hasStoragePermission()) {
        mWebSdkHelper?.selectFromAlbum(mH5GalleryParm)
    } else {
        showRWPermissionUseTipDlg()
    }
}

private fun hasStoragePermission(): Boolean {
    return EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
}

private fun showRWPermissionUseTipDlg() {
    PermissionUseTipDialog(this).apply {
        setContent("需要存储权限才能保存图片")
        setOnOkClickListener { requestRWPermission() }
        show()
    }
}
```

## 命名模式速查表

| 场景 | 命名模式 | 示例 |
|------|----------|------|
| 初始化视图 | `initView()` | - |
| 初始化数据 | `initData()` | - |
| 订阅 UI | `subscribeUi()` | - |
| 订阅事件 | `subscribeEvent()` | - |
| 发送请求 | `send{Action}Request()` | `sendLoginRequest()` |
| 获取数据 | `fetch{Entity}()` | `fetchUserInfo()` |
| 显示对话框 | `show{Feature}Dlg()` | `showLogoutDlg()` |
| 显示数据 | `show{Entity}Data()` | `showBannerData()` |
| 启动 Activity | `intentStart()` | - |
| 创建 Fragment | `newInstance()` | - |
| 懒加载 | `lazyLoad()` | - |
| 权限检查 | `has{Permission}()` | `hasStoragePermission()` |
| 权限申请 | `request{Permission}()` | `requestRWPermission()` |
