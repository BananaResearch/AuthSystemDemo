将项目使用 docker 进行打包，编写 Dockerfile

Dockerfile 中要包括
1. eclipse temurin 21 jdk 作为基础镜像
2. 在用 maven 进行项目构建前，要修改为国内源
3. 要包括打包和运行两个阶段