# 控件 ID 命名规范 (View ID Naming Specification)

> 本文档定义 Android XML 布局中控件 ID 的命名规则。

## 基本格式

```
m + 类型缩写 + 业务名称（驼峰）
```

## 类型缩写对照表

| 控件类型 | 前缀 | 示例 |
|----------|------|------|
| TextView | `mTv` | `mTvTitle`, `mTvContent`, `mTvNickName`, `mTvTime` |
| ImageView | `mIv` | `mIvBack`, `mIvClose`, `mIvClear`, `mIvAvatar` |
| Button | `mBtn` | `mBtnLogin`, `mBtnSubmit`, `mBtnSendSms`, `mBtnActivate` |
| EditText | `mEt` | `mEtPhone`, `mEtPassword`, `mEtEvaluate` |
| CheckBox | `mChk` | `mChkCheck`, `mChkAgree`, `mChkCheckAll` |
| RadioButton | `mRb` | `mRbScore`, `mRbMale`, `mRbFemale` |
| RecyclerView | `mRv` | `mRvList`, `mRvImage`, `mRvPayWay`, `mRvEvaluate` |
| ViewPager | `mVp` | `mVpHome`, `mVpDetail`, `mVpBanner` |
| ProgressBar | `mPb` | `mPbLoading`, `mPbDownloadProgress` |
| LinearLayout | `mLl` | `mLlContent`, `mLlLoadingContent`, `mLlEmpty` |
| FrameLayout | `mFl` | `mFlContainer`, `mFlContent` |
| ConstraintLayout | `mCl` | `mClHeader`, `mClAgreement`, `mClFeedbackImageCell` |
| RelativeLayout | `mRl` | `mRlContent`, `mRlItem` |
| ScrollView | `mSv` | `mSvContent`, `mSvMain` |
| NestedScrollView | `mNsv` | `mNsvUpdateContent`, `mNsvDetail` |
| WebView | `mWv` | `mWvContent`, `mWvDetail` |
| ProgressWebView | `mPWeb` | `mPWeb` |
| Toolbar | `mToolbar` | `mToolbar` |
| CollapsingToolbarLayout | `mCtl` | `mCtlTitle`, `mCtlWebTitle` |
| AppBarLayout | `mAppBar` | `mAppBar` |
| TabLayout | `mTab` | `mTabDetail`, `mTabCategory` |
| BottomNavigationView | `mBottomNav` | `mBottomNav` |
| View（分割线等） | `mView` | `mViewDivider`, `mViewPayPassword` |
| RatingBar | `mRb` | `mRbScore`, `mRbComment` |
| NumberPickerView | `mNumberPicker` | `mNumberPicker` |
| SwipeRefreshLayout | `mSrl` | `mSrlRefresh` |

## XML 使用示例

```xml
<!-- TextView -->
<TextView
    android:id="@+id/mTvTitle"
    ... />

<TextView
    android:id="@+id/mTvNickName"
    ... />

<!-- ImageView -->
<ImageView
    android:id="@+id/mIvBack"
    ... />

<ImageView
    android:id="@+id/mIvAvatar"
    ... />

<!-- Button -->
<Button
    android:id="@+id/mBtnLogin"
    ... />

<!-- EditText -->
<EditText
    android:id="@+id/mEtPhone"
    ... />

<!-- RecyclerView -->
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/mRvList"
    ... />

<!-- ConstraintLayout -->
<androidx.constraintlayout.widget.ConstraintLayout
    android:id="@+id/mClHeader"
    ... />

<!-- LinearLayout -->
<LinearLayout
    android:id="@+id/mLlContent"
    ... />
```

## 常用标准 ID

部分控件使用固定的标准 ID（无前缀）：

| ID | 控件类型 | 说明 |
|----|----------|------|
| `appbar` | AppBarLayout | 应用栏 |
| `appbar_layout` | AppBarLayout | 应用栏布局 |
| `toolbar` | Toolbar | 工具栏 |
| `fl_container` | FrameLayout | Fragment 容器 |
| `progressBar` | ProgressBar | 进度条 |
| `previewView` | PreviewView | 相机预览 |
| `main_content` | CoordinatorLayout | 主内容区域 |

## 命名规则总结

| 规则 | 说明 |
|------|------|
| m 前缀必须 | 所有自定义控件 ID 必须以 `m` 开头 |
| 类型缩写 | 紧跟 `m` 后面的是控件类型缩写（2-3个字母） |
| 驼峰命名 | 业务名称使用驼峰式（首字母大写） |
| 语义清晰 | ID 应能表达控件的用途 |

## 对照速查表

| 缩写 | 控件类型 | 示例 |
|------|----------|------|
| Tv | TextView | `mTvTitle` |
| Iv | ImageView | `mIvBack` |
| Btn | Button | `mBtnSubmit` |
| Et | EditText | `mEtPhone` |
| Chk | CheckBox | `mChkAgree` |
| Rb | RadioButton/RatingBar | `mRbScore` |
| Rv | RecyclerView | `mRvList` |
| Vp | ViewPager | `mVpHome` |
| Pb | ProgressBar | `mPbLoading` |
| Ll | LinearLayout | `mLlContent` |
| Fl | FrameLayout | `mFlContainer` |
| Cl | ConstraintLayout | `mClHeader` |
| Rl | RelativeLayout | `mRlItem` |
| Sv | ScrollView | `mSvContent` |
| Nsv | NestedScrollView | `mNsvDetail` |
| Wv | WebView | `mWvContent` |
| Ctl | CollapsingToolbarLayout | `mCtlTitle` |
| Tab | TabLayout | `mTabDetail` |
| View | View | `mViewDivider` |
