# XML 布局规范 (XML Layout Specification)

> 本文档定义 Android XML 布局文件的编写规范，不涉及命名。

## 文件头格式

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- 不添加描述性注释，直接跟根标签 -->
<根布局
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:ignore="ResourceName">
```

## 命名空间声明顺序

```xml
xmlns:android="http://schemas.android.com/apk/res/android"   <!-- 第1位 -->
xmlns:app="http://schemas.android.com/apk/res-auto"          <!-- 第2位 -->
xmlns:tools="http://schemas.android.com/tools"               <!-- 第3位 -->
```

## 属性书写顺序

```xml
<TextView
    android:id="@+id/mTvTitle"                              <!-- 1. ID -->
    android:layout_width="0dp"                                <!-- 2. 布局尺寸 -->
    android:layout_height="@dimen/dp_96"
    app:layout_constraintTop_toTopOf="parent"                 <!-- 3. 约束 -->
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    android:layout_marginTop="@dimen/dp_40"                   <!-- 4. 边距 -->
    android:layout_marginStart="@dimen/dp_16"
    android:background="@drawable/shape_white_radius_12"      <!-- 5. 外观 -->
    android:gravity="center"                                  <!-- 6. 内容/文本 -->
    android:textColor="@color/color_1A1A1A"
    android:textSize="@dimen/dp_32"
    android:text="标题"
    android:clickable="true"                                  <!-- 7. 行为 -->
    android:onClick="onTitleClick"
    tools:text="预览文本"                                      <!-- 8. tools 预览 -->
    tools:visibility="visible"/>
```

## 根布局选择规则

| 页面类型 | 根布局 | 说明 |
|----------|--------|------|
| 带 Toolbar 的 Activity | CoordinatorLayout | 配合 AppBarLayout 折叠 |
| 普通 Activity | ConstraintLayout | 首选，性能最优 |
| Fragment | ConstraintLayout | 首选 |
| Dialog | ConstraintLayout / LinearLayout | 根据复杂度选择 |
| RecyclerView Item | ConstraintLayout / FrameLayout | 根据复杂度选择 |
| 简单列表项 | 单个 TextView / ImageView | 最简结构 |

## 布局嵌套规则

### 核心原则

**尽量使用 ConstraintLayout 一层布局完成所有布局需求**，禁止同类布局嵌套，禁止 ConstraintLayout 嵌套其他布局容器。

### 禁止的嵌套组合

| 禁止规则 | 说明 |
|----------|------|
| ConstraintLayout 嵌套 ConstraintLayout | 同类禁止 |
| ConstraintLayout 嵌套 LinearLayout | 禁止，用约束替代 |
| ConstraintLayout 嵌套 RelativeLayout | 禁止，用约束替代 |
| ConstraintLayout 嵌套 FrameLayout | 禁止，用约束替代 |
| LinearLayout 嵌套 LinearLayout | 同类禁止 |
| RelativeLayout 嵌套 RelativeLayout | 同类禁止 |
| FrameLayout 嵌套 FrameLayout | 同类禁止 |

### ConstraintLayout 嵌套 ConstraintLayout（禁止）

```xml
<!-- ❌ 错误示例 -->
<androidx.constraintlayout.widget.ConstraintLayout ...>
    <androidx.constraintlayout.widget.ConstraintLayout ...>
        <TextView ... />
    </androidx.constraintlayout.widget.ConstraintLayout>
</androidx.constraintlayout.widget.ConstraintLayout>
```

```xml
<!-- ✅ 正确示例：扁平化约束 -->
<androidx.constraintlayout.widget.ConstraintLayout ...>
    <TextView
        android:id="@+id/mTvTitle"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        ... />
    <TextView
        android:id="@+id/mTvSubtitle"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/mTvTitle"
        ... />
</androidx.constraintlayout.widget.ConstraintLayout>
```

### ConstraintLayout 嵌套其他布局（禁止）

```xml
<!-- ❌ 禁止：ConstraintLayout 内嵌套 LinearLayout -->
<androidx.constraintlayout.widget.ConstraintLayout ...>
    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">
        <TextView ... />
        <TextView ... />
    </LinearLayout>
</androidx.constraintlayout.widget.ConstraintLayout>
```

```xml
<!-- ✅ 正确：直接用 ConstraintLayout 约束所有控件 -->
<androidx.constraintlayout.widget.ConstraintLayout ...>
    <TextView
        android:id="@+id/mTvTitle"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        ... />
    <TextView
        android:id="@+id/mTvSubtitle"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/mTvTitle"
        ... />
</androidx.constraintlayout.widget.ConstraintLayout>
```

### 同类布局嵌套（禁止）

```xml
<!-- ❌ 禁止：LinearLayout 嵌套 LinearLayout -->
<LinearLayout ...>
    <LinearLayout ...>
        <TextView ... />
    </LinearLayout>
</LinearLayout>

<!-- ❌ 禁止：RelativeLayout 嵌套 RelativeLayout -->
<RelativeLayout ...>
    <RelativeLayout ...>
        <TextView ... />
    </RelativeLayout>
</RelativeLayout>

<!-- ❌ 禁止：FrameLayout 嵌套 FrameLayout -->
<FrameLayout ...>
    <FrameLayout ...>
        <TextView ... />
    </FrameLayout>
</FrameLayout>
```

### 允许的嵌套组合

仅在以下特殊场景允许嵌套，且外层**不能是 ConstraintLayout**：

| 外层布局 | 可嵌套 | 适用场景 |
|----------|--------|----------|
| LinearLayout | RecyclerView / ScrollView / ViewPager | 列表或滚动容器 |
| FrameLayout | RecyclerView / ViewPager / 单个控件 | 层叠替换场景 |
| RelativeLayout | RecyclerView / ScrollView | 兼容旧布局 |

### 嵌套层级限制

- 最大嵌套层级：**2 层**（含根布局）
- 推荐：**1 层 ConstraintLayout** 完成全部布局
- 超过 2 层必须重构

### 布局选择优先级

```
1. ConstraintLayout（首选，一层完成）
       ↓ 无法实现时
2. LinearLayout / FrameLayout（仅用于列表、滚动等特殊容器）
       ↓ 禁止再嵌套
3. 使用 <include> 复用子布局
```

## 尺寸规范

### 设计图基准

- Figma 设计图宽度：750px
- 对应 Android 密度：xxhdpi（3x）
- 换算规则：Figma px = Android dp（1:1 对应）

### 使用规则

```xml
<!-- 所有尺寸使用 @dimen/dp_* 引用 -->
android:layout_width="@dimen/dp_100"
android:layout_height="@dimen/dp_48"
android:padding="@dimen/dp_16"
android:layout_marginTop="@dimen/dp_24"
android:textSize="@dimen/dp_28"

<!-- 禁止使用 sp -->
<!-- ❌ android:textSize="14sp" -->
<!-- ✅ android:textSize="@dimen/dp_28" -->
```

## 颜色引用规范

```xml
<!-- 通过 @color/color_* 引用 -->
android:textColor="@color/color_1A1A1A"
android:background="@color/color_F9F9F9"
app:strokeColor="@color/color_0084FF"
```

## ImageView 尺寸规范

### 图标类（wrap_content）

```xml
<ImageView
    android:id="@+id/mIvBack"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:padding="@dimen/dp_12"
    android:src="@mipmap/ic_back_black"
    tools:ignore="ContentDescription"/>
```

### 内容图片（固定尺寸）

```xml
<ImageView
    android:id="@+id/mIvAvatar"
    android:layout_width="@dimen/dp_80"
    android:layout_height="@dimen/dp_80"
    android:scaleType="centerCrop"
    tools:ignore="ContentDescription"
    tools:src="@mipmap/img_placeholder"/>
```

## Toolbar 标准结构

```xml
<androidx.coordinatorlayout.widget.CoordinatorLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:ignore="ResourceName">

    <com.google.android.material.appbar.AppBarLayout
        android:id="@+id/mAppBar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/white">

        <com.google.android.material.appbar.CollapsingToolbarLayout
            android:id="@+id/mCtlTitle"
            android:layout_width="match_parent"
            android:layout_height="@dimen/dp_96"
            app:layout_scrollFlags="scroll|exitUntilCollapsed"
            app:titleEnabled="false">

            <androidx.appcompat.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                style="@style/ToolbarStyle"
                app:layout_collapseMode="pin">

                <androidx.constraintlayout.widget.ConstraintLayout
                    android:layout_width="match_parent"
                    android:layout_height="match_parent">

                    <ImageView
                        android:id="@+id/mIvBack"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:padding="@dimen/dp_12"
                        android:src="@mipmap/ic_back_black"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent"
                        tools:ignore="ContentDescription"/>

                    <TextView
                        android:id="@+id/mTvTitle"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:textColor="@color/color_1A1A1A"
                        android:textSize="@dimen/dp_36"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent"
                        tools:text="标题"/>

                </androidx.constraintlayout.widget.ConstraintLayout>

            </androidx.appcompat.widget.Toolbar>

        </com.google.android.material.appbar.CollapsingToolbarLayout>

    </com.google.android.material.appbar.AppBarLayout>

    <!-- 内容区域 -->
    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <!-- 页面内容 -->

    </androidx.constraintlayout.widget.ConstraintLayout>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

## include 使用规范

```xml
<!-- 头部子布局引用 -->
<include
    android:id="@+id/includeHeader"
    layout="@layout/act_pay_way_header"
    android:layout_width="0dp"
    android:layout_height="@dimen/dp_96"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent"/>
```

## tools 属性使用

```xml
<!-- 抑制多模块资源同名警告（必须添加） -->
tools:ignore="ResourceName"

<!-- 抑制 ContentDescription 警告 -->
tools:ignore="ContentDescription"

<!-- 预览文本 -->
tools:text="预览文本"

<!-- 预览可见性 -->
tools:visibility="visible"

<!-- 预览图片 -->
tools:src="@mipmap/img_placeholder"

<!-- 预览列表项 -->
tools:listitem="@layout/rv_message_cell"
```

## 代码格式

- 缩进：4 个空格
- 不使用 Tab
- 每个属性独占一行
- 自闭合标签使用 `/>` 结尾
