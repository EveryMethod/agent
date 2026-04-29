# AI Agent 项目

[![Java](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-6DB33F)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3-42b883)](https://vuejs.org/)
[![Vite](https://img.shields.io/badge/Vite-5-646cff)](https://vitejs.dev/)
[![MCP](https://img.shields.io/badge/MCP-Enabled-blue)](https://modelcontextprotocol.io/)

一个面向智能体应用开发的全栈示例项目，整合了：

- 主后端 AI 服务（对话、流式输出、工具调用、RAG）
- MCP 工具生态（SSE 服务 + stdio 本地服务）
- Vue 3 前端应用（多页面聊天体验）

> 适合用于：Spring AI + MCP 的学习、二次开发模板、Agent 工程化实践。

---

## 项目特性

- 支持同步对话与 SSE 流式对话
- 支持多种流式封装（`Flux<String>`、`ServerSentEvent`、`SseEmitter`）
- 内置多步骤超级智能体入口（Manus 风格）
- 可扩展工具体系：搜索、抓取、下载、文件、终端、PDF
- 支持 RAG 文档增强（本地 markdown 语料）
- 支持 MCP 双模式接入：SSE + stdio

---

## 项目架构

```text
ai-agent-fronted (Vue3/Vite, :5173)
        |
        v
agent 主服务 (Spring Boot, :8123/api)
  - Controller: AI/SSE/Health
  - LoveApp: Prompt + Memory + RAG + Tool 调用
  - Agent: ReAct/ToolCall/AnManus
  - Tools: 搜索、网页抓取、文件、PDF、终端等
        |
        +--> MCP SSE 服务 (http://localhost:8127)
        |
        +--> MCP Stdio 服务 (image-search-mcp-server jar)
```

---

## 技术栈

### 后端（主服务）

- Java 21
- Spring Boot 3.4.3
- Spring AI (含 MCP Client)
- MyBatis-Plus + MySQL
- Knife4j / OpenAPI 3

### MCP 服务（图片搜索）

- Java 21
- Spring Boot 3.4.3
- Spring AI MCP Server
- Pexels API

### 前端

- Vue 3
- Vue Router 4
- Axios
- Vite 5

---

## 项目结构

```text
.
├─ pom.xml                               # 主后端 Maven 配置
├─ src
│  ├─ main
│  │  ├─ java/com/ai/agent
│  │  │  ├─ AgentApplication.java        # 主启动类
│  │  │  ├─ controller/                  # AI 接口、健康检查
│  │  │  ├─ app/                         # LoveApp 业务编排核心
│  │  │  ├─ agent/                       # 多步骤 Agent 实现
│  │  │  ├─ tool/                        # 工具定义与注册
│  │  │  ├─ rag/                         # RAG 与向量检索相关
│  │  │  ├─ chatMemory/                  # 聊天记忆实现
│  │  │  ├─ mapper/ entity/ converter/   # 数据层与转换
│  │  │  └─ advisor/ config/ demo/       # 顾问、配置、示例
│  │  └─ resources
│  │     ├─ application.yml              # 主配置
│  │     ├─ mcp-server.json              # MCP stdio 服务清单
│  │     └─ document/                    # RAG 文档语料
│  └─ test                               # 后端测试
├─ image-search-mcp-server               # 图片检索 MCP 子项目
│  ├─ pom.xml
│  └─ src/main
│     ├─ java/com/ai/imagesearchmcpserver
│     │  ├─ ImageSearchMcpServerApplication.java
│     │  └─ tool/ImageSearchTool.java
│     └─ resources/application*.yml
├─ ai-agent-fronted                      # Vue 前端
│  ├─ package.json
│  └─ src
│     ├─ views/                          # Home/LoveMaster/SuperAgent/ChatPage
│     ├─ api/http.js                     # API baseURL
│     └─ router/index.js                 # 路由定义
├─ docs/images                           # README 截图目录（建议）
└─ tmp                                   # 运行中间文件、下载与生成产物
```

---

## 快速开始

### 1) 环境准备

- JDK 21
- Maven 3.9+
- Node.js 18+
- MySQL 8+（或按配置切换其他数据源）

### 2) 配置密钥与数据库

编辑 `src/main/resources/application.yml`：

- `spring.ai.dashscope.api-key`
- `tool.search.api_key`
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

编辑 `src/main/resources/mcp-server.json`（可选）：

- `AMAP_MAPS_API_KEY`

编辑 `image-search-mcp-server/src/main/resources/application.yml`（可选）：

- `imageSearchTool.apiKey`

### 3) 启动后端主服务

```bash
mvn clean compile
mvn spring-boot:run
```

默认地址：`http://localhost:8123/api`

### 4) 启动图片 MCP 服务（可选）

```bash
cd image-search-mcp-server
mvn clean package
mvn spring-boot:run
```

默认端口：`8127`（SSE）

stdio 模式（供 `mcp-server.json` 调用）：

```bash
java -Dspring.ai.mcp.server.stdio=true -Dspring.main.web-application-type=none -Dlogging.pattern.console= -jar target/image-search-mcp-server-0.0.1-SNAPSHOT.jar
```

### 5) 启动前端

```bash
cd ai-agent-fronted
npm install
npm run dev
```

前端默认请求：`http://localhost:8123/api`

---

## 接口说明

基础路径：`/api/ai/loveApp`

- `GET /chat/sync?message=...&chatId=...`：同步对话
- `GET /chat/sse?message=...&chatId=...`：SSE 字符串流
- `GET /chat/sse/event?message=...&chatId=...`：SSE 事件流
- `GET /chat/sse/emitter?message=...&chatId=...`：SseEmitter 流
- `GET /manus/chat?message=...`：超级智能体流式调用

健康检查：

- `GET /api/health`

---

## 开发命令

### 后端

```bash
mvn test
mvn clean package
```

### MCP 服务

```bash
cd image-search-mcp-server
mvn test
mvn clean package
```

### 前端

```bash
cd ai-agent-fronted
npm run build
npm run preview
```

---
