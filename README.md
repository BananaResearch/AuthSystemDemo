# 认证系统

基于 Java 和 Spring Boot 的认证系统，实现了用户注册、登录、JWT 认证、密码找回等功能。

## 功能特性

- 用户注册
- 用户登录（JWT 认证）
- 令牌刷新
- 密码找回
- 登录限流（5 分钟内连续失败 3 次锁定 15 分钟）
- Redis 缓存支持
- MySQL 数据库存储

## 技术栈

- Java 21
- Spring Boot 2.7.x
- Spring Security
- JWT (JSON Web Token)
- Redis
- MySQL
- Maven

## 环境要求

- JDK 21
- MySQL 8.0+
- Redis
- Maven 3.6+

## 配置

在 `src/main/resources/application.properties` 中配置数据库和 Redis 连接：

```
spring.datasource.url=jdbc:mysql://localhost:3306/auth_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root

spring.redis.host=localhost
spring.redis.port=6379
```

## 构建和运行

### 使用 Maven

```bash
# 构建项目
mvn clean package

# 运行应用
java -jar target/auth-system-0.0.1-SNAPSHOT.jar
```

### 使用 Docker

```bash
# 构建 Docker 镜像
docker build -t auth-system .

# 运行容器
docker run -p 8080:8080 auth-system
```

## API 接口

### 用户注册

```
POST /api/auth/register

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "Secure@123"
}
```

### 用户登录

```
POST /api/auth/login

{
  "email": "john@example.com",
  "password": "Secure@123"
}
```

### 令牌刷新

```
POST /api/auth/refresh

{
  "refreshToken": "refresh_token_here"
}
```

### 密码找回

```
POST /api/auth/forgot-password

{
  "email": "john@example.com"
}
```

### 重置密码

```
POST /api/auth/reset-password

{
  "email": "john@example.com",
  "code": "123456",
  "newPassword": "NewSecure@456"
}
```

## 数据库表结构

应用启动时会自动创建以下表结构：

- users: 用户表
- roles: 角色表
- user_roles: 用户角色关联表
- verification_codes: 验证码表

## 安全特性

- 密码使用 BCrypt 加密存储
- JWT 令牌包含设备信息和地理位置
- 登录失败次数限制
- 账户锁定机制