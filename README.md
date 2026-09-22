# college-ai-tutor
大学生辅助学习 AI 数字人系统

## ⚠️ 启动前置：DeepSeek API Key（重要）

AI 网关会在配置了真实 `DEEPSEEK_API_KEY` 后自动走 [DeepSeekGateway](../../infrastructure/src/main/java/com/aistudy/tutor/infrastructure/aigateway/DeepSeekGateway.java)；未配置时回退到 `MockAiGateway`（固定引导式三段回复，便于无 Key 演示）。

**启动后端前必须设置环境变量，否则走 Mock：**

```powershell
# 本机持久化（推荐）
setx DEEPSEEK_API_KEY "你的-key"

# 或仅在当前会话生效（注意：必须与 java 在同一个 shell 里赋值后启动）
$env:DEEPSEEK_API_KEY = "你的-key"
```

> ⚠️ 关键点：若用 `java -jar` 后台启动，请**在启动命令内先显式赋值** `$env:DEEPSEEK_API_KEY` 再 exec java（如下），否则后台进程的环境快照可能读不到已 `setx` 的变量，而误走 Mock 网关。

```powershell
cd backend
$env:DEEPSEEK_API_KEY = [System.Environment]::GetEnvironmentVariable('DEEPSEEK_API_KEY','User')
java -jar start/target/start-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev --server.port=8080
```

**验证是否走真实 DeepSeek**：登录后提问，若返回针对题目的具体讲解（非固定模板文案），即接入生效。

---

## 启动

### 后端（Spring Boot，:8080）
1. 设置上方 DeepSeek 环境变量
2. 打包（首次）并运行：
   ```powershell
   cd backend
   mvn -q clean package -DskipTests
   java -jar start\target\start-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev --server.port=8080
   ```
   > 依赖 MySQL（库 `ai_tutor`，root），Flyway 自动建表。
   > 健康检查：`http://localhost:8080/actuator/health` → `{"status":"UP"}`

### 前端（静态页，:5500）
```powershell
cd frontend
python -m http.server 5500
```
打开 `http://localhost:5500/login.html` 即可。

---

## 账号

系统无预置账号，需现场注册（仅支持 `STUDENT` / `TEACHER` 角色，注册时选择）。密码需含大小写字母 + 数字 + 符号（如 `Passw0rd!`）。

### 测试验证脚本
`e2e-doc-verify.ps1` —— 按《测试文档》的功能黑盒验证（注册 + 各功能域 + 越权 403），会自动生成随机测试账号。
