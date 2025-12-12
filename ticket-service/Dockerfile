# FROM eclipse-temurin:17-jre-alpine
#
# # 安装 wget 用于健康检查
# RUN apk add --no-cache wget
#
# WORKDIR /app
# COPY target/booking-service-1.0.0.jar app.jar
# EXPOSE 8070
# ENTRYPOINT ["java", "-jar", "app.jar"]
FROM maven:3.8-openjdk-17 AS build
WORKDIR /workspace
COPY . .
RUN mvn clean package -DskipTests -pl ticket-service -am

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 安装curl
RUN apk add --no-cache curl
COPY --from=build /workspace/ticket-service/target/*.jar app.jar
EXPOSE 8070
ENTRYPOINT ["java", "-jar", "app.jar"]