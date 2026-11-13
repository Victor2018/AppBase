# 业务模块模板（结构参考 module_message，页面参考 module_wallet）

> 适用 `module_<业务>`：有页面、交互和业务流程。编译配置由约定插件注入，`build.gradle.kts` 只保留 `namespace` 与模块私有依赖；页面骨架照 `module_wallet` 的 Activity + Fragment 模板。

## 1. 目录结构

```
module_<业务>/
├── .gitignore
├── build.gradle.kts
├── consumer-rules.pro
├── proguard-rules.pro
└── src/
    ├── main/
    │   ├── AndroidManifest.xml                # 集成模式清单
    │   ├── module/AndroidManifest.xml         # 组件模式清单
    │   ├── java/com/ydj/module/<业务>/
    │   │   ├── view/activity/XxxActivity.kt
    │   │   ├── view/fragment/XxxFragment.kt
    │   │   ├── view/adapter|dialog|holder/    # 按需
    │   │   ├── data/                          # 按需
    │   │   └── interfaces/                    # 按需
    │   └── res/
    │       ├── layout/activity_<业务>.xml     # module_wallet 模板
    │       ├── layout/fragment_<业务>.xml     # module_wallet 模板
    │       ├── values/strings.xml
    │       └── mipmap-xxhdpi/                 # 切图
    ├── test/java/com/ydj/module/<业务>/ExampleUnitTest.kt
    └── androidTest/java/com/ydj/module/<业务>/ExampleInstrumentedTest.kt
```

跨模块资源（固定放 lib_common）：

```
lib_common/src/main/res/drawable/module_<业务>_logo.xml
```

- 模块 logo 必须放 `lib_common/src/main/res/drawable/module_<业务>_logo.xml`，组件模式清单从 `lib_common` 解析该资源；放进本模块 `res/drawable` 会导致构建失败。
- 制作方式：直接复制 `lib_common/src/main/res/drawable/module_me_logo.xml` 作为模板，**只改 `android:fillColor`** 为一个与现有 logo 不重复的颜色，其余（`width/height/viewportWidth/viewportHeight`、`group` 的 `translateY/scaleX/scaleY`、`pathData`）原样保留，保存为 `module_<业务>_logo.xml`。
- 颜色取值避开已占用值：`#2196F3`、`#795548`、`#9c27b0`、`#AEB443`、`#C99C28`、`#E91E63`、`#FF9800`。

模板结构（`pathData` 必须原样照抄 `module_me_logo.xml`，不重画）：

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="2000.000000dp"
    android:height="2000.000000dp"
    android:viewportWidth="2000.000000"
    android:viewportHeight="2000.000000">

    <group
            android:translateY="2000.000000"
            android:scaleX="0.100000"
            android:scaleY="-0.100000">
        <path
            android:fillColor="#00C48C"
            android:pathData="M11280 16063 c-588 -45 ... 1002 50z" />
    </group>
</vector>
```

完整 `pathData` 以 `lib_common/src/main/res/drawable/module_me_logo.xml` 为准，复制文件后只改颜色最稳妥。

- 只建真正用到的包，不建空目录。
- 页面类进 `view/activity`、`view/fragment`；模块内常量与 UI 模型进 `data/`；回调进 `interfaces/`。
- 网络链路不放本模块：`Req/Data/Api/ApiService/IDS/DS/VM/VMFactory` 在 `lib_coremodel`，本模块通过 `lib_common` 的 api 传递依赖引用。
- 模块页面资源（layout/values/mipmap）放本模块；通用颜色、Drawable、公共控件与模块 logo 放 `lib_common`。

## 2. build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.ydj.android.module)
}

android {
    namespace = "com.ydj.module.<业务>"
}
```

- 没有私有依赖时不写 `dependencies`（`module_message` 即如此）。
- 需要其它模块能力时只加本模块特有的依赖，例如 `module_wallet`：`implementation(project(":lib_pay"))`。
- `lib_common` 已由约定插件 `api(project(":lib_common"))` 注入，不重复声明。
- 不在此重复 `compileSdk`、`minSdk`、`buildTypes`、`buildFeatures`、KSP、ARouter。

## 3. settings.gradle.kts

```kotlin
include(":module_<业务>")
```

- 追加到现有业务模块之后，写 `:目录名`，不写路径。
- 目录名必须与 `namespace` 末段一致。

## 4. 集成模式清单 `src/main/AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application android:theme="@style/AppTheme.NoActionBar">
        <activity
            android:name=".view.activity.XxxActivity"
            android:label="@string/xxx_module"
            android:launchMode="singleTask"
            android:screenOrientation="portrait" />
    </application>

</manifest>
```

- 不写 `package`（AGP 8 使用 `namespace`）。
- 只声明本模块组件；`name`、`icon`、`allowBackup` 由 app 清单统一提供。
- 普通页面不加 `exported`；只有被外部显式调起的组件才需要。

## 5. 组件模式清单 `src/main/module/AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.ydj.module.<业务>">

    <application
        android:name="com.ydj.lib.common.app.App"
        android:icon="@drawable/module_<业务>_logo"
        android:label="@string/xxx_module"
        android:theme="@style/AppTheme.NoActionBar"
        android:usesCleartextTraffic="true"
        android:allowBackup="true"
        tools:replace="android:allowBackup,android:label,android:icon">

        <activity
            android:name=".view.activity.XxxActivity"
            android:screenOrientation="portrait"
            android:launchMode="singleTask"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

- 该清单仅在 `AppBuildConfig.isModule = true` 时被 `com.ydj.convention.module` 选用。
- 主入口 Activity 必须加 LAUNCHER 和 `exported="true"`；其余组件不加。
- `android:name` 指向工程的 `App` 类，与集成模式保持一致。
- `android:icon="@drawable/module_<业务>_logo"` 必须能在 `lib_common/src/main/res/drawable/` 找到；放本模块会构建失败。用 `module_me_logo.xml` 复制后只改 `fillColor` 生成。
- `tools:replace` 必须覆盖与 app 清单冲突的属性。

## 6. 字符串

```xml
<resources xmlns:tools="http://schemas.android.com/tools">

    <string name="xxx_module">module_<业务></string>
</resources>
```

- 模块名文案固定为 `<业务>_module` / `module_<业务>`，供两份清单的 `android:label` 和 Toolbar 标题使用。
- 其余字符串命名按 `android-naming/references/string-naming.md`；新增前先搜索是否已有同文案资源。

## 7. 页面模板（参考 module_wallet）

业务模块默认交付「Activity + 一个 Fragment」。布局与类结构照 `module_wallet`，不另起一套。

### 7.1 Activity 布局 `activity_<业务>.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:ignore="ResourceName">

    <com.google.android.material.appbar.AppBarLayout
        android:id="@+id/appbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
        android:fitsSystemWindows="true">

        <com.google.android.material.appbar.CollapsingToolbarLayout
            android:id="@+id/mCtlTitle"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:layout_scrollFlags="scroll|exitUntilCollapsed"
            android:fitsSystemWindows="true"
            app:contentScrim="?attr/colorPrimary"
            app:expandedTitleMarginStart="@dimen/dp_50"
            app:expandedTitleMarginEnd="@dimen/dp_65"
            app:titleEnabled="false">

            <androidx.appcompat.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                android:background="@color/transparent"
                app:popupTheme="@style/ThemeOverlay.AppCompat.Light"
                app:layout_collapseMode="pin"
                app:title="@string/xxx_module" />
        </com.google.android.material.appbar.CollapsingToolbarLayout>

    </com.google.android.material.appbar.AppBarLayout>

    <FrameLayout
        android:id="@+id/fl_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"
        app:layout_behavior="@string/appbar_scrolling_view_behavior" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

- `toolbar` 与 `fl_container` 的 ID 固定，Activity 代码直接使用。
- 标题用模块字符串 `<业务>_module`。
- 布局里的尺寸、颜色沿用 `dimen` / `colors` 现有资源，不新增一次性资源。

### 7.2 Activity 类

```kotlin
@Route(path = ARouterPath.XxxAct)
class XxxActivity : BaseActivity<ActivityXxxBinding>(ActivityXxxBinding::inflate) {
    var currentFragment: Fragment? = null

    companion object {
        fun intentStart(activity: Activity) {
            val intent = Intent(activity, XxxActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        switchFragment(currentFragment, XxxFragment.newInstance())
    }

    fun switchFragment(fromFragment: Fragment?, toFragment: Fragment?) {
        if (currentFragment?.javaClass?.name == toFragment?.javaClass?.name) {
            return
        }

        currentFragment = toFragment

        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(
            com.ydj.lib.common.R.anim.anim_fragment_enter,
            com.ydj.lib.common.R.anim.anim_fragment_exit
        )
        if (toFragment?.isAdded()!!) {
            ft.show(toFragment)
        } else {
            ft.add(R.id.fl_container, toFragment)
        }
        fromFragment?.let {
            if (it.isAdded && !it.isHidden) {
                ft.hide(it)
            }
        }
        ft.commitAllowingStateLoss()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
```

- `onResume` 固定调用 `switchFragment` 加载默认 Fragment；新增页面时替换 Fragment 类型。
- `switchFragment` 的转场动画固定用 `lib_common` 的 `anim_fragment_enter` / `anim_fragment_exit`。
- 返回键在 `onOptionsItemSelected` 处理 `android.R.id.home`。
- `module_wallet` 的 Activity 未写 `intentStart`；本模板保留该入口，跨模块启动统一走它。
- 页面只做展示与转发，业务请求经 `XxxVM`；VM 与 Factory 从 `InjectorUtils` 获取。

### 7.3 Fragment 布局 `fragment_<业务>.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main_content"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:ignore="ResourceName">

    <com.google.android.material.appbar.AppBarLayout
        android:id="@+id/appbar_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
        android:background="@color/white"
        android:fitsSystemWindows="true"
        app:elevation="0dp">

        <com.google.android.material.appbar.CollapsingToolbarLayout
            android:id="@+id/mCtlWebTitle"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:fitsSystemWindows="true"
            app:contentScrim="@color/white"
            app:titleEnabled="false">

            <androidx.appcompat.widget.Toolbar
                style="@style/ToolbarStyle"
                app:title="@string/<业务>_module">

                <androidx.constraintlayout.widget.ConstraintLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content">

                    <com.ydj.lib.common.view.widget.MovingTextView
                        android:id="@+id/mTvTitle"
                        style="@style/ToolbarTitleStyle"
                        app:layout_constraintTop_toTopOf="parent"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        android:textColor="@color/color_1A1A1A"
                        android:ellipsize="marquee"
                        android:maxEms="12"
                        android:singleLine="true"
                        android:text="@string/<业务>_module" />

                </androidx.constraintlayout.widget.ConstraintLayout>

            </androidx.appcompat.widget.Toolbar>

        </com.google.android.material.appbar.CollapsingToolbarLayout>
    </com.google.android.material.appbar.AppBarLayout>

    <TextView
        android:id="@+id/mTvContent"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"
        tools:text="页面内容占位"
        app:layout_behavior="@string/appbar_scrolling_view_behavior" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

- 内容区固定用 `TextView`（ID `mTvContent`）作占位，替换为实际原生控件（RecyclerView、ConstraintLayout 等）时保持 ID 规范。
- 占位文案只写在 `tools:text`，不写 `android:text`，避免硬编码进入发布包。
- `mTvTitle` 是标题（跑马灯），`mCtlWebTitle` 是折叠标题容器，ID 由 `module_wallet` 固定，保持引用一致。
- `@style/ToolbarStyle`、`@style/ToolbarTitleStyle`、`@color/white`、`@color/color_1A1A1A` 均来自 `lib_common`，不重复定义。
- 标题初始值用模块字符串 `<业务>_module`；`module_wallet` 里写死为「消息」，新模块不要照抄文案。
- 需要 H5 页面时再参考 `module_wallet`：把 `TextView` 换成 `ProgressWebView`（ID `mPWeb`），Fragment 基类改回 `BaseWebFragment`。

### 7.4 Fragment 类

```kotlin
@Route(path = ARouterPath.XxxFgt)
class XxxFragment : BaseFragment<FragmentXxxBinding>(FragmentXxxBinding::inflate), OnClickListener {

    companion object {
        fun newInstance(): XxxFragment {
            return newInstance(0)
        }

        fun newInstance(id: Int): XxxFragment {
            val fragment = XxxFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        subscribeUi()
    }

    fun initData() {
    }

    fun subscribeUi() {
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }
}
```

- 原生页面继承 `BaseFragment`，必须实现 `handleBackEvent` / `freshFragData`，否则无法编译。
- `handleBackEvent` 返回 `false` 表示不拦截，交给宿主 Activity 关闭；有子页面栈时再返回 `true` 自行处理。
- `freshFragData` 在页面首次可见时被调用，在这里发起首屏数据加载；`initData` 放与可见性无关的初始化。
- `initView` 固定先 `subscribeUi()` 注册 UI 事件，再按需 `subscribeEvent()` 订阅 EventBus。
- `onClick` 用于 Fragment 内原生控件点击，无按钮时保留空 `when`。
- `module_wallet` 的 Fragment 未加 `@Route`；本模板保留 `@Route` + `XxxFgt` 常量，便于跨模块直接打开，模块内自用时删掉即可。

## 8. 对外入口（ARouter）

路由常量追加到 `lib_common/src/main/java/com/ydj/lib/common/base/ARouterPath.kt`：

```kotlin
private const val XXX = "/<业务>"

/** Xxx Activity */
const val XxxAct = "$XXX/module/XxxActivity"

/** Xxx Fragment */
const val XxxFgt = "$XXX/module/XxxFragment"
```

规则：

- Activity 常量后缀 `Act`，Fragment 后缀 `Fgt`。
- 路径格式 `"/<业务>/module/<类名>"`，段名与该模块 `ARouterPath` 私有常量一致。
- Activity、Fragment 都要加 `@Route`；Activity 的 `companion object` 提供 `intentStart`。
- 跨模块调用只通过 `ARouterPath` + `NavigationUtils`，禁止直接 import 对方的 Activity/Fragment。
- 模块内回调放 `interfaces/On<动作>Listener.kt`，方法名 `On<动作>Complete`，命名见 `class-naming.md`。

## 9. 资源前缀

- `com.ydj.convention.module` 自动设置 `resourcePrefix = "<目录名去掉 module_>_"`，例如 `module_message` → `message_`。
- 该前缀用于 lint 检查跨模块资源冲突，不是重命名工具。
- 现有模块布局仍使用 `activity_` / `fragment_` / `dlg_` / `rv_`，新模块保持一致，不为通过前缀检查改名。
- 模块专属字符串、图片、颜色建议带业务前缀（如 `message_title`），新增前先全局搜索同名资源。
- 模块 logo 不参与前缀规则：固定命名 `module_<业务>_logo`，固定放在 `lib_common/src/main/res/drawable/`，供组件模式清单引用。

## 10. 测试文件

可选，按 Android Studio 模板生成：

- `ExampleUnitTest.kt`：包名与 `namespace` 一致，只放模板用例。
- `ExampleInstrumentedTest.kt`：断言 `appContext.packageName == "<namespace>.test"`。

不写业务断言，不引入测试框架之外的新依赖。

## 11. 自检

- [ ] 目录、`include`、`namespace`、源码目录一致
- [ ] `build.gradle.kts` 无重复编译配置
- [ ] 双清单齐全，组件模式可独立启动
- [ ] `lib_common/src/main/res/drawable/module_<业务>_logo.xml` 已创建，组件清单 `android:icon` 可解析
- [ ] Activity/Fragment 布局与类结构按 module_wallet：`toolbar` + `fl_container` + `switchFragment`
- [ ] Fragment 布局内容区为 `TextView(mTvContent)`，标题栏 `mTvTitle` 保留；占位文案只在 `tools:text`
- [ ] Fragment 继承 `BaseFragment`，实现 `handleBackEvent` / `freshFragData` / `onClick`，`initView` 调 `subscribeUi()`
- [ ] Fragment 提供 `newInstance` 与 `@Route`（模块内自用可去掉 `@Route`）
- [ ] 路由常量 `XxxAct` / `XxxFgt` 已入 `ARouterPath`，跨模块无直接类依赖
- [ ] 网络链路在 `lib_coremodel`，模块内无 Req/Api/DS/VM 文件
- [ ] 资源命名合规且无重复
- [ ] `./gradlew :module_<业务>:assembleDebug` 与 `./gradlew :app:assembleDebug` 均通过
