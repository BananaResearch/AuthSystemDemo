# 用户认证授权系统设计方案

## 项目概述
设计一个安全的用户认证授权系统，实现注册、登录、权限管理和密码找回功能，适合作为演示自动测试用例生成的基础项目。

## 核心实体
1. **User**
   - 属性：id (Long), username (String), email (String), passwordHash (String), status (String), lastLoginTime (LocalDateTime), createTime (LocalDateTime)

2. **Role**
   - 属性：id (Long), name (String), description (String), permissions (List<String>)

3. **UserRole**
   - 属性：id (Long), userId (Long), roleId (Long)

4. **VerificationCode**
   - 属性：id (Long), email (String), code (String), type (String), expireTime (LocalDateTime), status (String)

## 基础接口设计
### 1. 用户注册接口
- **POST /api/auth/register**
  - 功能：创建新用户账号
  - 请求体：{"username": "johndoe", "email": "john@example.com", "password": "Secure@123"}
  - 返回：注册状态和用户ID

### 2. 用户登录接口
- **POST /api/auth/login**
  - 功能：用户登录并获取访问令牌
  - 请求体：{"email": "john@example.com", "password": "Secure@123"}
  - 返回：accessToken, refreshToken和用户基本信息

### 3. 令牌刷新接口
- **POST /api/auth/refresh**
  - 功能：使用刷新令牌获取新的访问令牌
  - 请求体：{"refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."}
  - 返回：新的accessToken

## 接口修改说明
### 修改用户登录接口逻辑
原逻辑：仅验证用户名密码并返回令牌
修改后逻辑：
1. 验证用户名密码
2. 检查账号状态（是否被锁定/禁用）
3. 检查登录IP是否在黑名单中
4. 实现登录限流（5分钟内连续失败3次锁定15分钟）
5. 生成JWT令牌时增加设备信息和地理位置声明
6. 记录登录日志（成功/失败）

## 新增接口设计
### 密码找回接口
- **POST /api/auth/forgot-password**
  - 功能：发送密码重置验证码
  - 请求体：{"email": "john@example.com"}
  - 返回：验证码发送状态

- **POST /api/auth/reset-password**
  - 功能：使用验证码重置密码
  - 请求体：{"email": "john@example.com", "code": "123456", "newPassword": "NewSecure@456"}
  - 返回：密码重置结果

## 技术架构
- 后端框架：Spring Boot 2.7.x
- 安全框架：Spring Security 5.x
- 认证机制：JWT (JSON Web Token)
- 密码加密：BCrypt + 盐值
- ORM：Spring Data JPA
- 数据库：PostgreSQL 14
- 缓存：Redis (用于令牌存储和限流)
- API文档：Springdoc OpenAPI (Swagger UI)
- 依赖管理：Maven