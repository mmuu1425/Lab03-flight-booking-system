# FROM eclipse-temurin:17-jre-alpine
#
# # 安装 wget 用于健康检查
# RUN apk add --no-cache wget
#
# WORKDIR /app
# COPY target/user-service-1.0.0.jar app.jar
# EXPOSE 8050
# ENTRYPOINT ["java", "-jar", "app.jar"]
FROM maven:3.8-openjdk-17 AS build
WORKDIR /workspace

# 复制整个项目
COPY . .

# 只构建bonus-service模块及其依赖
RUN mvn clean package -DskipTests -pl bonus-service -am

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 安装curl
RUN apk add --no-cache curl
COPY --from=build /workspace/bonus-service/target/*.jar app.jar
EXPOSE 8050
ENTRYPOINT ["java", "-jar", "app.jar"]

