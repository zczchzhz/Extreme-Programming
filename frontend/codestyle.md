# 代码规范文档(前后端通用)

## 📚 规范来源

本代码规范基于以下主流官方标准：

1. **Vue.js 官方风格指南**  
   [https://vuejs.org/style-guide/](https://vuejs.org/style-guide/)

2. **Google Java 风格指南**  
   [https://google.github.io/styleguide/javaguide.html](https://google.github.io/styleguide/javaguide.html)

3. **Spring Framework 代码约定**  
   [https://github.com/spring-projects/spring-framework/wiki/Code-Conventions](https://github.com/spring-projects/spring-framework/wiki/Code-Conventions)

4. **Airbnb JavaScript 风格指南**  
   [https://github.com/airbnb/javascript](https://github.com/airbnb/javascript)

5. **Element Plus 组件使用规范**  
   [https://element-plus.org/zh-CN/guide/design.html](https://element-plus.org/zh-CN/guide/design.html)

## 🎯 通用规范

### 代码格式
- 使用 **4个空格** 进行缩进（禁止使用 Tab）
- 文件末尾保留一个空行
- 每行代码不超过 120 个字符
- 使用 UTF-8 编码

### 命名规范
- 使用有意义的英文名称
- 避免使用缩写（除非是广泛认可的）
- 保持命名的一致性

## 🖥️ 前端代码规范 (Vue.js)

### 文件命名
- **组件文件**: PascalCase (如 `ContactForm.vue`)
- **工具文件**: camelCase (如 `request.js`)
- **资源文件**: kebab-case (如 `user-avatar.png`)

### 组件规范
```vue
<template>
  <!-- 模板内容 -->
</template>

<script setup>
// Composition API 代码
</script>

<style scoped>
/* 组件样式 */
</style>
```
#### Vue 组件规范
✅ 使用 Composition API 和 script setup

✅ 组件名使用 PascalCase

✅ Props 定义使用明确的数据类型

✅ 使用 ref 和 reactive 进行状态管理

#### JavaScript 规范
✅ 使用 ES6+ 语法特性

✅ 使用 const/let 代替 var

✅ 使用箭头函数保持 this 上下文

✅ 使用模板字符串进行字符串拼接

#### CSS 规范
✅ 使用 scoped CSS 避免样式污染

✅ 类名使用 kebab-case

✅ 优先使用 Element Plus 组件样式

✅ 使用 CSS 变量维护主题一致性

## 💻 后端代码规范 (Java/Spring Boot)
### 文件命名
✅ 类文件: PascalCase (如 ContactController.java)

✅ 接口文件: PascalCase (如 ContactService.java)

✅ 配置文件: kebab-case (如 application.properties)

### 包结构规范
com.contacts/
├── config/          # 配置类
├── controller/      # 控制层
├── entity/         # 实体类
├── exception/      # 异常类
├── repository/     # 数据访问层
└── service/        # 业务逻辑层

### 类与接口规范
```java
// 类注释
/**
* 联系人业务逻辑实现类
* 实现 ContactService 接口中定义的所有业务方法
  */
  @Service
  @RequiredArgsConstructor
  public class ContactServiceImpl implements ContactService {

  // 字段注释
  private final ContactRepository contactRepository;

  // 方法注释
  /**
    * 根据ID获取联系人详情
    * @param id 联系人ID
    * @return 联系人对象
    * @throws ContactNotFoundException 当联系人不存在时抛出异常
      */
      @Override
      public Contact getContactById(Long id) {
      // 方法实现
      }
      }
```
### Java 编码规范
✅ 使用 final 关键字修饰不可变字段

✅ 使用 @Override 注解明确重写方法

✅ 使用 try-with-resources 处理资源

✅ 使用 Optional 避免空指针异常

### Spring 规范
✅ 使用构造器注入依赖

✅ 在 Service 层使用 @Transactional

✅ 使用统一的异常处理机制

✅ 使用 @RestControllerAdvice 进行全局异常处理

### 📝 注释规范
**文件头注释**
```java
/**
* 联系人管理控制器
* 提供联系人的增删改查 RESTful API 接口
*
* @author 你的姓名
* @version 1.0
* @since 2024-01-01
  */
```
**方法注释**
```java
/**
* 创建新联系人
*
* @param contact 联系人信息，包含姓名、电话等字段
* @return 创建成功的联系人对象，包含自动生成的ID
* @throws DuplicatePhoneException 当电话号码已存在时抛出
* @throws ValidationException 当参数验证失败时抛出
  */
```
**行内注释**
```java
// 检查电话号码是否已存在
if (contactRepository.existsByPhone(contact.getPhone())) {
throw new DuplicatePhoneException(contact.getPhone());
}
```

## 🔧 工具配置
### IDE 配置

✅ **VS Code**: 安装 Vue、ESLint、Prettier 插件

✅ **IntelliJ IDEA**: 启用 Google Java Format 插件

✅ **Eclipse**: 配置 Java Code Formatter

### 构建工具
✅ 前端使用 **Vite** 进行构建

✅ 后端使用 **Maven** 进行依赖管理和构建

✅ 使用 **ESLint** 进行 **JavaScript** 代码检查

## 📊 代码质量要求
### 必须遵守
✅ 所有代码必须通过编译

✅ 单元测试覆盖率不低于 80%

✅ 没有编译警告（尽可能）

✅ 遵循 RESTful API 设计原则

### 建议遵守
🔶 方法长度不超过 50 行

🔶 类长度不超过 500 行

🔶 避免深度嵌套（不超过 3 层）

🔶 使用有意义的日志输出

## 🔄 版本控制规范
### 提交信息格式
✅ 类型(范围): 描述

✅ 正文（可选）

✅ 脚注（可选）

## 提交类型
🔶 feat: 新功能

🔶 fix: 修复 bug

🔶 docs: 文档更新

🔶 style: 代码格式调整

🔶 refactor: 代码重构

🔶 test: 测试相关

🔶 chore: 构建过程或辅助工具变动

## 📞 维护与更新
本规范会根据项目发展和团队需求进行定期更新。如有建议或发现问题，请及时提出。

**最后更新**: 2025年11月7日 21:21