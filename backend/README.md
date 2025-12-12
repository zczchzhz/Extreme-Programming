# 通讯录管理系统 - 后端
**姓名:贺鸿志 吴建远**

**FZUID**: 832302220/832302126

**MUID**: 23125390/23126787

**云部署地址**: [http://112.124.50.95:8088/](http://112.124.50.95:8088/)

**API 文档**: 查看下面的 API 接口说明  
**后端源码仓库**: [https://github.com/zczchzhz/Extreme-Programming/tree/main/backend](https://github.com/zczchzhz/Extreme-Programming/tree/main/backend)

## 📋 项目简介

基于 Spring Boot 3.x 的通讯录管理系统后端 API，提供完整的 RESTful 接口，支持联系人的增删改查和高级搜索功能。

## 🚀 技术栈

### 后端框架
- **Spring Boot 3.5.7** - 企业级 Java 应用框架
- **Spring Data JPA** - 数据持久层解决方案
- **Spring MVC** - Web 请求处理框架

### 数据库
- **H2 Database** - 内存数据库（开发环境）
- **mysql** - 关系型数据库（生产环境）

### 开发工具
- **Maven** - 项目构建和依赖管理
- **Lombok** - 简化 Java 对象代码
- **JUnit 5** - 单元测试框架

## 🏗️ 系统架构

### 分层架构设计
控制层 (Controller) → 业务层 (Service) → 数据层 (Repository) → 数据库

### 项目结构
ExtremeProgramming_contacts_backend/
├── src/main/java/com/contacts/
│ ├── config/ 
│ │ ├── CorsConfig.java
│ │ ├── ErrorResponse.java
│ │ └── GlobalExceptionHandler.java
│ ├── controller/ 
│ │ ├── ContactController.java
│ │ └── TestController.java
│ ├── entity/ 
│ │ └── Contact.java
│ ├── exception/ 
│ │ ├── BusinessException.java
│ │ ├── ContactNotFoundException.java
│ │ ├── DuplicatePhoneException.java
│ │ ├── InvalidQQException.java
│ │ └── ValidationException.java
│ ├── repository/ 
│ │ └── ContactRepository.java
│ ├── service/ 
│ │ ├── ContactService.java
│ │ └── impl/
│ │ └── ContactServiceImpl.java
│ ├── utils/
│ │ └── ExcelUtil.java
│ └── ContactsApplication.java
├── src/test/java/
├── src/main/resources/
├── codestyle.md
├── mvnw.cmd
├── pom.xml
├── railway.toml
└──  README.md

## 🔧 API 接口文档

### 联系人管理接口

#### 获取所有联系人
```http
GET /api/contacts
Response: [Contact]
```
#### 根据ID获取联系人
```http
GET /api/contacts/{id}
Response: Contact
```
#### 创建联系人
```http
POST /api/contacts
Body: Contact
Response: Contact
```
#### 更新联系人
```http
PUT /api/contacts/{id}
Body: Contact
Response: Contact
```
#### 删除联系人
```http
DELETE /api/contacts/{id}
Response: 204 No Content
```
#### 搜索联系人
```http
GET /api/contacts/search?keyword={keyword}
Response: [Contact]
```
### 系统接口
#### 健康检查
```http
GET /health
Response: String
```
#### 测试接口
```http
GET /test
Response: String
```
## 🛠️ 安装运行
### 环境要求
JDK 17 或更高版本

Maven 3.6 或更高版本

### 开发环境运行
```bash
# 1.克隆项目
git clone https://github.com/zczchzhz/Extreme-Programming/tree/main/backend.git
```
```bash
# 进入项目目录
cd ExtremeProgramming_contacts_backend
```
```bash
# 编译项目
mvn clean compile
```
```bash
# 运行项目
mvn spring-boot:run
```

## 生产环境部署
✅项目已部署到 **阿里云** 平台，支持：

✅自动 CI/CD 流水线

✅**mysql** 数据库自动配置

✅环境变量自动管理

## 🧪 测试
### 运行单元测试
```bash
mvn test
```
### 测试覆盖范围
✅实体层测试 (ContactTest)

✅数据访问层测试 (ContactRepositoryTest)

✅业务逻辑层测试 (ContactServiceTest)

✅集成测试 (ContactServiceIntegrationTest)

## 🔒 安全特性
✅ 参数验证 - 请求参数自动验证

✅ 异常处理 - 统一的异常处理机制

✅ CORS 配置 - 跨域资源共享支持

✅ 输入清理 - SQL 注入防护

## 📞 联系信息
如有问题或建议，请联系：

姓名: **贺鸿志 吴建远**

FZUID: **832302220/832302126**

MUID: **23125390/23126787**

邮箱: **2074056583@qq.com/477731294@qq.com**

## 📄 许可证

本项目仅用于教学目的，遵循**福州大学梅努斯国际工程学院EE308FZ_Extreme Programming**作业要求。
