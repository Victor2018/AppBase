# 代码模板 (Code Templates)

> 本文档提供各层级代码的完整模板。
> 适用于所有采用 Retrofit + MVVM 架构的 Android 项目。
> 
> **注意**：以下模板中的包名 `{基础包名}` 和基类名（如 `BaseReq`、`AbsDS`）为示例，
> 实际使用时请根据项目现有规范替换。

## 目录

- [请求实体类模板](#1-请求实体类模板)
- [响应实体类模板](#2-响应实体类模板)
- [API路径常量模板](#3-api路径常量模板)
- [Retrofit接口模板](#4-retrofit接口模板)
- [DataSource接口模板](#5-datasource接口模板)
- [DataSource实现模板](#6-datasource实现模板)
- [ViewModel模板](#7-viewmodel模板)
- [ViewModel工厂模板](#8-viewmodel工厂模板)
- [Activity调用示例](#9-activity调用示例)

---

## 1. 请求实体类模板

**路径**：`{网络模块}/.../entity/request/{动作}{模块}Req.kt`

```kotlin
package {基础包名}.data.remote.entity.request

/**
 * {接口说明}请求参数
 * @date {yyyy-MM-dd}
 */
class {动作}{模块}Req : {请求基类}() {
    /** {字段说明} */
    var {字段名}: {类型}? = null
}
```

### 示例：添加子账号

```kotlin
package com.example.app.data.remote.entity.request

/**
 * 添加子账号请求参数
 * @date 2024-01-15
 */
class AddSubAccountReq : BaseReq() {
    /** 手机号 */
    var phone: String? = null
    /** 真实姓名 */
    var realName: String? = null
    /** 角色ID */
    var roleId: Int? = null
    /** 状态：0=禁用, 1=启用 */
    var status: Int? = null
}
```

---

## 2. 响应实体类模板

**路径**：`{网络模块}/.../entity/bean/{模块}Data.kt`

### 方式一：open class（推荐）

```kotlin
package {基础包名}.data.remote.entity.bean

/**
 * {数据说明}
 * @date {yyyy-MM-dd}
 */
open class {模块}Data {
    /** {字段说明} */
    var {字段名}: {类型}? = null
}
```

### 方式二：data class + @SerializedName

```kotlin
package {基础包名}.data.remote.entity.bean

import com.google.gson.annotations.SerializedName

/**
 * {数据说明}
 */
data class {模块}Data(
    @SerializedName("{json字段名}")
    val {字段名}: {类型}?,
    // ...
)
```

### 示例：子账号数据

```kotlin
package com.example.app.data.remote.entity.bean

/**
 * 子账号信息
 * @date 2024-01-15
 */
open class SubAccountData {
    /** 子账号ID */
    var id: Long? = null
    /** 手机号 */
    var phone: String? = null
    /** 真实姓名 */
    var realName: String? = null
    /** 角色ID */
    var roleId: Int? = null
    /** 角色名称 */
    var roleName: String? = null
    /** 状态：0=禁用, 1=启用 */
    var status: Int? = null
    /** 创建时间 */
    var createTime: String? = null
}
```

---

## 3. API路径常量模板

**路径**：`{网络模块}/.../api/{模块}Api.kt`

```kotlin
package {基础包名}.data.remote.api

/**
 * {模块名称}相关API路径
 */
object {模块}Api {
    /** {接口说明} */
    const val {动作大写} = "{接口路径}"
}
```

### 示例

```kotlin
package com.example.app.data.remote.api

/**
 * 子账号管理相关API路径
 */
object SubAccountApi {
    /** 添加子账号 */
    const val ADD = "user/front/subaccount/add"
    /** 获取子账号列表 */
    const val GET_LIST = "user/front/subaccount/list"
    /** 获取子账号详情 */
    const val GET_DETAIL = "user/front/subaccount/detail"
    /** 更新子账号 */
    const val UPDATE = "user/front/subaccount/update"
    /** 删除子账号 */
    const val DELETE = "user/front/subaccount/delete"
}
```

---

## 4. Retrofit接口模板

**路径**：`{网络模块}/.../service/{模块}ApiService.kt`

### POST请求（带Body）

```kotlin
package {基础包名}.data.remote.service

import {基础包名}.data.remote.api.{模块}Api
import {基础包名}.data.remote.entity.bean.{模块}Data
import {基础包名}.data.remote.entity.request.{动作}{模块}Req
import {网络库包名}.adapter.NetworkResponse
import {网络库包名}.data.BaseRes
import {网络库包名}.data.HttpError
import retrofit2.http.Body
import retrofit2.http.POST

interface {模块}ApiService {
    /**
     * {接口说明}
     */
    @POST({模块}Api.{动作大写})
    suspend fun {动作}{模块}(@Body body: {动作}{模块}Req?): NetworkResponse<BaseRes<{返回类型}>, HttpError>
}
```

### GET请求（带Query）

```kotlin
import retrofit2.http.GET
import retrofit2.http.Query

@GET({模块}Api.{动作大写})
suspend fun get{模块}List(
    @Query("page") page: Int?,
    @Query("pageSize") pageSize: Int?
): NetworkResponse<BaseRes<List<{模块}Data>>, HttpError>
```

### 无数据返回

`data` 为空或接口无返回字段时，泛型使用 `Nothing`（不要使用 `Any?`）：

```kotlin
@POST({模块}Api.{动作大写})
suspend fun {动作}{模块}(@Body body: {动作}{模块}Req?): NetworkResponse<BaseRes<Nothing>, HttpError>
```

### 完整示例

```kotlin
package com.example.app.data.remote.service

import {基础包名}.data.remote.api.SubAccountApi
import {基础包名}.data.remote.entity.bean.SubAccountData
import {基础包名}.data.remote.entity.request.AddSubAccountReq
import {基础包名}.data.remote.entity.request.UpdateSubAccountReq
import {网络库包名}.adapter.NetworkResponse
import {网络库包名}.data.BaseRes
import {网络库包名}.data.HttpError
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 子账号管理相关接口
 */
interface SubAccountApiService {

    /** 添加子账号 */
    @POST(SubAccountApi.ADD)
    suspend fun addSubAccount(@Body body: AddSubAccountReq?): NetworkResponse<BaseRes<Nothing>, HttpError>

    /** 获取子账号列表 */
    @GET(SubAccountApi.GET_LIST)
    suspend fun getSubAccountList(
        @Query("page") page: Int?,
        @Query("pageSize") pageSize: Int?
    ): NetworkResponse<BaseRes<List<SubAccountData>>, HttpError>

    /** 获取子账号详情 */
    @GET(SubAccountApi.GET_DETAIL)
    suspend fun getSubAccountDetail(
        @Query("id") id: Long?
    ): NetworkResponse<BaseRes<SubAccountData>, HttpError>

    /** 更新子账号 */
    @POST(SubAccountApi.UPDATE)
    suspend fun updateSubAccount(@Body body: UpdateSubAccountReq?): NetworkResponse<BaseRes<Nothing>, HttpError>

    /** 删除子账号 */
    @POST(SubAccountApi.DELETE)
    suspend fun deleteSubAccount(@Query("id") id: Long?): NetworkResponse<BaseRes<Nothing>, HttpError>
}
```

---

## 5. DataSource接口模板

**路径**：`lib_coremodel/.../interfaces/I{模块}DS.kt`

```kotlin
package {基础包名}.data.remote.interfaces

import androidx.lifecycle.LiveData
import {基础包名}.data.remote.entity.bean.{模块}Data
import {基础包名}.data.remote.entity.request.{动作}{模块}Req
import {网络库包名}.data.BaseRes
import {网络库包名}.data.HttpResult

/**
 * {模块名称}数据源接口
 */
interface I{模块}DS {
    /** {动作}结果 */
    val {动作小写}Data: LiveData<HttpResult<BaseRes<{返回类型}>>>
    
    /**
     * {接口说明}
     */
    suspend fun {动作小写}(body: {动作}{模块}Req?)
}
```

### 完整示例

```kotlin
package {基础包名}.data.remote.interfaces

import androidx.lifecycle.LiveData
import {基础包名}.data.remote.entity.bean.SubAccountData
import {基础包名}.data.remote.entity.request.AddSubAccountReq
import {基础包名}.data.remote.entity.request.UpdateSubAccountReq
import {网络库包名}.data.BaseRes
import {网络库包名}.data.HttpResult

/**
 * 子账号管理数据源接口
 */
interface ISubAccountDS {
    val addData: LiveData<HttpResult<BaseRes<Nothing>>>
    val listData: LiveData<HttpResult<BaseRes<List<SubAccountData>>>>
    val detailData: LiveData<HttpResult<BaseRes<SubAccountData>>>
    val updateData: LiveData<HttpResult<BaseRes<Nothing>>>
    val deleteData: LiveData<HttpResult<BaseRes<Nothing>>>

    suspend fun add(body: AddSubAccountReq?)
    suspend fun getList(page: Int?, pageSize: Int?)
    suspend fun getDetail(id: Long?)
    suspend fun update(body: UpdateSubAccountReq?)
    suspend fun delete(id: Long?)
}
```

---

## 6. DataSource实现模板

**路径**：`lib_coremodel/.../datasource/{模块}DS.kt`

```kotlin
package {基础包名}.data.remote.datasource

import androidx.lifecycle.MutableLiveData
import {基础包名}.data.remote.entity.bean.{模块}Data
import {基础包名}.data.remote.entity.request.{动作}{模块}Req
import {基础包名}.data.remote.interfaces.I{模块}DS
import {基础包名}.data.remote.service.{模块}ApiService
import {网络库包名}.ApiClient
import {网络库包名}.data.BaseRes
import {网络库包名}.data.HttpResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * {模块名称}数据源实现
 */
class {模块}DS : AbsDS(), I{模块}DS {
    override val {动作小写}Data = MutableLiveData<HttpResult<BaseRes<{返回类型}>>>()

    override suspend fun {动作小写}(body: {动作}{模块}Req?) {
        withContext(Dispatchers.Main) {
            {动作小写}Data.value = {动作小写}Req(body)
        }
    }

    private suspend fun <T> {动作小写}Req(body: {动作}{模块}Req?): T = withContext(Dispatchers.IO) {
        handleResponse(
            ApiClient.getApiService({模块}ApiService::class.java).{动作小写}(body)
        ) as T
    }
}
```

### 完整示例

```kotlin
package {基础包名}.data.remote.datasource

import androidx.lifecycle.MutableLiveData
import {基础包名}.data.remote.entity.bean.SubAccountData
import {基础包名}.data.remote.entity.request.AddSubAccountReq
import {基础包名}.data.remote.entity.request.UpdateSubAccountReq
import {基础包名}.data.remote.interfaces.ISubAccountDS
import {基础包名}.data.remote.service.SubAccountApiService
import {网络库包名}.ApiClient
import {网络库包名}.data.BaseRes
import {网络库包名}.data.HttpResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 子账号管理数据源实现
 */
class SubAccountDS : AbsDS(), ISubAccountDS {

    override val addData = MutableLiveData<HttpResult<BaseRes<Nothing>>>()
    override val listData = MutableLiveData<HttpResult<BaseRes<List<SubAccountData>>>>()
    override val detailData = MutableLiveData<HttpResult<BaseRes<SubAccountData>>>()
    override val updateData = MutableLiveData<HttpResult<BaseRes<Nothing>>>()
    override val deleteData = MutableLiveData<HttpResult<BaseRes<Nothing>>>()

    override suspend fun add(body: AddSubAccountReq?) {
        withContext(Dispatchers.Main) {
            addData.value = addReq(body)
        }
    }

    override suspend fun getList(page: Int?, pageSize: Int?) {
        withContext(Dispatchers.Main) {
            listData.value = getListReq(page, pageSize)
        }
    }

    override suspend fun getDetail(id: Long?) {
        withContext(Dispatchers.Main) {
            detailData.value = getDetailReq(id)
        }
    }

    override suspend fun update(body: UpdateSubAccountReq?) {
        withContext(Dispatchers.Main) {
            updateData.value = updateReq(body)
        }
    }

    override suspend fun delete(id: Long?) {
        withContext(Dispatchers.Main) {
            deleteData.value = deleteReq(id)
        }
    }

    private suspend fun <T> addReq(body: AddSubAccountReq?): T = withContext(Dispatchers.IO) {
        handleResponse(ApiClient.getApiService(SubAccountApiService::class.java).addSubAccount(body)) as T
    }

    private suspend fun <T> getListReq(page: Int?, pageSize: Int?): T = withContext(Dispatchers.IO) {
        handleResponse(ApiClient.getApiService(SubAccountApiService::class.java).getSubAccountList(page, pageSize)) as T
    }

    private suspend fun <T> getDetailReq(id: Long?): T = withContext(Dispatchers.IO) {
        handleResponse(ApiClient.getApiService(SubAccountApiService::class.java).getSubAccountDetail(id)) as T
    }

    private suspend fun <T> updateReq(body: UpdateSubAccountReq?): T = withContext(Dispatchers.IO) {
        handleResponse(ApiClient.getApiService(SubAccountApiService::class.java).updateSubAccount(body)) as T
    }

    private suspend fun <T> deleteReq(id: Long?): T = withContext(Dispatchers.IO) {
        handleResponse(ApiClient.getApiService(SubAccountApiService::class.java).deleteSubAccount(id)) as T
    }
}
```

---

## 7. ViewModel模板

**路径**：`lib_coremodel/.../vm/{模块}VM.kt`

```kotlin
package {基础包名}.data.remote.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import {基础包名}.data.remote.entity.bean.{模块}Data
import {基础包名}.data.remote.entity.request.{动作}{模块}Req
import {基础包名}.data.remote.interfaces.I{模块}DS
import {网络库包名}.data.BaseRes
import {网络库包名}.data.HttpResult
import kotlinx.coroutines.launch

/**
 * {模块名称} ViewModel
 */
class {模块}VM(private val dataSource: I{模块}DS) : ViewModel() {
    
    /** {动作}结果 */
    val {动作小写}Result: LiveData<HttpResult<BaseRes<{返回类型}>>> = dataSource.{动作小写}Data

    /**
     * {接口说明}
     */
    fun {动作小写}(body: {动作}{模块}Req?) {
        viewModelScope.launch {
            dataSource.{动作小写}(body)
        }
    }
}
```

### 完整示例

```kotlin
package {基础包名}.data.remote.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import {基础包名}.data.remote.entity.bean.SubAccountData
import {基础包名}.data.remote.entity.request.AddSubAccountReq
import {基础包名}.data.remote.entity.request.UpdateSubAccountReq
import {基础包名}.data.remote.interfaces.ISubAccountDS
import {网络库包名}.data.BaseRes
import {网络库包名}.data.HttpResult
import kotlinx.coroutines.launch

/**
 * 子账号管理 ViewModel
 */
class SubAccountVM(private val dataSource: ISubAccountDS) : ViewModel() {

    val addResult: LiveData<HttpResult<BaseRes<Nothing>>> = dataSource.addData
    val listResult: LiveData<HttpResult<BaseRes<List<SubAccountData>>>> = dataSource.listData
    val detailResult: LiveData<HttpResult<BaseRes<SubAccountData>>> = dataSource.detailData
    val updateResult: LiveData<HttpResult<BaseRes<Nothing>>> = dataSource.updateData
    val deleteResult: LiveData<HttpResult<BaseRes<Nothing>>> = dataSource.deleteData

    fun add(body: AddSubAccountReq?) {
        viewModelScope.launch { dataSource.add(body) }
    }

    fun getList(page: Int? = 1, pageSize: Int? = 20) {
        viewModelScope.launch { dataSource.getList(page, pageSize) }
    }

    fun getDetail(id: Long?) {
        viewModelScope.launch { dataSource.getDetail(id) }
    }

    fun update(body: UpdateSubAccountReq?) {
        viewModelScope.launch { dataSource.update(body) }
    }

    fun delete(id: Long?) {
        viewModelScope.launch { dataSource.delete(id) }
    }
}
```

---

## 8. ViewModel工厂模板

**路径**：`lib_coremodel/.../vm/factory/{模块}VMFactory.kt`

```kotlin
package {基础包名}.data.remote.vm.factory

import androidx.lifecycle.SavedStateRegistryOwner
import androidx.lifecycle.ViewModel
import {基础包名}.data.remote.datasource.{模块}DS
import {基础包名}.data.remote.vm.{模块}VM
import {网络库包名}.vm.BaseVMFactory

/**
 * {模块名称} ViewModel工厂
 */
class {模块}VMFactory(owner: SavedStateRegistryOwner) : BaseVMFactory(owner) {
    override fun getVM(): ViewModel {
        return {模块}VM({模块}DS())
    }
}
```

### 完整示例

```kotlin
package {基础包名}.data.remote.vm.factory

import androidx.lifecycle.SavedStateRegistryOwner
import androidx.lifecycle.ViewModel
import {基础包名}.data.remote.datasource.SubAccountDS
import {基础包名}.data.remote.vm.SubAccountVM
import {网络库包名}.vm.BaseVMFactory

/**
 * 子账号管理 ViewModel工厂
 */
class SubAccountVMFactory(owner: SavedStateRegistryOwner) : BaseVMFactory(owner) {
    override fun getVM(): ViewModel {
        return SubAccountVM(SubAccountDS())
    }
}
```

---

## 9. Activity调用示例

```kotlin
package {业务模块包名}.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import {公共库包名}.base.BaseActivity
import {公共库包名}.utils.ToastUtils
import {基础包名}.data.remote.entity.request.AddSubAccountReq
import {基础包名}.data.remote.vm.SubAccountVM
import {基础包名}.data.remote.vm.factory.SubAccountVMFactory
import {网络库包名}.data.HttpResult
import {业务模块包名}.databinding.ActivityXxxBinding
import {项目包名}.utils.InjectorUtils

class SubAccountActivity : BaseActivity<ActivityXxxBinding>(ActivityXxxBinding::inflate) {

    private val subAccountVM: SubAccountVM by viewModels {
        InjectorUtils.provideSubAccountVMFactory(this)
    }

    override fun initView() {
        // 初始化视图
    }

    override fun initData() {
        // 加载初始数据
        subAccountVM.getList()
    }

    override fun subscribeUi() {
        // 监听添加结果
        subAccountVM.addResult.observe(this, Observer { result ->
            when (result) {
                is HttpResult.Success -> {
                    ToastUtils.show("添加成功")
                    subAccountVM.getList() // 刷新列表
                }
                is HttpResult.Error -> {
                    ToastUtils.show(result.message ?: "添加失败")
                }
            }
        })

        // 监听列表数据
        subAccountVM.listResult.observe(this, Observer { result ->
            when (result) {
                is HttpResult.Success -> {
                    val list = result.value.data ?: emptyList()
                    // 更新UI
                }
                is HttpResult.Error -> {
                    ToastUtils.show(result.message ?: "加载失败")
                }
            }
        })
    }

    private fun doAddSubAccount() {
        val req = AddSubAccountReq().apply {
            phone = binding.mEtPhone.text.toString()
            realName = binding.mEtRealName.text.toString()
            roleId = binding.mSpinnerRoleId
            status = 1
        }
        subAccountVM.add(req)
    }

    companion object {
        fun intentStart(context: Context) {
            context.startActivity(Intent(context, SubAccountActivity::class.java))
        }
    }
}
```

---

## InjectorUtils 依赖注入

> 底层数据接口通过 `InjectorUtils` 统一暴露给上层（Activity/Fragment）使用。
> 项目采用**手动依赖注入**（无 Hilt/Dagger/Koin），`InjectorUtils` 是唯一的注入入口。

### 依赖注入链路

```
Activity / Fragment（上层 UI）
    ↓ by viewModels { InjectorUtils.provide{模块}VMFactory(this) }
InjectorUtils（注入入口）
    ↓ provide{模块}VMFactory()
{模块}VMFactory（VM 工厂）
    ↓ getVM() → {模块}VM({模块}DS())
{模块}VM（ViewModel）
    ↓ 持有 I{模块}DS 接口
{模块}DS（DataSource 实现）
    ↓ 调用 ApiClient.getApiService({模块}ApiService::class.java)
{模块}ApiService（Retrofit 接口）
```

### InjectorUtils 完整模板

**路径**：`{项目包名}/utils/InjectorUtils.kt`

```kotlin
package {项目包名}.utils

import androidx.lifecycle.SavedStateRegistryOwner
import {基础包名}.data.remote.datasource.{模块}DS
import {基础包名}.data.remote.vm.{模块}VM
import {基础包名}.data.remote.vm.factory.{模块}VMFactory

/**
 * 依赖注入工具类
 * 
 * 职责：为上层 UI（Activity/Fragment）提供底层 ViewModel 工厂实例。
 * 每新增一个业务模块，需在此添加对应的 provide 方法。
 */
object InjectorUtils {

    /**
     * 提供 {模块名称} ViewModel 工厂
     */
    fun provide{模块}VMFactory(owner: SavedStateRegistryOwner): {模块}VMFactory {
        return {模块}VMFactory(owner)
    }

    // ===== 以下为其他模块示例 =====

    // fun provideAuthVMFactory(owner: SavedStateRegistryOwner): AuthVMFactory {
    //     return AuthVMFactory(owner)
    // }

    // fun provideHomeVMFactory(owner: SavedStateRegistryOwner): HomeVMFactory {
    //     return HomeVMFactory(owner)
    // }
}
```

### 完整示例：子账号模块注入

```kotlin
package com.example.app.utils

import androidx.lifecycle.SavedStateRegistryOwner
import com.example.app.data.remote.datasource.SubAccountDS
import com.example.app.data.remote.vm.SubAccountVM
import com.example.app.data.remote.vm.factory.SubAccountVMFactory

object InjectorUtils {

    fun provideSubAccountVMFactory(owner: SavedStateRegistryOwner): SubAccountVMFactory {
        return SubAccountVMFactory(owner)
    }
}
```

### 上层调用方式

#### Activity 中使用

```kotlin
class SubAccountActivity : BaseActivity<ActivitySubAccountBinding>(ActivitySubAccountBinding::inflate) {

    // 通过 InjectorUtils 获取 ViewModel
    private val subAccountVM: SubAccountVM by viewModels {
        InjectorUtils.provideSubAccountVMFactory(this)
    }

    override fun initData() {
        subAccountVM.getList()
    }

    override fun subscribeUi() {
        subAccountVM.listResult.observe(this) { result ->
            when (result) {
                is HttpResult.Success -> { /* 处理成功 */ }
                is HttpResult.Error -> { /* 处理失败 */ }
            }
        }
    }
}
```

#### Fragment 中使用

```kotlin
class SubAccountFragment : BaseFragment<FragmentSubAccountBinding>(FragmentSubAccountBinding::inflate) {

    private val subAccountVM: SubAccountVM by viewModels {
        InjectorUtils.provideSubAccountVMFactory(requireActivity())
    }

    override fun lazyLoad() {
        subAccountVM.getList()
    }
}
```

### 新增模块时的 InjectorUtils 更新步骤

1. 创建 `{模块}DS`（DataSource 实现）
2. 创建 `{模块}VM`（ViewModel），构造函数接收 `I{模块}DS`
3. 创建 `{模块}VMFactory`，内部实例化 `{模块}VM({模块}DS())`
4. 在 `InjectorUtils` 中添加 `provide{模块}VMFactory()` 方法
5. 上层 Activity/Fragment 通过 `by viewModels { InjectorUtils.provide{模块}VMFactory(this) }` 获取 VM

### 设计原则

| 原则 | 说明 |
|------|------|
| **单向依赖** | 上层(UI) → InjectorUtils → Factory → VM → DS → ApiService，禁止反向依赖 |
| **接口隔离** | VM 只持有 `I{模块}DS` 接口，不直接依赖 DS 实现类 |
| **单一入口** | 所有 VM 工厂统一由 InjectorUtils 提供，便于管理和查找 |
| **生命周期安全** | Factory 接收 `SavedStateRegistryOwner`，确保 VM 与 UI 生命周期绑定 |
