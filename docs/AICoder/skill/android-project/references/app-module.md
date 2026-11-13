# app 壳模块

> 参考工程：`D:\GitProjects\ydj-driver-android\app`。app 只做打包入口与主框架，业务页面、网络链路一律不进 app。

## 1. app/build.gradle.kts

```kotlin
import com.<域>.support.AppBuildConfig
import com.<域>.support.TestApkDistribution.manualDistributeTestApk

plugins {
    alias(libs.plugins.ydj.android.application)
}

android {
    namespace = "com.<域>.<应用名>"
    defaultConfig {
        applicationId = "com.<域>.<应用名>"
        versionCode = AppBuildConfig.VERSION_CODE
        versionName = AppBuildConfig.VERSION_NAME
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    applicationVariants.all {
        val flavor = this.flavorName
        val buildType = this.buildType.name
        outputs.all {
            val outputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            outputImpl.outputFileName = "${AppBuildConfig.BUILD_NAME}_${flavor}_${buildType}" +
                    "_v${AppBuildConfig.VERSION_NAME}_${AppBuildConfig.VERSION_CODE}" +
                    "_${AppBuildConfig.getBuildDate()}_${AppBuildConfig.BUILD_NUMBER}.apk"
        }
    }
}

dependencies {
    if (AppBuildConfig.isModule) {
        implementation(project(":lib_common"))
        implementation(project(":lib_coremodel"))
    } else {
        implementation(project(":module_home"))
        implementation(project(":module_me"))
        // 其余业务模块
    }
}

tasks.register("apkUpload") {
    doLast {
        manualDistributeTestApk()
    }
}
```

- 只 apply `ydj.android.application`；SDK、Java、ViewBinding、flavors、签名、混淆、ARouter KSP 全由插件注入，不重复声明。
- `applicationId` 与 `namespace` 同值，发布后不可改。
- `apkUpload` 与自动分发依赖 `TestApkDistribution`；新工程没有分发能力时删掉 import、task 与 `application` 插件里的自动分发。

## 2. src/main/AndroidManifest.xml

骨架：

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <!-- 其余权限按实际功能裁剪 -->

    <queries package="${applicationId}">
        <intent>
            <action android:name="android.media.action.IMAGE_CAPTURE" />
        </intent>
    </queries>

    <application
        android:name="com.<域>.lib.common.app.App"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:networkSecurityConfig="@xml/network_config"
        android:requestLegacyExternalStorage="true"
        android:supportsRtl="true"
        android:theme="@style/AppTheme.NoActionBar"
        android:usesCleartextTraffic="true"
        tools:replace="android:allowBackup,android:theme">

        <activity
            android:name=".MainActivity"
            android:exported="true" />

        <activity
            android:name="com.<域>.lib.common.view.activity.SplashActivity"
            android:exported="true"
            android:screenOrientation="portrait"
            android:theme="@style/AppTheme.Splash">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name="com.<域>.lib.common.view.activity.WebActivity"
            android:launchMode="singleTop"
            android:screenOrientation="portrait" />

        <activity
            android:name="com.<域>.lib.common.view.activity.NoTitleWebActivity"
            android:screenOrientation="portrait" />

        <activity
            android:name="com.<域>.lib.common.view.activity.SchemaProxyActivity"
            android:exported="true"
            android:launchMode="singleTask"
            android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="<应用 scheme>" />
            </intent-filter>
        </activity>

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/apk_file_provider" />
        </provider>
    </application>

</manifest>
```

规则：

- `android:name` 指向 `lib_common` 的 `App`；启动页、Web、扫码、Schema 代理、崩溃页等公共组件也来自 `lib_common`，app 不重复实现。
- 清单里的第三方 `meta-data`（地图 key、推送 appId）属于项目凭据：新工程不复制参考工程的值，接入哪家就填哪家自己的。
- 权限只保留实际用到的；`tools:ignore` 按 lint 提示补，不整份照抄。
- `tools:replace` 覆盖与 `lib_common` 清单冲突的属性。

## 3. MainActivity.kt

```kotlin
package com.<域>.<应用名>

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.navigation.NavigationBarView
import com.<域>.<应用名>.databinding.ActivityMainBinding
import com.<域>.lib.common.base.ARouterPath
import com.<域>.lib.common.base.BaseActivity
import com.<域>.lib.common.view.adapter.ViewPagerAdapter

@Route(path = ARouterPath.MainAct)
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate),
    NavigationBarView.OnItemSelectedListener, ViewPager.OnPageChangeListener {

    var mViewPagerAdapter: ViewPagerAdapter? = null
    var fragmentList: MutableList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        fragmentList = getFragmentList()
        mViewPagerAdapter?.fragmetList = fragmentList
        binding.mVpHome.adapter = mViewPagerAdapter
        binding.mVpHome.offscreenPageLimit = fragmentList.size
        binding.mBottomNav.setOnItemSelectedListener(this)
        binding.mVpHome.addOnPageChangeListener(this)
    }

    fun getFragmentList(): ArrayList<Fragment> {
        val frags = ArrayList<Fragment>()
        frags.add(ARouter.getInstance().build(ARouterPath.HomeFgt).navigation() as Fragment)
        frags.add(ARouter.getInstance().build(ARouterPath.MeFgt).navigation() as Fragment)
        return frags
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        binding.mBottomNav.selectedItemId = when (position) {
            0 -> R.id.navigation_home
            else -> R.id.navigation_me
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> {
                binding.mVpHome.setCurrentItem(0, false)
                return true
            }
            R.id.navigation_me -> {
                binding.mVpHome.setCurrentItem(1, false)
                return true
            }
        }
        return false
    }
}
```

- Tab 用 ARouter 取 Fragment，不 import 业务模块类；Tab 数量、顺序、图标按产品定义。
- 登录态、版本更新、推送 token、网络状态提示等壳级逻辑放 `initData` / `subscribeUi` / `subscribeEvent`，参考工程 `MainActivity` 已给完整写法。
- `MainActivity` 需要 `lib_common`（`BaseActivity`、`ARouterPath`、`ViewPagerAdapter`）与 `lib_coremodel`，先建库再接壳。

## 4. src/main/res/layout/activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.<域>.lib.common.view.widget.NoTouchViewPager
        android:id="@+id/mVpHome"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:overScrollMode="never"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@+id/mBottomNav"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <com.<域>.lib.common.view.widget.NoLongToastBottomNavigationView
        android:id="@+id/mBottomNav"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="?android:attr/windowBackground"
        app:itemBackground="@color/white"
        app:itemTextColor="@color/bottom_navigation_title_selector"
        app:labelVisibilityMode="labeled"
        app:menu="@menu/bottom_nav_menu"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

- 控件 ID 按 `android-naming` 的 `view-id-naming.md`。
- 网络异常提示条、启动引导等按需追加，不默认带上。

## 5. 资源与配置

| 文件 | 内容 |
|---|---|
| `res/values/strings.xml` | `app_name`、`title_home`、`title_me` 等导航标题 |
| `res/values/colors.xml` | 只放 `black`、`white` 等基础色；业务色进 `lib_common` |
| `res/values/themes.xml` | `Base.Theme.<工程名>` + `Theme.<工程名>` |
| `res/menu/bottom_nav_menu.xml` | 底部导航项，id 用 `navigation_*` |
| `res/color/bottom_navigation_title_selector.xml` | Tab 文字选中态 |
| `res/drawable/bottom_navigation_*_selector.xml` | Tab 图标选中态 |
| `res/mipmap-*/ic_launcher.png` | 启动图标（各密度） |
| `res/xml/network_config.xml` | 网络安全配置 |
| `res/xml/apk_file_provider.xml` | 升级安装 FileProvider 路径 |
| `res/xml/backup_rules.xml` / `data_extraction_rules.xml` | 备份规则 |

图标资源用设计切图，不复制参考工程品牌的 `ic_*` 图片。

## 6. 其他文件

- `app/.gitignore`：至少包含 `/build`。
- `app/proguard-rules.pro`：保留四大组件、实体类、ARouter、Retrofit/OkHttp、Gson、Glide、WebView 及项目用到的第三方；新工程按实际依赖裁剪，不整份照抄。
- `src/test/.../ExampleUnitTest.kt`、`src/androidTest/.../ExampleInstrumentedTest.kt`：模板用例，`packageName` 断言改成新 `applicationId`。

## 7. 自检

- [ ] `namespace` 与 `applicationId` 一致且为反向域名
- [ ] app 内无业务页面、无网络链路代码
- [ ] 清单只有壳组件，第三方 key 是项目自己的
- [ ] `MainActivity` 只做 Tab 壳，Fragment 全部走 ARouter
- [ ] 资源无品牌残留（图标、图片、字符串）
- [ ] `proguard-rules.pro` 与依赖匹配，没有无关规则
- [ ] `./gradlew :app:assembleDevDebug` 通过
