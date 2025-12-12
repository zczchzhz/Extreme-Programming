# 通讯录管理系统 - 前端
**姓名:贺鸿志 吴建远**

**FZUID**: 832302220/832302126  

**MUID**: 23125390/23126787

**在线访问**: [http://112.124.50.95:8088/](http://112.124.50.95:8088/)  
**前端源码仓库**: [https://github.com/zczchzhz/Extreme-Programming/edit/main/frontend](https://github.com/zczchzhz/Extreme-Programming/edit/main/frontend)

## 📋 项目简介

基于 Vue 3 + Element Plus 的前后端分离通讯录管理系统前端项目，提供现代化的用户界面和流畅的用户体验。

## 🚀 技术栈

### 前端框架
- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 下一代前端构建工具
- **Vue Router** - 官方路由管理器

### UI 组件库
- **Element Plus** - 基于 Vue 3 的组件库

### 开发工具
- **Axios** - HTTP 请求库
- **ES6+** - 现代 JavaScript 语法

## ✨ 功能特性

### 核心功能
- ✅ **联系人管理** - 完整的 CRUD 操作
- ✅ **智能搜索** - 支持姓名、电话、邮箱、公司、微信号、qq号多字段搜索
- ✅ **数据验证** - 前端表单验证和错误处理
- ✅ **响应式设计** - 完美适配各种屏幕尺寸

### 高级功能
- ✅ **批量操作** - 支持批量删除联系人
- ✅ **批量收藏** - 支持批量收藏常用联系人
- ✅ **数据导入导出** - 支持 xlsx/xls/CSV/JSON 格式
- ✅ **头像系统** - 自定义头像上传和默认头像生成
- ✅ **实时统计** - 联系人数据统计面板

## 🏗️ 项目结构
ExtremeProgramming_contacts_frontend/
├── src/
│ ├── api/ 
│ │ └── contacts.js
│ ├── components/ 
│ │ ├── ContactForm.vue
│ │ └── ContactList.vue
│ ├── views/ 
│ │ └── HomeView.vue
│ ├── router/ 
│ │ └── index.js
│ ├── utils/
│ │ ├── excel-utils.js
│ │ ├── request.js
│ │ ├── export.js
│ │ └── import.js
│ ├── App.vue
│ └── main.js
├── .env.production
├── codestyle.md
├── index.html
├── package.json
├── package-lock.json
├── README.md
├── vercel.json
└── vite.config.js


## 🛠️ 安装运行

### 环境要求
- Node.js 16.0 或更高版本
- npm 或 yarn 包管理器

### 开发环境运行
```bash
# 1.克隆项目
git clone https://github.com/zczchzhz/Extreme-Programming/frontend.git
```
```bash
# 2.进入项目目录
cd ExtremeProgramming_contacts_frontend
```
```bash
# 3.安装依赖
npm install
```
```bash
# 4.启动开发服务器
npm run dev
```
### 生产环境构建
```bash
# 1.构建生产版本
npm run build
```
```bash
# 2.预览构建结果
npm run preview
```
## 🌐 部署说明
✅ 本项目已部署到 *阿里云服务器** 平台：

✅ **自动部署:** 推送到 main 分支自动触发部署

✅ **环境变量:** 通过 Vercel 平台配置生产环境变量

✅ 全球 CDN: 享受快速的全球内容分发

## 📞 联系信息
如有问题或建议，请联系：

姓名: **贺鸿志 吴建远**

FZUID: **832302220/832302126**

MUID: **23125390/23126787**

邮箱: **2074056583@qq.com/477731294@qq.com**

## 📄 许可证

本项目仅用于教学目的，遵循**福州大学梅努斯国际工程学院EE308FZ_Extreme Programming**作业要求。
