# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概览

本仓库包含一个基于 **Spring Boot 2.7 + MyBatis-Spring-Boot-Starter + MySQL** 的后端项目：

- 模块：`kuaimianshi-backend/`
- 入口类：`com.jinlin.kuaimianshibackend.KuaimianshiBackendApplication`（src/main/java/.../KuaimianshiBackendApplication.java）
- 构建工具：Maven（pom.xml）
- 数据库：MySQL（初始化脚本在 `kuaimianshi-backend/create_database.sql`）

当前代码结构较精简，随着业务扩展，优先按照典型分层结构组织代码：

- `controller`：HTTP 接口层
- `service`：业务逻辑层
- `mapper` / `dao`：数据访问层（MyBatis）
- `model` / `entity` / `dto`：实体和传输对象
- `common`：通用返回结构、错误码、工具类等（已存在）

## 常用命令

以下命令需在 `kuaimianshi-backend/` 目录下执行。

### 启动应用

```bash
# 打包并运行（通常开发直接用 IDE 运行 main 方法即可）
mvn spring-boot:run
```

### 运行测试

```bash
# 运行全部测试
mvn test

# 运行单个测试类
mvn -Dtest=KuaimianshiBackendApplicationTests test

# 运行某个测试类中的单个测试方法
mvn -Dtest=KuaimianshiBackendApplicationTests#methodName test
```

### 构建

```bash
# 打包生成可执行 jar
mvn clean package

# 跳过测试打包
mvn clean package -DskipTests
```

## 数据库与脚本

- 初始化 SQL：`kuaimianshi-backend/create_database.sql`
  - 创建库：`mianshiya`
  - 主要表：
    - `user`：用户表
    - `question_bank`：题库表
    - `question`：题目表
    - `question_bank_question`：题库与题目关联表（硬删除）
    - `mock_interview`：AI 模拟面试表
- 根据实际环境配置 `application.yml` 中的 MySQL 连接信息（当前文件较短未展开，未来扩展时保持敏感信息使用本地配置，不入库）。

## 编码约定与架构要点

1. **统一返回结构**  
   在 `com.jinlin.kuaimianshibackend.common` 包中已经存在：
   - `BaseResponse`：统一响应封装
   - `ErrorCode`：错误码枚举
   - `DeleteRequest`：通用删除请求体
   - `ResultUtils`：构建成功/失败响应的工具类

   新增接口时，优先使用上述类，保持统一的接口返回格式（如 `BaseResponse<T>` + `ResultUtils.success()` / `ResultUtils.error()`）。

2. **分层设计**  
   未来新增业务时请保持：
   - Controller 只做参数接收与简单组装、调用 Service，并返回统一响应
   - Service 实现业务逻辑、事务处理
   - Mapper 使用 MyBatis 进行数据库访问（xml/注解皆可，但同一模块保持风格一致）

3. **数据库字段与实体映射**  
   - 表字段多为下划线命名，Java 实体使用驼峰命名；可通过 MyBatis 配置 `mapUnderscoreToCamelCase=true` 实现自动映射。
   - 逻辑删除字段：`isDelete`，应在查询时注意过滤（可通过全局配置或查询条件约束）。

4. **SQL 变更**  
   - 所有数据库结构变更建议追加到 `create_database.sql` 末尾（保持版本历史），或后续引入专门的迁移工具（如 Flyway / Liquibase）。

## 对未来 Claude Code 实例的建议

- 新增或修改接口时：
  - 先查看并复用 `com.jinlin.kuaimianshibackend.common` 中的通用类。
  - 保持 Controller / Service / Mapper 的分层清晰，避免将复杂业务堆叠在 Controller。
- 引入第三方依赖时：
  - 在 `pom.xml` 中声明，并注意与现有 Spring Boot 版本 (2.7.6) 的兼容性。
- 如需扩展 AI 模拟面试、题库等功能，应优先对照 `create_database.sql` 中表结构，保持字段语义一致，避免重复建表或命名风格不统一。
