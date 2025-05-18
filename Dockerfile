# ——构建阶段：用 Maven 编译生成 WAR 包——
FROM maven:3.8.5-openjdk-11 AS builder

# 设置工作目录
WORKDIR /app

# 先复制 pom 以利用层缓存，加速依赖下载
COPY pom.xml ./
RUN mvn dependency:go-offline

# 复制源码并编译（跳过测试）
COPY src ./src
RUN mvn clean package -DskipTests

# ——运行阶段：用 Jetty 部署——
FROM jetty:11-jre11

# 将编译好的 WAR 包拷贝到 Jetty 的 webapps 目录
COPY --from=builder /app/docs-web/target/docs-web-*.war /var/lib/jetty/webapps/docs.war

# 暴露应用端口
EXPOSE 8080

# 默认启动命令
CMD ["java", "-jar", "/opt/jetty/start.jar"]
