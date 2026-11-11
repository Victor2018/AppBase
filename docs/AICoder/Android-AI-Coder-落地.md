# Android AI Coder 落地

> 适用对象：Android 客户端开发、测试、技术负责人
>
> 覆盖范围：Android 项目 AI 编码能力落地——三个 Skill、两个 Command；以 Qoder CN 桌面端 + Android Studio Qoder CN 插件为主要客户端，同一套 Skill 与 Command 可复用到 Cursor、Trae、CodeBuddy、Claude Code。Skill、Command 与 MCP 的配置、凭证获取见 [SKILL-MCP-COMMAND配置指南.md](SKILL-MCP-COMMAND配置指南.md)
>
> 版本：v1.8 ｜ 日期：2026-11-11

**Android AI Coder** 指沉淀在 Android 工程里的整套 AI 编码能力：MCP 负责「读得到」，三个 Skill 负责「做得对」，两个 Command 负责「串得起来」。它不绑定某个客户端，Qoder 是本文的主要示例，其他客户端的复用方式见 0.6；使用前提见 0.7——规则来自项目架构，架构不清先别接。

---

## 0. 落地目标与前置条件

### 0.1 最终目标

让 AI 编码助手在 Android 项目中具备四项能力：

1. **读设计稿**：通过 Figma MCP 读取设计结构和图片，生成符合项目规范的 XML 与页面代码。
2. **读缺陷单**：通过 TAPD MCP 读取 Bug 详情、评论和流转记录，分析根因并修复。
3. **按项目规范产出**：通过三个 Skill 约束目录结构、命名、分层、资源位置和验收标准。
4. **一键串联流程**：通过 Commands 把「UI → 接口 → 数据上屏 → 审查」固定成一条命令，减少阶段遗漏和人工交接。

### 0.2 先区分 MCP、Skill 与 Command

| 概念 | 作用 | 本项目的例子 |
|---|---|---|
| MCP | 给 AI 提供工具和数据通道 | Figma MCP 读取设计稿；TAPD MCP 读取 Bug |
| Skill | 给 AI 提供单点任务的流程、规范和检查清单 | 设计稿怎么转 XML、接口链路怎么分层、Bug 怎么定位 |
| Command | 把多个 Skill 和 MCP 调用按固定顺序、固定门禁编排成一条可复用指令 | `/android-feature` 串起 UI → 接口 → 上屏 → 审查；`/android-review` 双规范审查 |

三者缺一不可：只有 MCP，AI 能读到数据但不知道项目规范；只有 Skill，AI 知道规范但读不到 Figma/TAPD，而且需要人逐个触发；只有 Command，能串起流程但每个环节没有可依据的规范。

- **MCP 解决「读得到」**：Figma、TAPD、Swagger 的数据通道。
- **Skill 解决「做得对」**：单点任务的规范、模板和验收清单。
- **Command 解决「串得起来」**：阶段顺序、交接门禁、确认停止点由命令固化，不依赖人记流程。

Skill 和 Command 都不是 Qoder 专有能力：Skill 是遵循开放约定的 Markdown 指令包，Command 是一份 Markdown 编排提示词。换到 Cursor、Trae、CodeBuddy、Claude Code 等客户端时，规范内容和流程不变，只需要换安装目录和触发方式，见 0.6。

### 0.3 三个 Skill 一页速览

| Skill | 解决什么问题 | 输入 | 主要产出 | 覆盖阶段 |
|---|---|---|---|---|
| `figma-android-ui` | 设计稿到 XML 布局的重复还原与命名不统一 | Figma / MasterGo / 即时设计 / 蓝湖 / Sketch / XD / PS 链接或截图 | XML 布局、Activity/Fragment 代码、资源命名与切图清单 | UI 开发 |
| `android-api-ds` | 一个接口要写 8~9 个文件，容易漏层、漏字段 | Swagger / Knife4j 文档链接或截图 | Req、Data、Api、ApiService、IDS、DS、VM、VMFactory、InjectorUtils | 接口联调 |
| `fix-tapd-android-bug` | Bug 上下文分散，描述与评论不一致，修复易漏文案 | TAPD Bug 链接或 Bug ID | 根因分析、修复方案、代码修改 | 提测与缺陷修复 |

**一句话结论**：这三个 Skill 不是让 AI 随便写代码，而是把团队已有的目录结构、命名规范、分层方式和检查清单变成 AI 可执行的约束，让产出直接接近可提交状态，减少返工和 Review 轮次。

**它们不覆盖**：需求评审、服务端开发、自动化测试、发布上线。

### 0.4 为什么要把流程做成 Skill

Android 日常开发中，真正耗时的往往不是写业务逻辑，而是下面四类工作：

| 痛点 | 具体表现 | 结果 |
|---|---|---|
| 信息跨系统搬运 | 设计在 Figma、接口在 Swagger、缺陷在 TAPD，反复切换和截图 | 上下文准备时间长 |
| 规范靠记忆和 Review 兜底 | 控件 ID、颜色、布局文件、类名、LiveData 命名各有规则 | 首轮 PR 问题多，返工 |
| 样板代码占比高 | 一个接口要新增请求类、响应类、路径常量、Service、DS、VM、Factory、注入入口 | 机械劳动多，容易漏层 |
| 问题定位依赖个人经验 | Bug 描述不准确，关键结论埋在评论里 | 定位慢，修复范围不确定 |

Skill 的本质，是把「输入格式 + 执行步骤 + 规范约束 + 检查清单」固化成一份可复用、可版本化的指令：

```
口头经验 / 团队规范
        ↓ 固化
Skill（SKILL.md + references）
        ↓ 执行
按统一标准产出的代码与文档
```

它带来的不是单次写得更快，而是**同一类任务每次都能按同一标准完成**。

当多个 Skill 需要按固定顺序协作时，再向上固化一层 Command：Skill 定义「每一步怎么做」，Command 定义「先做哪步、做到什么程度才进入下一步」。见 0.5 与 4.5。

> AI 可以加速生成，但不能替代业务判断、代码审查和测试验证。效率提升必须建立在质量达标之上。

### 0.5 两个 Command 一页速览

Skill 是单点能力，Command 是把单点能力串成流水线的编排层。本项目提供两个命令：

| Command | 解决什么问题 | 输入 | 主要产出 | 是否改代码 |
|---|---|---|---|---|
| `/android-feature` | 一个功能跨 UI、接口、上屏、审查四段，靠人记顺序容易漏阶段、漏规范 | 设计稿链接 + 接口文档链接 + 功能名，顺序任意；`-y` 跳过中途确认 | XML 与页面骨架、完整网络链路、数据上屏、审查报告 | 阶段一~三改代码，阶段四只审不改 |
| `/android-review` | 审查只看正确性，不按项目规范逐条比对 | 无参默认审查未提交改动 + 当前分支 diff；也可指定文件、目录或提交 | 分级问题清单 + 最小修复方案 | 否 |

- `/android-feature` 内部依次调用 `figma-android-ui`、`android-api-ds`，第四阶段复用 `/android-review` 的标准。
- `/android-review` 可以脱离流水线单独使用，作为提交前的统一审查入口。
- 命令的安装与验证见 [SKILL-MCP-COMMAND配置指南.md](SKILL-MCP-COMMAND配置指南.md) 第 2 节，完整用法与示例见 4.5。

### 0.6 跨客户端复用：Skill 与 Command 不绑定 Qoder

本项目的三个 Skill 和两个 Command 不是 Qoder 专属资产，可以在使用不同客户端的团队成员之间共享。

- **Skill 是开放格式**：一个目录 + `SKILL.md`（YAML frontmatter + Markdown 正文）+ 可选的 `references/`、`scripts/`。这是 Agent Skills 的通用约定，Cursor、Trae、CodeBuddy、Claude Code 等客户端都能直接读取同一份内容。
- **Command 是普通 Markdown**：正文写的是阶段顺序、门禁和停止点，不调用任何 Qoder 专有接口。

换客户端时，规范与流程不变，要改的只有「放在哪、怎么触发」：

| 要素 | 是否通用 | 说明 |
|---|---|---|
| `SKILL.md` 正文与 `references/` | 通用 | 纯 Markdown，与工具无关，不需要改写 |
| frontmatter 的 `name` / `description` | 通用，但更关键 | 多数客户端靠 `description` 自动匹配，描述要写清「做什么 + 何时用」 |
| Skill 安装目录 | 不通用 | 各客户端目录不同，见下方对照表 |
| Command 正文 | 通用 | 斜杠命令、规则文件、固定 Prompt 都能承载 |
| Command 的调用入口 | 不通用 | 名称、斜杠语法、是否支持自定义命令因工具而异 |
| MCP 配置 | 不通用 | 配置文件位置和启用开关不同，需按各客户端文档单独配置 |

**Skill 目录对照**

各客户端的 Skill 安装目录不同，完整对照与安装步骤见 [SKILL-MCP-COMMAND配置指南.md](SKILL-MCP-COMMAND配置指南.md) 1.1。

**Command 的承载方式**

| 客户端 | 推荐方式 |
|---|---|
| Qoder CN | 放进 `commands/`，用 `/android-feature`、`/android-review` 调用 |
| Claude Code | `commands/` 下的 Markdown 同样映射为斜杠命令 |
| Cursor / Trae / CodeBuddy | 支持自定义命令时放对应命令目录；不支持时把命令正文作为规则文件或固定 Prompt 使用，由人按阶段推进 |

不管用哪种方式，Command 都只是编排层：规范仍由 Skill 提供，数据仍由 MCP 提供。客户端不支持自定义命令，不影响 Skill 单独使用，只是少了一层流水线。

**一份源，多处复用**

同时使用多个客户端时，不要把 Skill 复制成多份分别维护，否则很快出现版本漂移，A 工具改过的规则在 B 工具里还是旧的。

```bash
# macOS / Linux：把共享目录软链到各客户端
ln -s ~/shared-skills ~/.qoder-cn/skills
ln -s ~/shared-skills ~/.cursor/skills
ln -s ~/shared-skills ~/.trae/skills
ln -s ~/shared-skills ~/.codebuddy/skills
```

```bat
:: Windows：目录联接（/J 不需要管理员权限，跨盘也可用）
mklink /J "%USERPROFILE%\.qoder-cn\skills" "D:\shared-skills"
```

团队协作仍推荐项目级安装：把 `.qoder/skills/`、`.qoder/commands/` 提交到仓库，Skill 正文只维护这一份；其他客户端需要时，按上表软链或同步到自己的目录，不要各改一版。

**跨工具差异（实际会遇到）**

| 差异 | 表现 | 处理 |
|---|---|---|
| 自动触发不稳定 | 有的客户端不自动匹配 `description` | 显式写「使用 figma-android-ui 技能处理这个设计稿」 |
| 不支持 references 按需加载 | 只读 `SKILL.md`，忽略子目录 | 让工具先读指定 reference，或把关键规范直接贴进对话 |
| 不支持 MCP | 读不到 Figma、TAPD | 用截图、导出的接口文档作为输入，并相应提高人工校准要求 |
| 斜杠命令不可用 | 输入 `/` 看不到命令 | 按上面的承载方式改成规则文件或固定 Prompt |
| 命令里写死了 Skill 路径 | 换客户端后路径不存在 | 命令正文优先按 Skill 名称引用，实际路径由安装位置决定 |

> 跨工具复用的是「规范与流程」，不是「效果完全一致」。各客户端的模型、上下文窗口和工具调用能力不同，生成结果仍要按第 6 节的质量门禁验收。

### 0.7 使用前提：先有架构，再谈 AI

这套能力的规则全部来自项目本身，不是通用模板，也不是 AI 凭空生成的「最佳实践」。

| 前提 | 说明 |
|---|---|
| 必须熟悉项目架构 | 使用前先读 README、`settings.gradle`、模块职责、基础库、基类、响应封装与命名规范；能说清新代码该落在哪个模块 |
| 说明文档要能对上代码 | 架构说明、目录约定、命名规范与代码不一致时先修文档；按错误文档写出的 Skill，产出只会错得更整齐 |
| 规则来自现有代码 | Skill 里的基类名、包名、分层方式、响应封装、DI 方式，都照搬工程现有实现，不额外发明一套 |
| 按项目实际情况写 Skill | 本项目三个 Skill 只是样例；换项目要按 1.5 改写 references，不是直接复制过去 |
| 分层不明确就不建议用 | 没有统一基类、网络封装和模块边界时，AI 只能猜；猜出来的代码进不了 Code Review |

**适用性判断**

| 情况 | 建议 |
|---|---|
| 分层清晰：有基础库、统一响应封装、明确模块边界 | 可以直接试用 |
| 有命名规范与目录约定文档，且与代码一致 | 可以直接试用 |
| 规范只存在于个人经验，没有文档 | 先整理成文档，或先用 Skill 固化一版 |
| 架构分层不明确、同类代码有多种写法 | 不建议直接上 Skill，先做架构收敛再引入 |
| 想跳过项目认知直接生成 | 停下来；文件会放错模块、继承错基类 |

> 直接套用别人项目的 Skill，生成结果看起来对、结构却不对，返工成本比自己写还高。**在架构分层不明确的项目里，第一件事是优化项目结构，而不是接 AI 工具。**

**一句话**：AI 不缺口才，缺的是它不知道你的目录结构、命名规则和分层方式。

### 0.8 环境与技术栈前提

| 项目 | 要求 |
|---|---|
| Node.js | 已安装 Node.js 18+，终端可执行 `node -v` 和 `npx -v` |
| Qoder CN 桌面端 | 已安装并登录，可访问「扩展 → 连接器」 |
| Android Studio | 已安装 Qoder CN 插件并登录 |
| Android 工程 | Kotlin + Retrofit + MVVM，具备基础库和业务模块结构 |
| Figma | 有目标设计稿的访问权限 |
| TAPD | 有目标项目空间和 Bug 的访问权限 |
| 编译环境 | 本地能正常执行 Gradle 构建，便于生成后验证 |
| 网络 | 能访问 npm 源、Figma API、TAPD API |

> 使用 Cursor、Trae、CodeBuddy、Claude Code 等其他客户端时，把「Qoder CN 桌面端」「Android Studio」两行替换成对应客户端即可；Node.js、工程结构、Figma/TAPD 权限和编译环境的要求不变，Skill 与 Command 的放置位置按 0.6 的对照表调整。

**技术栈前提**

| 维度 | 要求 |
|---|---|
| 语言 | Kotlin |
| 网络 | Retrofit2 + OkHttp3 + Gson |
| 异步 | Kotlin Coroutines |
| 架构 | MVVM + LiveData |
| 视图绑定 | ViewBinding |
| 依赖注入 | 手动注入：`InjectorUtils + VMFactory`（非 Hilt/Koin） |
| 组件化 | 基础库 + 业务模块的多模块结构 |
| 路由/事件 | ARouter、FlowEventBus（`architecture-spec` 中有约定） |

如果项目使用 Hilt、Koin、Compose、MVI 或单模块结构，需要先调整 references 中的模板，不能直接套用。

**设计与数据前提**

| 类型 | 前提 |
|---|---|
| 设计稿 | 建议 750px 宽、3x；图层结构清晰；可访问 Figma 文件 |
| 截图输入 | 可生成布局，但尺寸和颜色精度低于设计稿链接，必须人工校准 |
| 接口文档 | 字段名、类型、必填、说明尽可能完整；缺失时需补充业务语义 |
| 缺陷单 | 评论中有开发/测试的排查结论时效果最好；无评论时需人工确认根因 |

> **安全提醒**：MCP 配置中包含 Figma API Key 和 TAPD 个人访问令牌，属于个人凭证。本文档只使用占位符，**不要把真实 token 提交到 Git、聊天群或公开文档**。如果 token 已经公开，请立即到 Figma/TAPD 后台撤销并重新生成。

---

## 1. 总体接入流程

### 1.1 接入步骤

括号内为 [SKILL-MCP-COMMAND配置指南.md](SKILL-MCP-COMMAND配置指南.md) 的章节号。

```
确认项目架构与文档可用（0.7）：分层清晰、规范有文档
        ↓
安装 Node.js / 确认 npx
        ↓
获取 Figma API Key 和 TAPD 个人访问令牌（3~4）
        ↓
配置 MCP（Qoder / Cursor / Trae，5~9）
        ↓
验证 MCP 连接（10）
        ↓
安装三个 Skill（1）
        ↓
安装 Commands（/android-feature、/android-review，2）
        ↓
完成项目架构认知
        ↓
按「页面 → 接口 → Bug」试点
        ↓
质量验收后团队推广
```

### 1.2 阶段与产出

| 阶段 | 预计时间 | 产出 |
|---|---|---|
| 环境准备 | 0.5 天 | Node、AI 编码客户端、Android Studio 可用 |
| 凭证与 MCP | 0.5 天 | 两个 MCP 显示「可用 / 已连接」 |
| Skill 安装 | 10 分钟 | 三个 Skill 出现在技能列表 |
| Commands 安装 | 5 分钟 | `/android-feature`、`/android-review` 出现在斜杠列表 |
| 项目认知 | 0.5~1 天 | 模块职责、分层、资源归属说明 |
| 试点 | 1 周 | 1 个页面、1 个接口、1 个 Bug 的完整记录 |
| 推广 | 持续 | 团队使用规范与质量度量 |

### 1.3 三个 Skill 的全景定位

三个 Skill 分别卡在研发链路的三段高频环节：

```
需求评审 → 设计 → UI 开发 → 接口联调 → 提测 → 缺陷修复 → 回归
                     │           │                    │
                     │           │                    └── fix-tapd-android-bug
                     │           └── android-api-ds
                     └── figma-android-ui
```

| 链路环节 | Skill | 触发方式 | 人的角色 |
|---|---|---|---|
| UI 开发 | `figma-android-ui` | 提供设计稿链接或截图，说「生成页面 / 转 XML」 | 确认模块、走查还原度 |
| 接口联调 | `android-api-ds` | 提供 Swagger/Knife4j 文档，说「对接接口 / 生成请求代码」 | 确认字段语义、业务分支 |
| 缺陷修复 | `fix-tapd-android-bug` | 提供 TAPD 链接，说「修复这个 Bug」 | 确认根因、验收修复 |
| 完整功能 | `figma-android-ui` → `android-api-ds` | `/android-feature <功能名> <设计稿链接> <接口文档链接>` | 确认边界、逐阶段验收 |
| 提交前审查 | `android-api-ds` + `figma-android-ui` | `/android-review` | 处理问题、决定修复优先级 |

三个 Skill 共用同一套项目规范（MVVM、组件化目录、命名规则），因此可以串起来使用。

命令层在此之上做编排：`/android-feature` 把「UI 开发 → 接口联调 → 数据上屏 → 审查」固定成一条流水线，`/android-review` 则把同一套审查标准单独抽出，供提交前复用。

---

## 2. 配置 MCP

MCP 给 AI 提供 Figma 设计稿和 TAPD Bug 的数据通道，配置完成后 Skill 才能读取这些数据。

完整步骤已拆分到 [SKILL-MCP-COMMAND配置指南.md](SKILL-MCP-COMMAND配置指南.md)，按使用客户端选择对应章节：

| 内容 | 位置 |
|---|---|
| 获取 Figma API Key | 第 3 节 |
| 获取 TAPD 个人访问令牌 | 第 4 节 |
| MCP 配置 JSON 模板 | 第 5 节 |
| Qoder 桌面端 / IDE 插件配置 | 第 6、7 节 |
| Cursor / Trae 配置 | 第 8、9 节 |
| 验证 MCP 与常见问题 | 第 10 节 |

进入下一步前，确认 `figma_mcp` 和 `tapd_mcp` 在所用客户端中显示已连接 / 可用，并能成功调用一次工具。

---

## 3. 添加 Skill 与 Command

Skill 与 Command 都不需要编译或安装依赖，把文件放进客户端扫描的目录即可生效。

| 内容 | 位置 |
|---|---|
| Skill 安装路径、各客户端目录对照、生效验证、项目适配与维护 | [SKILL-MCP-COMMAND配置指南.md](SKILL-MCP-COMMAND配置指南.md) 第 1 节 |
| Command 安装路径、编写要求、加载与验证 | [SKILL-MCP-COMMAND配置指南.md](SKILL-MCP-COMMAND配置指南.md) 第 2 节 |

安装完成后确认：`/skills list` 能看到三个 Skill，输入 `/` 能看到 `android-feature` 和 `android-review`。

> 命令与 Skill 不要混放：Skill 进 `skills/`，Command 进 `commands/`。放错目录时，两边都不会出现在列表里。

---

## 4. 三个 Skill 与两个 Command 的落地

### 4.0 落地前统一动作：项目认知

在使用任何 Skill 前，先让 AI 编码助手只读理解项目，不要直接生成代码。前提是项目本身有清晰的分层与文档（见 0.7）；架构未收敛时，先优化结构，再谈 AI。Skill 解决的是「按规范产出」，前提是它知道规范属于哪个项目、代码应该落在哪里。使用前必须先完成项目认知，不能把架构判断交给 AI 猜测。

**必须先阅读的文档**

| 文档 | 重点看什么 |
|---|---|
| 根目录 README / 架构说明 | 项目定位、模块划分、构建方式、分支规范 |
| `settings.gradle` / 各模块 `build.gradle` | 模块清单、依赖方向、是否支持独立运行、flavor |
| `lib_common` | BaseActivity/BaseFragment、工具类、统一资源 |
| `lib_http` | 响应封装、错误类型、拦截器、证书配置 |
| `lib_coremodel` | DataSource、ViewModel、InjectorUtils、实体目录 |
| 业务模块 README / 现有页面 | 页面组织方式、Adapter/Holder/Dialog 写法 |
| UI/设计规范、API 规范、代码规范 | 命名、资源、分层、提交要求 |
| 缺陷流程 / 测试说明 | Bug 优先级、回归范围、验收标准 |
| 最近 3~6 个月的提交与相似功能 | 当前真实写法，避免照搬过期文档 |

**项目认知 Prompt**

```text
请先只读理解项目，不要修改任何代码：
- 阅读 README、settings.gradle、各模块 build.gradle
- 阅读 lib_common 的 BaseActivity/BaseFragment
- 阅读 lib_http 的响应封装与错误处理
- 阅读 lib_coremodel 的 DS、VM、InjectorUtils
- 抽查 2~3 个现有业务页面

输出：
1. 模块清单与依赖关系
2. 各模块职责和分层边界
3. 新页面/新接口应落到的目录
4. 资源归属与复用规则
5. 与 Skill 文档不一致的地方

我确认后再开始具体任务。
```

确认以下问题后再开始：

1. 需求属于哪个业务模块？
2. UI、ViewModel、DataSource、ApiService 分别放在哪里？
3. 新页面继承哪个 BaseActivity/BaseFragment？
4. 网络请求使用哪套响应封装和错误处理？
5. ViewModel 如何创建，InjectorUtils 是否是唯一入口？
6. 颜色、Drawable、图标、字符串分别放在哪里？
7. 模块是集成模式还是独立模式？
8. 是否有同类页面或接口可以对齐？

如果这些问题答不上来，先补文档和代码阅读，不要开始生成。

**建议的认知动作**

1. 先让 AI 编码助手只读不改，输出一份「项目架构理解摘要」。
2. 人工核对模块依赖、分层职责、资源归属和新文件落点。
3. 确认摘要无误后，再进入具体页面、接口或 Bug 任务。
4. 生成过程中如发现架构与文档不一致，以当前代码为准，并回写文档。

> 原则：**先建立项目上下文，再让 AI 动手。** 架构认知缺失时，生成速度越快，返工成本越高。

---

### 4.1 figma-android-ui：设计稿转页面

**适用场景**

- 新页面开发
- 设计稿改版
- 已有页面补充 UI
- 需要按项目规范生成 XML 和 Activity/Fragment 骨架

**触发关键词**

- 设计工具：`Figma`、`MasterGo`、`即时设计`、`蓝湖`、`Sketch`、`Adobe XD`、`Photoshop`
- 转换动作：`设计稿转代码`、`UI 稿转 XML`、`生成 Android 布局`、`Activity 生成`
- 技术术语：`控件 ID 命名`、`dp 转换`、`750px 适配`、`3x 图片导出`、`ViewBinding`

**前置条件**

- Figma MCP 已连接。
- 设计稿为 750px 宽、3x；或能提供清晰的设计稿链接。
- 已确认目标业务模块和页面类型。
- 已了解 `lib_common` 中可复用的颜色、Drawable、图标。

**输入**

| 输入项 | 示例 |
|---|---|
| 设计稿链接 | Figma 文件链接 |
| 业务模块 | `module_order` |
| 页面类型 | Activity / Fragment / Dialog |
| 类名 | `OrderDetailActivity` |
| 图片输出目录 | `module_order/src/main/res/mipmap-xxhdpi` |
| 特殊要求 | 是否需要下拉刷新、空状态、沉浸式状态栏 |

**推荐 Prompt**

```text
根据这个 Figma 链接生成「订单详情」页面：
- 链接：<Figma 链接>
- 业务模块：module_order
- 页面类型：Activity
- 类名：OrderDetailActivity
- 图片导出到：module_order/src/main/res/mipmap-xxhdpi
- 要求：
  1. 遵循项目 XML 布局规范、命名规范和颜色规范
  2. 控件 ID 使用 m + 类型缩写 + 驼峰
  3. 尺寸统一使用 @dimen/dp_*
  4. 颜色统一使用 @color/color_*
  5. 新增资源前先检查 lib_common 是否已有
  6. 不要手写 Vector Drawable
```

**执行流程**

1. 读取 Figma 节点结构和样式。
2. 分析页面结构，识别 Frame、Text、Image、Input、Tab、Dialog 等元素。
3. 选择根布局：普通页面用 `ConstraintLayout`；折叠 Toolbar 用 `CoordinatorLayout + AppBarLayout`。
4. 生成 XML 布局、Activity/Fragment 代码和资源引用。
5. 导出图片到 `mipmap-xxhdpi`，并给出切图清单。

**元素映射（节选）**

| 设计元素 | Android 控件 | 备注 |
|---|---|---|
| 页面级 Frame | ConstraintLayout / CoordinatorLayout | 决定根布局 |
| 列表项 Frame | RecyclerView + `rv_*_cell.xml` | 单独建 item 布局 |
| Text | TextView | 关注字号、字重、颜色 |
| Rectangle | View + background | 分割线、背景块 |
| Image | ImageView | 图标、图片 |
| Input | EditText | 输入框 |
| Tab 导航 | TabLayout | 顶部标签栏 |
| 底部导航 | BottomNavigationView | 底部 Tab |
| Dialog 弹窗 | `dlg_*.xml` + Dialog 类 | 独立布局 |

**规范体系：16 份单一职责 reference**

| 分类 | 文件 | 约束内容 |
|---|---|---|
| 命名（12 份） | class / variable / method / layout-file / view-id / color / drawable / image / package / style-theme / animation / string | 每份只讲一类命名，不混规则 |
| 资源编写（1 份） | drawable-xml-spec | shape、border、ripple、gradient、selector 模板与复用规则 |
| 代码与结构（3 份） | xml-layout-spec / kotlin-code-style / architecture-spec | 属性顺序、嵌套限制、Kotlin 风格、MVVM 分层 |

**关键硬约束**

| 维度 | 规则 |
|---|---|
| 尺寸 | 设计稿 750px、3x；Figma px 与 Android dp 按 1:1 对应；文字也用 dp，不用 sp；统一引用 `@dimen/dp_*` |
| 颜色 | `color_XXXXXX`，十六进制字母大写；禁止语义化命名；统一放在 `lib_common/colors.xml` |
| 控件 ID | `m` + 类型缩写 + 驼峰，如 `mTvTitle`、`mIvBack`、`mEtPhone`、`mRvList` |
| 布局文件 | 必须带类型前缀：`activity_`、`fragment_`、`dlg_`、`rv_*_cell`、`act_*_header` |
| 嵌套 | ConstraintLayout 必须保持单层；禁止 ConstraintLayout 嵌套 ConstraintLayout（含 include/merge/自定义控件）；非 ConstraintLayout 容器总层级不超过 2 层 |
| 属性顺序 | ID → 尺寸 → 约束 → 边距 → 外观 → 内容 → 行为 → tools |
| 图片 | 从设计稿导出 3x PNG，放 `mipmap-xxhdpi`，文件名去掉 `@3x` |
| 资源复用 | 新建 shape/颜色/图片前先查 `lib_common`，已有则直接引用 |
| Vector | 禁止手写 `<vector>`，图标必须从设计稿导出 |

**产出**

```
module_order/src/main/res/layout/activity_order_detail.xml
module_order/src/main/java/.../view/activity/OrderDetailActivity.kt
module_order/src/main/res/mipmap-xxhdpi/ic_order_status.png
lib_common/src/main/res/values/colors.xml（仅新增颜色时追加）
lib_common/src/main/res/drawable/shape_white_radius_12.xml（仅缺失时新增）
```

**验收清单**

- [ ] 设计稿尺寸按 750px / 3x 处理，px 与 dp 按 1:1 对应。
- [ ] 文字大小也使用 `@dimen/dp_*`，没有使用 sp。
- [ ] 控件 ID 符合 `mTv`、`mIv`、`mEt`、`mRv` 等规范。
- [ ] 颜色使用 `@color/color_XXXXXX`，没有硬编码色值。
- [ ] ConstraintLayout 保持单层，没有 ConstraintLayout 嵌套 ConstraintLayout。
- [ ] 新资源先查 `lib_common`，没有重复创建。
- [ ] 图片为 3x PNG，放在 `mipmap-xxhdpi`。
- [ ] 没有手写 Vector Drawable。
- [ ] 真机或模拟器走查，确认还原度。

---

### 4.2 android-api-ds：接口文档转网络链路

**适用场景**

- 新接口接入
- 已有模块追加接口
- 根据 Swagger/Knife4j 文档生成请求实体、响应实体和完整调用链路
- 统一网络请求分层和错误处理

**触发关键词**

- 文档来源：`接口文档`、`API文档`、`Swagger`、`Knife4j`、`doc.html`
- 动作意图：`生成请求代码`、`对接接口`、`新增网络请求`、`生成实体类`、`生成 Req`、`生成 Bean`
- 技术术语：`Retrofit接口`、`ApiService`、`DataSource`、`ViewModel`、`VMFactory`、`LiveData`

**前置条件**

- 已确认网络模块，例如 `lib_coremodel`。
- 已确认基础包名和目录结构。
- 已确认项目基类：`BaseReq`、`BaseRes`、`AbsDS`、`BaseVMFactory` 等。
- 已确认响应封装：`NetworkResponse`、`HttpResult`、`HttpError`。
- 已确认 `InjectorUtils` 是 ViewModel 工厂的唯一入口。

**输入**

| 输入项 | 示例 |
|---|---|
| 接口文档 | Swagger/Knife4j 链接或截图 |
| 模块 | 子账号管理（SubAccount） |
| 动作 | 添加、列表、详情、更新、删除 |
| 接口路径 | `user/front/subaccount/add` |
| 请求方法 | POST |
| 请求参数 | `phone: String`、`realName: String`、`roleId: Int` |
| 响应数据 | `id: Long`、`name: String` |

**推荐 Prompt**

```text
根据这个 Swagger 链接生成「子账号-添加」的网络链路：
- 文档：<Swagger 链接>
- 模块：SubAccount
- 动作：add
- 网络模块：lib_coremodel
- 基础包名：com.xxx
- 要求：
  1. 包含 Req、Data、Api、ApiService、IDS、DS、VM、VMFactory
  2. 在 InjectorUtils 中追加 provideSubAccountVMFactory
  3. 遵循项目的命名规范、可空类型和错误处理
  4. 模块已存在时追加，不要覆盖原文件
```

**产出：最多 9 个落点**

```
entity/request/AddSubAccountReq.kt       请求实体
entity/bean/SubAccountData.kt            响应实体
api/SubAccountApi.kt                     路径常量
service/SubAccountApiService.kt          Retrofit 接口
interfaces/ISubAccountDS.kt              DataSource 接口
datasource/SubAccountDS.kt               DataSource 实现
vm/SubAccountVM.kt                       ViewModel
vm/factory/SubAccountVMFactory.kt        ViewModel 工厂
utils/InjectorUtils.kt                   追加 provideSubAccountVMFactory
```

**依赖链路**

```
Activity / Fragment
    ↓ by viewModels { InjectorUtils.provideSubAccountVMFactory(this) }
InjectorUtils
    ↓
SubAccountVMFactory
    ↓
SubAccountVM（持有 ISubAccountDS 接口）
    ↓
SubAccountDS
    ↓
SubAccountApiService（Retrofit）
```

设计原则：单向依赖、接口隔离、单一注入入口、VM 与 UI 生命周期绑定。

**关键约定**

| 维度 | 规则 |
|---|---|
| 请求定义 | ApiService 使用 `suspend`；GET 用 `@Query`，POST 用 `@Body` |
| 返回封装 | `NetworkResponse<BaseRes<T>, HttpError>` |
| 列表接口 | `BaseRes<List<{模块}Data>>` |
| 无数据返回 | `BaseRes<Any?>` |
| 实体字段 | 全部可空 `?`，默认 `null`，字段名与 JSON 完全一致 |
| 命名 | 请求类 `{动作}{模块}Req`；响应类 `{模块}Data`；常量大写下划线；VM 暴露 `{动作}Result` |
| 线程 | DS 内 `withContext(Dispatchers.IO)` 发请求、`Dispatchers.Main` 回填 LiveData |
| 错误处理 | 统一走 `AbsDS.handleResponse`，集中处理 401、409、网络错误 |
| 增量更新 | 模块已存在时追加，不覆盖原文件 |

**适配能力**

Skill 不绑定某一个工程，首次使用会确认项目根目录、网络模块、基础包名和目录结构，并自动检测现有基类命名和响应封装。常见差异的替换点：

| 项目差异 | 替换方式 |
|---|---|
| 请求/响应基类不同 | `BaseReq` / `BaseRequest`、`BaseRes` / `BaseResponse` |
| DS 基类不同 | `AbsDS` / `BaseDataSource` / `BaseRepository` |
| 响应封装不同 | `NetworkResponse` / `Result` / `ApiResponse` |
| 错误类型不同 | `HttpError` / `ApiError` / `ErrorResponse` |

**核心价值**：一次生成完整链路，避免「Service 写了但 DS 没写」「DS 写了但 InjectorUtils 没加」这类漏层问题。

**验收清单**

- [ ] 请求类命名为 `{动作}{模块}Req`，响应类命名为 `{模块}Data`。
- [ ] ApiService 使用 `suspend`。
- [ ] GET 使用 `@Query`，POST 使用 `@Body`。
- [ ] 返回类型为 `NetworkResponse<BaseRes<T>, HttpError>`。
- [ ] 列表接口返回 `BaseRes<List<T>>`。
- [ ] 实体字段全部可空，默认值为 `null`。
- [ ] 字段名与接口文档 JSON 完全一致。
- [ ] DataSource 统一走 `handleResponse`。
- [ ] ViewModel 使用 `viewModelScope.launch`。
- [ ] InjectorUtils 已追加工厂方法。
- [ ] 模块已有文件采用追加方式，没有覆盖原内容。

---

### 4.3 fix-tapd-android-bug：缺陷单转修复

**适用场景**

- 测试提交的 Bug 需要定位和修复
- Bug 描述不准确，需要结合评论和接口信息判断
- 涉及界面文案修改，需要全量排查引用

**输入链接格式**

| 格式 | 示例 | 处理方式 |
|---|---|---|
| A | `https://www.tapd.cn/tapd_fe/{workspace_id}/bug/detail/{bug_id}` | 直接提取 workspace 和 bug ID |
| B | `https://www.tapd.cn/tapd_fe/my/work?dialog_preview_id=bug_{bug_id}` | 提取 bug ID，再查 workspace |
| C | 仅 Bug ID | 先查 workspace 列表 |

**前置条件**

- TAPD MCP 已连接。
- 已确认项目根目录。
- 已了解相关业务模块和页面。
- 涉及服务端问题时，能联系后端确认。

**输入**

| 输入项 | 示例 |
|---|---|
| Bug 链接 | `https://www.tapd.cn/tapd_fe/{workspace_id}/bug/detail/{bug_id}` |
| 项目根目录 | `D:\android\project` |
| 修复要求 | 先根因和方案，确认后改代码 |
| 特殊说明 | 涉及文案、权限、支付等 |

**推荐 Prompt**

```text
修复这个 TAPD Bug：
- 链接：<TAPD Bug 链接>
- 项目根目录：<项目路径>
- 要求：
  1. 先输出根因、影响范围和修复方案，确认后再改代码
  2. 以评论中的技术结论为准，描述与评论冲突时以评论为准
  3. 遵循最小改动原则，不做无关重构
  4. 如果涉及文案，检查 XML、strings.xml、代码硬编码和多语言文件
  5. 修改后检查编译和逻辑
```

**执行流程**

1. 解析 Bug 链接，提取 workspace ID 和 bug ID。
2. 如果链接中没有 workspace ID，查询项目列表。
3. 获取 Bug 详情：标题、状态、优先级、描述、评论、流转记录、接口信息、附件。
4. 按信息优先级分析根因：
   - 评论中的技术讨论
   - 接口信息和错误码
   - 附件、截图、视频
   - 描述中的操作步骤
   - 目前结果 vs 期望结果
5. 输出修复方案：根因、影响范围、具体改动、验证要点。
6. 定位文件并修改代码。
7. 检查编译、逻辑和文案引用。

**信息优先级：评论优先原则**

| 优先级 | 信息源 | 说明 |
|---|---|---|
| 1 | 评论中的技术讨论 | 开发/测试的实际排查结论，最接近根因 |
| 2 | 接口信息 | URL、参数、响应、错误码、堆栈 |
| 3 | 附件/截图/视频 | 视觉证据、复现录屏 |
| 4 | 描述中的操作步骤 | 可能不完整或不准确 |
| 5 | 目前结果 vs 期望结果 | 差异点，以评论结论为准 |

Bug 描述与评论矛盾时，**以评论中的技术结论为准**。

**常见根因与修复方向**

| 类型 | 特征 | 修复方向 |
|---|---|---|
| 接口参数缺失 | 返回「缺少必要参数」 | 客户端补参数或增加前置校验 |
| 状态判断缺失 | 未开通/未完成流程可进入页面 | 增加状态检查、缺省页或引导 |
| 数据为空未处理 | 接口返回 null 或空数组 | 增加空数据兜底 UI |
| 逻辑顺序错误 | A 未完成可执行 B | 增加流程依赖校验 |
| 服务端问题 | 接口 500 或数据异常 | 标记服务端 Bug，客户端增加容错 |

**文案修改的全量检查**

- XML 布局中的硬编码文案
- `strings.xml` 资源
- Kotlin/Java 中的字符串字面量
- `values-en` 等多语言目录
- 其他引用同一文案的页面

优先修改 `strings.xml`，避免继续增加硬编码。

**验收清单**

- [ ] 根因有评论、接口或日志证据支撑。
- [ ] 描述与评论冲突时，以评论中的技术结论为准。
- [ ] 改动范围最小，没有无关重构。
- [ ] 复现路径已覆盖。
- [ ] 异常分支和空数据已处理。
- [ ] 文案修改已检查 XML、strings.xml、代码硬编码和多语言。
- [ ] 项目能编译通过。
- [ ] 已在真机或模拟器验证。

---

### 4.4 三个 Skill 的串联

一个新需求通常按以下顺序流转：

```
设计稿
  ↓ figma-android-ui
XML 布局 + Activity/Fragment 骨架
  ↓ android-api-ds
Req / Data / Api / Service / DS / VM / Factory / InjectorUtils
  ↓ 联调与提测
TAPD Bug
  ↓ fix-tapd-android-bug
根因分析 + 修复 + 回归
```

三个 Skill 共用同一套项目规范，因此可以连续使用。每次切换任务时，重新确认模块、页面和接口边界。

这套人工串联可以固化成 `/android-feature`：命令负责阶段顺序、交接门禁和停止点，规范仍由三个 Skill 提供。用法见 4.5。

---

### 4.5 用 Commands 把 Skill 串成流水线

Skill 一次只解决「页面」「接口」或「Bug」。真实需求常常是「新页面 + 新接口 + 数据上屏 + 提交审查」，需要连续调用多个 Skill，并在每次切换时重新交代规范。Commands 把顺序、门禁和停止点写进一条斜杠命令，避免漏阶段、漏规范、漏确认。

| Command | 解决的问题 | 与 Skill 的关系 |
|---|---|---|
| `/android-feature` | 一个功能跨四个阶段，靠人记住顺序和交接 | 阶段一调用 `figma-android-ui`，阶段二调用 `android-api-ds`，阶段四复用 `/android-review` 的标准 |
| `/android-review` | 审查只看代码正确性，不逐条比对项目规范 | 网络链路按 `android-api-ds`，UI 与资源按 `figma-android-ui` |

**分层关系**

```
Command（编排：顺序 + 门禁 + 停止点）
   ├── Skill（规范与模板：figma-android-ui / android-api-ds / fix-tapd-android-bug）
   └── MCP（数据通道：Figma 设计稿、Swagger 文档、TAPD Bug）
```

Command 本身不复制业务规则；规范冲突时，仍以 Skill 的 references 和项目现有代码为准。

**/android-feature 的四个阶段**

| 阶段 | 动作 | 是否改代码 | 阶段产出 |
|---|---|---|---|
| 一、UI | 读 Figma 节点，生成 XML 与 Activity/Fragment 骨架，导出 3x 图 | 是 | 文件清单 + 自检结果 + 与设计稿的已知偏差 |
| 二、接口链路 | 生成 Req/Data/Api/ApiService/IDS/DS/VM/Factory，补 InjectorUtils | 是 | 文件清单 + 文档字段映射表 + 对文档未明确处的假设 |
| 三、数据上屏 | DS → VM → LiveData → UI，打通加载/有数据/空/失败四态 | 是 | 数据流说明 + 改动文件清单 |
| 四、审查 | 按 `/android-review` 标准审查本次全部改动 | 否 | 概览 + 问题表 + 最小修复方案 |

- 四阶段是依赖顺序，不可跳、不可乱序；每阶段自检通过才进入下一阶段。
- 未加 `-y` 时，阶段一和阶段二结束后会停下等确认；加 `-y` 会连续跑完，但仍输出每阶段门禁摘要。
- 阶段一不写网络代码，列表用静态占位或空 Adapter；接口就绪后在阶段三替换，不允许用假数据冒充真实上屏。
- 阶段四只出报告不改代码；确认后才按 P0/P1/P2 修复，且只改本次范围内的代码。

**输入解析规则**

`/android-feature` 不要求固定格式，按内容识别，顺序任意：

| 参数 | 识别方式 | 示例 |
|---|---|---|
| 设计稿链接 | 域名含 `figma.com` / `mastergo.com` / `js.design` / `lanhuapp.com` | `https://www.figma.com/design/xxx` |
| 接口文档链接 | 含 `swagger` / `doc.html` / `knife4j` | `https://api.xxx.com/doc.html#/login` |
| 功能名 | 其余第一段非链接文本 | `登录页` |
| `-y` | 跳过中途确认，一次跑完 | 放在参数任意位置 |

- 当前对话中已出现的链接会被直接复用。
- 仍缺失的信息会一次性问全，不会多轮追问。
- 工程根目录就是当前工作区；没有 `settings.gradle(.kts)` 时会停下让你切换工作区，不会新建工程或目录。
- 模块、包名、网络模块位置从代码推断，推断不出才问。

**调用示例**

```text
# 完整功能，按阶段停下确认
/android-feature 登录页 https://www.figma.com/design/xxx https://api.xxx.com/doc.html#/login

# 参数顺序任意；只给设计稿时，命令会一次性问齐接口文档
/android-feature https://www.figma.com/design/xxx 登录页

# 试点校准后可连续跑完；仍输出每阶段门禁
/android-feature 订单详情 https://www.figma.com/design/xxx https://api.xxx.com/doc.html#/order -y

# 单独审查提交前改动
/android-review

# 只审查指定文件或目录
/android-review module_order/src/main/java/.../view/activity/OrderDetailActivity.kt
```

**什么时候用 Command，什么时候直接用 Skill**

| 场景 | 推荐方式 |
|---|---|
| 新页面 + 新接口的完整功能 | `/android-feature` |
| 已有页面改版，只动 UI | 直接触发 `figma-android-ui` |
| 只补一个接口 | 直接触发 `android-api-ds` |
| 只修 Bug | 直接触发 `fix-tapd-android-bug` |
| 提交前统一审查 | `/android-review` |
| 规范未适配、首次试点 | 人工逐步执行，不加 `-y` |
| 需求未定稿、跨模块大重构 | 拆解后逐步执行，不要一条命令跑完 |

> `-y` 只省略人工确认，不降低门禁。Skill 未适配、需求边界不清、涉及支付/权限/隐私时，不要加 `-y`。

**命令的硬性约束**

- UI 未定稿不写接口绑定；接口未就绪不得用硬编码假数据冒充真实上屏。
- 设计稿与接口冲突（字段缺失、交互无法实现）时立即提出并给可选方案，不自行折中。
- 每阶段结束必须给出「已完成 / 待确认 / 下一步」，不静默推进、不跨阶段返工。
- 规范类结论必须能指到 Skill 的具体文件与规则，不臆造。
- 不修改无关代码，不新增未使用的依赖、工具类或抽象层。

---

### 4.6 提升了哪些方面的开发效率

> 以下效率提升均以「通过质量门禁」为前提，不包含返工和缺陷修复的成本。

| 方面 | 传统方式 | 使用 Skill / Command 后 | 提效原因 |
|---|---|---|---|
| 样板代码 | 手写实体、Service、DS、VM、Factory | 一次生成完整链路 | 机械劳动交给模板 |
| 规范一致性 | 靠记忆和 Review 兜底 | 规范前置到生成阶段 | 命名、目录、资源位置一次到位 |
| 跨系统信息获取 | 在 Figma、Swagger、TAPD 间切换 | 一个链接直接读取 | MCP 直连，减少搬运 |
| 问题定位 | 靠经验翻描述和评论 | 评论优先 + 根因分类 | 上下文聚合，路径固定 |
| Code Review | 多轮修命名、修资源、修分层 | 首轮问题减少 | 检查清单内置 |
| 阶段交接 | 人记四阶段、逐次触发、重复交代规范 | `/android-feature` 按序执行并输出门禁 | 顺序、停止点、交付物固化 |
| 新人上手 | 读文档、问老人、照旧代码模仿 | 看 Skill 就能按标准产出 | 规范从文档变成可执行指令 |

**预估对比**

> 以下为试点前的经验预估，不是实测数据。建议按第 10 节记录基线后再校准。

| 任务 | 传统耗时 | 使用 Skill 后 | 说明 |
|---|---|---|---|
| 单个 UI 页面还原 | 4~8 小时 | 0.5~1.5 小时 | 仍包含人工走查和细节微调 |
| 单个接口接入 | 1.5~3 小时 | 15~30 分钟 | 9 个文件链路一次成型 |
| 单个 Bug 修复 | 0.5~2 小时 | 15~40 分钟 | 含根因分析和文案全量排查 |
| 新人完成首个页面 | 1~2 天 | 2~4 小时 | 依赖项目规范完整度 |
| 规范类 Review 问题 | 每 PR 多轮 | 首轮明显减少 | 需要持续维护 Skill |

> 这些耗时是「生成 + 人工验收」的净收益，不是纯生成时间。跳过验收省下的时间，会以返工、缺陷和线上问题加倍偿还。

**质量收益**

- **错误处理统一**：401、409、网络异常集中走 `AbsDS.handleResponse`，不再每个页面各写一套。
- **资源不重复**：颜色、shape、图标统一在 `lib_common`，生成前先查重。
- **遗漏减少**：接口链路、文案引用、注入入口都有固定检查项。
- **可追溯**：Skill 有版本号，规范变更可以随工程一起演进。

---

## 5. 标准作业流程（SOP）

**两种执行方式**

| 方式 | 适用场景 | 操作 |
|---|---|---|
| 人工逐步 | 首次试点、Skill 未适配、需求边界不清、涉及支付/权限/隐私 | 按 4.1~4.3 逐个触发 Skill，每步人工确认 |
| 命令编排 | Skill 已适配、需求边界清晰、按页面/接口拆分 | `/android-feature` 一次跑完四阶段；`/android-review` 单独复审 |

两种方式共用同一套步骤和门禁，命令只是把 3~5 步的顺序与交接固化下来，不跳过编译、自测和 Review。

| 步骤 | 动作 | 责任人 | 产出 |
|---|---|---|---|
| 1 | 完成项目认知，确认模块职责和文件落点 | 开发者 | 架构理解摘要 |
| 2 | 创建分支，保持工作区干净 | 开发者 | 可审查的 diff |
| 3 | 选择 Skill，准备完整输入 | 开发者 | 链接、模块、动作、路径 |
| 4 | 让 AI 编码助手生成或分析 | 开发者 | 代码、方案、文件清单 |
| 5 | 人工检查文件落点和依赖方向 | 开发者 | 修正意见 |
| 6 | 编译、静态检查、关键路径自测 | 开发者 | 自测记录 |
| 7 | 按验收清单逐项确认 | 开发者 | 验收结论 |
| 8 | 提交 PR，Reviewer 按普通代码标准审查 | 开发者 + Reviewer | 合并结论 |
| 9 | 提测，测试按业务场景回归 | 测试 | 测试结论 |

**命令与 SOP 的对应关系**

| 步骤 | 命令方式 |
|---|---|
| 1~2 | 命令启动时确认工程根目录与模块归属；分支和工作区仍由人保证 |
| 3~4 | `/android-feature <功能名> <设计稿链接> <接口文档链接>` |
| 5 | 每阶段结束时输出文件清单、自检结果和「待确认」项 |
| 6~7 | 命令内置阶段自检；提交前再跑 `/android-review` |
| 8 | Reviewer 用 `/android-review` 得到统一口径的问题清单 |

**任务粒度**

| 推荐 | 不推荐 |
|---|---|
| 先完成项目认知，再开始生成 | 未读文档直接让 AI 改代码 |
| 一个页面 | 一次生成整个模块的所有页面 |
| 一个接口或一组同模块接口 | 一次接入几十个接口 |
| 一个 Bug | 一次修一批不相关 Bug |
| 先试点再批量 | 未确认规范就直接全量生成 |

粒度越清晰，生成结果越稳定，`git diff` 也越容易审查。

**建议的工程纪律**

- 生成前保持工作区干净，便于用 `git diff` 审查生成内容。
- 一次只处理一个页面、一个接口或一个 Bug，避免大范围冲突。
- 生成后必须编译、自测、走查，不能直接提交。
- 批量生成前先在小范围试点，确认规范匹配度。

**提交前必须确认**

- [ ] 只包含本次任务相关的改动。
- [ ] 没有提交真实 token、密钥或本地配置。
- [ ] 生成代码已通过编译和自测。
- [ ] 文案、资源、异常分支已检查。
- [ ] PR 描述中说明了使用的 Skill 和人工修改点。

---

## 6. 质量门禁与验收

### 6.1 三条底线

| 底线 | 判定标准 | 不达标的处理 |
|---|---|---|
| 可编译 | 生成后首次构建通过 | 不允许提交 |
| 业务正确 | 主流程、边界、异常分支符合需求 | 回到需求澄清 |
| 规范一致 | 目录、分层、命名、资源符合项目规范 | 修改后再 Review |

### 6.2 四个不

1. 不跳过项目认知：不了解模块职责，不开始生成。
2. 不跳过自测：生成不等于验证，必须真机或模拟器走查。
3. 不跳过 Review：AI 产出按普通代码标准审查，不设「AI 例外」。
4. 不用生成速度替代验收：效率只统计通过质量门禁之后的交付。

### 6.3 质量门禁

| 阶段 | 检查项 | 责任 |
|---|---|---|
| 生成前 | 项目架构、模块归属、需求边界已确认 | 开发者 |
| 生成中 | 文件落点、依赖方向、资源复用符合规范 | 开发者 |
| 生成后 | 编译、静态检查、关键路径自测 | 开发者 |
| 合并前 | Code Review、文案/资源/异常分支检查 | 开发者 + Reviewer |
| 提测前 | 验收清单通过，风险点已说明 | 开发者 + 测试 |

> 验收结论只有两种：**通过**或**打回**，不存在「先合并再补」。

命令执行时，门禁由命令在各阶段尾部输出，但**验收责任仍归开发者**：`-y` 只是取消人工停顿，不会跳过编译、自测、`/android-review` 和 PR Review。

### 6.4 建议度量指标

| 指标 | 采集方式 |
|---|---|
| 单页面/单接口/单 Bug 交付耗时 | 试点前后各记录 3~5 个样本 |
| 首次编译通过率 | 生成后首次构建是否通过 |
| 首次自测通过率 | 生成后首次自测通过的需求比例 |
| Code Review 轮次 | 统计 PR 评论轮数 |
| 命名/资源类问题数 | Review 问题分类计数 |
| 命令一次跑通率 | `/android-feature` 过程中无需人工纠正阶段顺序或规范的比例 |
| 返工率 | 因规范或遗漏导致的二次修改比例 |
| 线上缺陷数 | AI 参与需求上线后的 P0/P1 缺陷 |

> 效率指标必须与质量指标一起看。单独看耗时，会掩盖返工和缺陷成本。

---

## 7. 边界与风险

| 风险 | 表现 | 对策 |
|---|---|---|
| 跳过项目认知 | 文件放错模块、继承错基类、架构不一致 | 使用前完成 4.0 的文档阅读和架构摘要确认 |
| 设计稿结构混乱 | 生成的层级和命名不合理 | 设计侧整理图层；先让 AI 编码助手输出结构分析再生成 |
| 用截图代替设计稿 | 尺寸、颜色、间距不准确 | 尽量使用设计稿链接；截图结果必须校准 |
| 接口文档缺字段说明 | 实体注释空泛、类型误判 | 补充文档或在提问中说明业务含义 |
| TAPD 评论不完整 | 根因判断偏差 | 评论优先，但仍需与开发/测试确认 |
| 生成代码直接上线 | 业务边界、异常分支缺失 | 必须编译、自测、Code Review |
| 规范漂移 | 工程改了基类，Skill 未更新 | 把 Skill 纳入工程规范维护范围 |
| 一次生成范围过大 | 文件冲突、遗漏、难以审查 | 按页面、接口、Bug 拆分任务 |
| 误用不匹配的架构 | Hilt/Compose 项目套用旧模板 | 先适配 references，再使用 |
| 项目架构未收敛 | 同类代码多种写法，生成结果随机、无法评审 | 先统一基类、网络封装与模块边界，见 0.7 |
| 用 `-y` 跳过交接 | 阶段没自检就进入下一阶段，问题被后移 | 试点期不加 `-y`；只在 Skill 校准后对低风险功能使用 |
| 命令与 Skill 脱节 | Command 引用的 Skill 路径或名称变更，命令仍在跑旧流程 | 命令和 Skill 同仓库维护，改目录或名称时同步更新命令正文 |
| 一条命令跑太大范围 | 一个功能塞多个页面、多个模块，diff 无法审查 | 命令只按单页面/单接口粒度使用，超出则拆分任务 |

需要明确：Skill 产出的是**符合团队规范的第一版代码**，不是最终业务实现。业务规则、边界条件、产品意图仍然需要人来判断。**质量责任始终在人，不在工具。**

---

## 8. 常见问题排查

配置类问题（MCP 未连接、Skill 不出现在列表、命令调不起来）按客户端查 [SKILL-MCP-COMMAND配置指南.md](SKILL-MCP-COMMAND配置指南.md)：Skill 见第 1 节，Command 见第 2 节，MCP 见第 10 节。

本节只列生成结果本身的问题。

### 8.1 生成结果相关

| 现象 | 可能原因 | 处理 |
|---|---|---|
| 生成结果放错模块 | 未做项目认知 | 先读架构文档，再指定模块和目录 |
| 文件重复创建 | 模块已有同类文件 | 要求追加而不是覆盖 |
| 颜色/资源重复 | 未检查 `lib_common` | 生成前明确要求先查重 |
| 图片尺寸不对 | 设计稿不是 3x 或导出倍率错误 | 重新导出 3x PNG |
| 布局层级过深 | 设计结构复杂 | 要求扁平化 ConstraintLayout，禁止 ConstraintLayout 嵌套 ConstraintLayout |
| Bug 修复范围过大 | 未限制改动范围 | 明确最小改动原则，只改必要代码 |

---

## 9. 安全与合规

1. **不要提交真实 token**：含 `FIGMA_API_KEY`、`TAPD_ACCESS_TOKEN` 的 `mcp.json` 只放在本地，加入 `.gitignore`。
2. **及时轮换凭证**：如果 token 出现在聊天、截图、公开文档或代码仓库中，立即撤销并重新生成。
3. **最小权限**：Figma token 只授予只读权限；TAPD 使用个人访问令牌，不要使用管理员账号的长期凭证。
4. **敏感模块人工 Review**：支付、认证、隐私、权限相关代码必须由模块负责人审查。
5. **截图脱敏**：分享配置截图时，遮挡 token、workspace ID、Bug ID、项目路径中的敏感信息。
6. **代码审查不豁免**：AI 生成的代码按普通代码标准审查，不能因为「是 AI 写的」而降低要求。
7. **谨慎使用 `-y`**：命令连续执行会在无人确认的情况下改多个文件；支付、认证、隐私、权限相关模块禁止使用 `-y`，并保持小粒度提交便于回滚。

---

## 10. 落地路线与度量

### 10.1 分阶段推进

| 阶段 | 时间 | 动作 | 产出 |
|---|---|---|---|
| 准备期 | 1 天 | 安装 Skill 与 Commands、配置 Figma/TAPD MCP、阅读项目架构与模块文档、输出架构理解摘要、选定试点工程 | 可运行环境 + 项目认知摘要 |
| 试点期 | 1 周 | 各选 1 个页面、1 个接口、1 个 Bug；先用单点 Skill，再加 `-y` 试跑 `/android-feature` | 基线耗时、生成 diff、问题清单 |
| 校准期 | 1 周 | 调整 references、基类映射、目录约定；同步命令正文中的 Skill 路径与阶段门禁 | 适配团队工程的 Skill 与 Command 版本 |
| 推广期 | 持续 | 团队使用、纳入 PR 检查（提交前 `/android-review`）、每月复盘 | 使用规范与度量报表 |

### 10.2 建议度量指标

> 效率指标必须与质量指标一起看。单独看耗时，会掩盖返工和缺陷成本。主要指标见 6.4，落地阶段再补充一项。

| 指标 | 采集方式 |
|---|---|
| 新人上手时间 | 完成首个页面的耗时 |

结论以「质量达标前提下的效率提升」为准，不以单次生成速度为准。

### 10.3 推广建议

- 先由 1~2 名开发者试点，形成可复制的提问模板和标准命令调用格式。
- 新成员进入项目后，先完成项目认知再使用 Skill 或 Command，不要跳过文档阅读。
- 把「生成后验收清单」放进 PR 模板，并规定提交前执行 `/android-review`。
- 每月收集一次反例，更新 Skill 与 Command，而不是只在群里口头提醒。
- 后续可以把测试用例生成、发布说明、埋点检查等重复工作也沉淀为 Skill，再把它们串进新的 Command。

---

## 11. 附录

### 11.1 MCP 配置模板（脱敏）

完整模板、字段说明与各客户端入口见 [SKILL-MCP-COMMAND配置指南.md](SKILL-MCP-COMMAND配置指南.md) 第 5 节。文档与仓库只保留占位符版本，真实 token 不进入 Git。

### 11.2 常用 Prompt 汇总

以下 Prompt 适合单点使用（只做 UI、只做接口、只修 Bug）。完整功能优先用 `/android-feature`，命令速查见 11.7。

**项目认知**

```text
请先只读理解项目，不要改代码。阅读 README、settings.gradle、各模块 build.gradle、
lib_common 的 BaseActivity/BaseFragment、lib_http 的响应封装、lib_coremodel 的 DS/VM/InjectorUtils，
输出模块依赖、分层职责、新文件落点和资源归属。我确认后再开始任务。
```

**设计稿转页面**

```text
根据这个 Figma 链接生成「<页面名>」页面：
- 链接：<链接>
- 业务模块：<module_xxx>
- 页面类型：<Activity/Fragment/Dialog>
- 类名：<XxxActivity>
- 图片导出到：<module>/src/main/res/mipmap-xxhdpi
- 遵循项目 XML、命名、颜色、尺寸规范；新增资源先查 lib_common；不要手写 Vector。
```

**接口接入**

```text
根据这个 Swagger 链接生成「<模块>-<动作>」的网络链路：
- 文档：<链接>
- 模块：<Module>
- 动作：<add/getList/getDetail/update/delete>
- 网络模块：lib_coremodel
- 基础包名：<com.xxx>
- 包含 Req、Data、Api、ApiService、IDS、DS、VM、VMFactory，
  并在 InjectorUtils 中追加 provide 方法。模块已存在时追加，不要覆盖。
```

**Bug 修复**

```text
修复这个 TAPD Bug：
- 链接：<链接>
- 项目根目录：<path>
- 先输出根因、影响范围和修复方案，确认后再改代码。
- 以评论中的技术结论为准；最小改动；涉及文案时全量检查 XML、strings、硬编码和多语言。
```

### 11.3 Android 项目目录参考

```
app
├── lib_common                 # Base 类、工具、颜色/Drawable/图片统一资源
│   ├── lib_coremodel          # MVVM、DataSource、ViewModel、网络实体
│   │   └── lib_http           # Retrofit/OkHttp 封装、响应封装、错误类型
│   └── lib_luban              # 图片压缩等基础能力
├── module_home                # 业务模块
├── module_delivery
├── module_me
├── module_login
├── module_message
├── module_wallet
└── module_map
```

网络层：

```
lib_coremodel/src/main/java/{basePackage}/data/remote/
├── api/                    # {模块}Api.kt
├── service/                # {模块}ApiService.kt
├── interfaces/             # I{模块}DS.kt
├── datasource/             # {模块}DS.kt
├── entity/
│   ├── request/            # {动作}{模块}Req.kt
│   └── bean/               # {模块}Data.kt
└── vm/
    ├── {模块}VM.kt
    └── factory/            # {模块}VMFactory.kt
```

业务模块目录：

```
module_{name}/src/main/java/{basePackage}/module/{name}/
├── view/
│   ├── activity/
│   ├── fragment/
│   ├── adapter/
│   ├── holder/
│   └── dialog/
└── data/
    ├── bean/
    └── request/
```

**模块职责**

| 模块 | 职责 | 使用 Skill 时的关注点 |
|---|---|---|
| `app` | 壳工程、Application、路由与全局配置 | 新页面入口、Manifest、依赖汇总 |
| `lib_common` | Base 类、工具、颜色/Drawable/共用图标 | 资源优先复用；颜色和通用 Drawable 只在这里定义 |
| `lib_http` | Retrofit/OkHttp、响应封装、错误类型、拦截器 | ApiService 返回类型和错误处理以此为准 |
| `lib_coremodel` | MVVM、DataSource、ViewModel、网络实体、InjectorUtils | 接口链路的实际落点 |
| `lib_luban` | 图片压缩等基础能力 | 涉及图片处理时确认是否复用 |
| `module_*` | 业务页面、Adapter、Holder、Dialog | 页面、布局、模块专属资源的落点 |
| 构建插件 | 版本、依赖、模块开关统一管理 | 新增模块或依赖前先确认约定 |

| 层级 | 所在模块 | 职责 |
|---|---|---|
| View | `module_*` | UI 展示、用户交互 |
| ViewModel | `lib_coremodel` | 业务逻辑、数据转换 |
| DataSource | `lib_coremodel` | 数据获取、网络请求 |
| ApiService | `lib_coremodel` | Retrofit 接口定义 |
| 网络层 | `lib_http` | HTTP 客户端封装、错误处理 |
| 基础层 | `lib_common` | Base 类、工具类、统一资源 |

**资源结构**

```
lib_common/src/main/res/
├── values/colors.xml        # 唯一颜色定义位置
├── drawable/                # shape/border/ripple/gradient/selector/bg 唯一位置
└── mipmap-xxhdpi/           # 3x PNG 主目录，存放共用图标

module_*/src/main/res/
├── layout/                  # activity_ / fragment_ / dlg_ / rv_*_cell / act_*_header
└── mipmap-xxhdpi/           # 模块专属 ic_ / img_
```

要求：

- 颜色、通用 Drawable 不允许在业务模块或 `app` 模块重复定义。
- 共用图标（返回、关闭、搜索、空状态等）先查 `lib_common`，已有则直接引用。
- 尺寸统一引用 `@dimen/dp_*`。dimen 由屏幕适配库自动生成，不需要手工维护尺寸表。
- 布局文件放对应业务模块的 `res/layout`，资源命名遵循前缀规则。

**必须存在的基类与基础设施**

| 能力 | 约定 | 作用 |
|---|---|---|
| `BaseActivity<T : ViewBinding>` | `initView` / `initData` / `subscribeUi` | 统一页面骨架 |
| `BaseFragment<T : ViewBinding>` | `initView` / `initData` / `lazyLoad` | 统一 Fragment 骨架 |
| `AbsDS` | `handleResponse` | 统一 401、409、网络错误 |
| `BaseVMFactory` | 接收 `SavedStateRegistryOwner` | VM 与 UI 生命周期绑定 |
| `InjectorUtils` | `provide{模块}VMFactory` | 手动 DI 唯一入口 |
| `BaseRes<T>` | `data` / `code` / `message` / `success` | 统一响应包装 |
| `NetworkResponse` / `HttpResult` / `HttpError` | 网络结果与错误类型 | 类型稳定，模板可复用 |
| ARouter | `ARouterPath` | 模块间路由 |
| FlowEventBus | `postEvent` / `observeEvent` | 跨模块事件 |
| 屏幕适配库 | 生成 `dp_*` | 尺寸统一 |

这些基础设施是 Skill 生成代码能够直接接上的前提。缺失时，生成结果需要额外改造。

**项目不一致时的适配方式**

| 项目差异 | 适配动作 |
|---|---|
| 基类命名不同 | 修改 `code-templates.md`，或首次使用时明确告知 AI 编码助手 |
| 响应封装不同 | 替换模板中的 `NetworkResponse` / `BaseRes` / `HttpResult` |
| 使用 Hilt/Koin | 去掉 `VMFactory` 和 `InjectorUtils`，改为注解注入 |
| 网络模块位置不同 | 首次确认网络模块名、基础包名、目录结构 |
| 设计基准不是 750px/3x | 调整尺寸换算规则后再生成 |
| 单模块非组件化 | 保留同样的包结构，目录扁平化 |
| 只有截图没有设计稿 | 可生成，但必须人工校准尺寸、颜色和图片资源 |

**关键规范速查**

设计还原：

- 750px / 3x，px 与 dp 1:1，文字也用 dp。
- 颜色 `color_XXXXXX`，字母大写，禁止语义化命名。
- 控件 ID：`m` + 类型缩写 + 驼峰。
- 布局保持单层 ConstraintLayout，禁止 ConstraintLayout 嵌套 ConstraintLayout。
- 图片 3x PNG，放 `mipmap-xxhdpi`，不手写 Vector。

接口接入：

- 请求类 `{动作}{模块}Req`，响应类 `{模块}Data`。
- ApiService 用 `suspend`，返回 `NetworkResponse<BaseRes<T>, HttpError>`。
- 实体字段全部可空，字段名与 JSON 一致。
- 模块已存在时追加，不覆盖。

缺陷修复：

- 评论优先于描述。
- 先根因和方案，再改代码。
- 最小改动，不顺手重构。
- 文案改动必须全量搜索 XML、strings、代码硬编码和多语言。

命令使用：

- `/android-feature` 一次只跑一个功能，不跨模块、不批量。
- 阶段顺序不可跳；审查阶段只审不改，确认后才按 P0/P1/P2 修复。
- Skill 未校准、需求未定稿、支付/权限/隐私模块不加 `-y`。

### 11.4 上线前检查表

- [ ] 两个 MCP 在桌面端和 IDE 插件中均显示可用 / 已连接。
- [ ] 三个 Skill 在技能列表中可见，触发语句能识别。
- [ ] `/android-feature`、`/android-review` 在斜杠列表中可见，且命令正文中的 Skill 路径正确。
- [ ] 项目认知摘要已确认，模块职责和文件落点明确。
- [ ] 试点页面、接口、Bug 各完成一次完整流程。
- [ ] 质量门禁和验收清单已纳入 PR 模板。
- [ ] 含 token 的配置文件未提交到仓库。
- [ ] 团队已明确 Skill 与 Command 的维护人和更新流程。

### 11.5 速查表

| 场景 | 一句话触发 | 主要产出 |
|---|---|---|
| 完整功能 | `/android-feature <功能名> <设计稿链接> <接口文档链接>` | UI + 网络链路 + 数据上屏 + 审查 |
| 设计还原 | 「根据这个 Figma 链接生成 XX 页面」 | XML + Activity/Fragment + 资源清单 |
| 接口接入 | 「根据这个 Swagger 生成 XX 模块 XX 动作的网络链路」 | 9 个落点的完整链路 |
| 缺陷修复 | 「修复这个 TAPD Bug：链接」 | 根因 + 方案 + 代码改动 |
| 提交前审查 | `/android-review` | 概览 + 分级问题表 + 最小修复方案 |
| 规范查询 | 「按项目规范检查这个布局」 | 命名、资源、嵌套、属性顺序问题清单 |

**使用前记住四件事**

1. 输入要完整：链接、模块、动作、项目路径。
2. 范围要小：一次一个页面、一个接口、一个 Bug。
3. 结果要验收：编译、自测、Review，一个都不能省。
4. 质量优先：质量不达标，速度没有意义。

### 11.6 5 分钟分享提纲

1. **30 秒结论**：三个 Skill 覆盖 UI 开发、接口联调、缺陷修复，`/android-feature` 把它们串成一条流水线，把团队规范变成可执行指令。
2. **1 分钟全景**：设计稿 → XML，接口文档 → 网络链路，TAPD → 修复方案；命令负责顺序与门禁。
3. **2 分钟演示**：跑一次 `/android-feature`，展示四个阶段、9 个文件的生成过程和 `/android-review` 的审查报告。
4. **1 分钟收益与前提**：提效来自减少样板代码、减少信息搬运、减少规范返工；但质量是前提，未通过编译、自测和 Review 的生成结果不计入效率收益。使用前还要先读懂项目架构与模块文档。
5. **30 秒落地**：先装 Skill 再装 Command，试点三个任务并记录基线，校准后再团队推广。

### 11.7 Commands 速查

| 命令 | 用途 | 说明 |
|---|---|---|
| `/android-feature <功能名> <设计稿链接> <接口文档链接>` | 一次交付完整功能 | 参数顺序任意；阶段一、二后停下确认 |
| `/android-feature ... -y` | 连续跑完四阶段 | 仍输出每阶段门禁；不要用于敏感模块 |
| `/android-review` | 审查未提交改动 + 分支 diff | 只审不改，问题分级输出 |
| `/android-review <文件或目录>` | 审查指定范围 | 以指定范围为准 |
| `/android-review <提交>` | 审查指定提交 | 以指定提交为准 |

**最短上手路径**

```text
1. 装 Skill（SKILL-MCP-COMMAND配置指南 第 1 节）→ /skills list 确认三个 Skill
2. 装 Command（SKILL-MCP-COMMAND配置指南 第 2 节）→ 输入 / 确认两个命令
3. 项目认知（4.0）→ 确认模块与落点
4. 单点试点（4.1~4.3）→ 校准 references
5. 完整功能（4.5）→ /android-feature ... -y
6. 提交前（4.5）→ /android-review
```

---

## 12. 版本记录

| 版本 | 日期 | 说明 |
|---|---|---|
| v1.0 | 2026-11-06 | 初始版本：MCP 配置、Skill 安装、三个 Skill 落地、质量门禁与排查 |
| v1.1 | 2026-11-06 | 合并技术分享内容：Skill 定位与效率分析、规范与元素映射、边界风险、落地路线、速查表与分享提纲 |
| v1.2 | 2026-11-11 | 新增 Commands：安装与验证、`/android-feature` 四阶段流水线、`/android-review` 双规范审查、输入解析与 `-y` 门禁、故障排查与速查；统一 Skill 命名为 `android-api-ds` / `fix-tapd-android-bug` |
| v1.3 | 2026-11-11 | 新增 0.6 跨客户端复用：Skill 与 Command 不绑定 Qoder，可用于 Cursor、Trae、CodeBuddy、Claude Code 等客户端；补充目录对照、Command 承载方式、共享安装与跨工具差异 |
| v1.4 | 2026-11-11 | 文档更名为「Android AI Coder 落地」；通用能力描述由 Qoder 改为 AI 编码助手，客户端操作步骤仍以 Qoder 为例 |
| v1.5 | 2026-11-11 | 截图按 `images/api_key`、`images/mcp` 重新分组并更新路径；新增 Cursor、Trae 的 MCP 配置与验证截图；MCP 配置与 Figma/TAPD 凭证获取拆分到独立文档 |
| v1.6 | 2026-11-11 | Skill 与 MCP 的配置内容统一收敛到 [SKILL-MCP-COMMAND配置指南.md](SKILL-MCP-COMMAND配置指南.md)，主文档只保留入口与流程；文档更名为「SKILL-MCP-配置指南」 |
| v1.7 | 2026-11-11 | 指南更名为「SKILL-MCP-COMMAND 配置指南」并新增 Command 配置章节；主文档精简 Skill/Command/MCP 配置与配置类排查，只保留入口、流程与生成结果问题 |
| v1.8 | 2026-11-11 | 新增 0.7 使用前提：必须熟悉项目架构、规则来自项目架构、按项目实际情况写 Skill；补充适用性判断与「分层不明确先优化项目」的建议 |
