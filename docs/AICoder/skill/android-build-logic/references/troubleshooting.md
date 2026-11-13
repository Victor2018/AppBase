# build-logic 常见问题

先判断问题发生在哪一层：**插件解析**（找不到 id）→ **build-logic 编译**（脚本或类报错）→ **插件执行**（配置冲突、flavor 不生效）。层不同，排查位置不同。

## 1. Plugin [id: 'com.<域>.convention.xxx'] was not found

| 检查 | 正确状态 |
|---|---|
| 根 `settings.gradle.kts` | 有 `includeBuild("build-logic")`，且在模块 `include` 之前 |
| `convention/build.gradle.kts` | 有对应 `register("...")` 块 |
| catalog `[plugins]` | 有对应别名，`id` 与 `register` 的 id 完全一致 |
| build-logic 自身 | 能单独 Sync；`./gradlew help` 不报错 |

常见原因：

- 三处只改了两处（类、注册、catalog 缺一）。
- id 拼写不一致，例如 catalog 写 `com.<域>.convention.module`，注册写 `com.<域>.convention.android.module`。
- 别名加了 `version`，Gradle 去插件门户找，而本地没有发布。
- 改了 build-logic 但 IDE 仍用旧缓存，执行 `./gradlew --stop` 后重新 Sync。

## 2. Unresolved reference: libs（在 convention/build.gradle.kts 内）

- `build-logic/settings.gradle.kts` 缺少：

```kotlin
versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
```

- `from(...)` 路径写错。它是相对 `build-logic/settings.gradle.kts` 的路径，根工程在上一级时应为 `../gradle/libs.versions.toml`。
- 访问器写法错：别名 `ydj-android-module` 对应 `libs.plugins.ydj.android.module`，不是 `libs.plugins.ydj-android-module`。
- 新增别名后没有 Sync。

## 3. 模块脚本里 Unresolved reference: AppBuildConfig

- `AppBuildConfig` 在 build-logic 中，只有模块 apply 了约定插件、拿到插件 classpath 后才能 import。
- 模块没有 apply 任何约定插件却 import 配置对象：改成不 import，或先 apply `ydj.android.application` / `ydj.android.lib`。
- IDE 未把 included build 加入索引：重新 Sync，必要时 Invalidate Caches。

## 4. validatePlugins 失败

```bash
./gradlew :convention:validatePlugins --stacktrace
```

| 报错要点 | 处理 |
|---|---|
| `implementationClass` 不存在 | 类路径、包名、类名与注册字符串不一致 |
| 同一 id 注册两次 | 删除重复 `register` 块 |
| `Plugin` 未实现 `apply` | 插件类必须 `Plugin<Project>` 且实现 `apply(target: Project)` |
| 警告被当作错误 | `failOnWarning = true`，按提示修掉，不要改为 false 绕过 |

## 5. Cannot add extension with name 'android' / 插件重复应用

- 模块同时写了 `alias(libs.plugins.android.library)` 和约定插件。删掉模块里的外部平台插件别名，只留约定插件。
- `application` / `module` 已包含 `base`、`room`，模块又 apply 一次会重复。

## 6. 资源找不到或 R 引用冲突

- `resourcePrefix` 由模块目录名推导：`module_wallet` → `wallet_`。改名后模块内资源前缀必须跟着改。
- 跨模块引用 `lib_common` 资源时，资源名要全局唯一；模块私有资源不要起通用名。
- 新增资源先搜 `lib_common`，已有就引用。

## 7. flavor 任务不存在（如 `assembleDevDebug`）

- 模块没有 apply 约定插件，`configureKotlinAndroid` 未执行，flavors 未注入。
- 插件里 `configureKotlinAndroid` 未被调用（例如自定义插件漏调）。
- flavor 名称与脚本不一致：以 `support/KotlinAndroid.kt` 的 `productFlavors` 为准，`_test` 的任务是 `assemble_TestDebug`。

## 8. 组件模式不生效

- `AppBuildConfig.isModule` 为 `false`：模块按 library 构建，app 正常聚合。
- 改为 `true` 后，模块必须有 `src/main/module/AndroidManifest.xml`，且含 `App`、launcher 与 `tools:replace`，否则安装后无入口。
- 切换后先 `./gradlew :module_xxx:assembleDevDebug` 单独验证，再验证 app。

## 9. 签名失败 / keystore 不存在

- `STORE_FILE` 相对根工程解析，确认 `rootProject.file(...)` 能找到文件。
- 本地没有 keystore 时应跳过签名而不是让构建崩溃；CI 用 Secret 注入密码。
- 不要把 keystore 和密码提交仓库。

## 10. 分发任务误触发或上传失败

- 自动分发依赖 `gradle.startParameter.taskNames` 命中 `UploadType.taskName`；执行的是 `assembleDevDebug` 而配置里只有 `assembleOnlineRelease` 时不会上传。
- 平台 key 为空时按设计跳过；若必须上传，检查环境变量名是否与代码一致。
- Python 脚本或解释器缺失会抛异常，先确认路径与本机环境。

## 11. 改了 build-logic 但构建行为没变

1. `./gradlew --stop` 停掉 daemon。
2. 重新 Sync。
3. 执行 `./gradlew :convention:validatePlugins` 确认改动已编译。
4. 仍无效时检查是否改在 `build/` 产物或缓存目录，而不是 `src/main/java`。

## 12. 排查顺序

```bash
./gradlew :convention:validatePlugins   # build-logic 自身是否合法
./gradlew help                          # 插件 id 与 catalog 是否能解析
./gradlew :module_xxx:assembleDevDebug  # 单模块是否生效
./gradlew :app:assembleDevDebug         # 全链路
```

从最内层开始，前一步通过再跑下一步，避免把编译错误误判成插件未找到。
