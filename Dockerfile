# 使用 Eclipse Temurin 21 JDK 作为基础镜像
FROM baresearch-registry.cn-beijing.cr.aliyuncs.com/baresearch/maven:3.9.11-eclipse-temurin-21-alpine

# 设置工作目录
WORKDIR /app

# 复制 Maven 依赖
COPY pom.xml .

# 复制源代码
COPY src ./src

# 安装依赖
RUN mvn install

# 编译
RUN mvn clean package -DskipTests

# 暴露端口
EXPOSE 8080

# 运行应用
CMD ["java", "-jar", "target/auth-system-0.0.1-SNAPSHOT.jar"]