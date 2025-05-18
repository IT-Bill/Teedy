# ── 第一阶段：Maven 构建所有模块 ──
FROM maven:3.8.5-openjdk-11 AS builder
WORKDIR /app

# 1. 复制所有文件
COPY . .

# 2. 跳过测试执行和 PMD 校验，一次性打包整个 Reactor
RUN mvn clean package \
    -DskipTests \
    -Dpmd.skip=true

# ── 第二阶段：运行时镜像 ──
FROM jetty:11-jre11
# 3. 把 docs-web 模块打好的 WAR 部署到 Jetty
COPY --from=builder /app/docs-web/target/docs-web-*.war \
     /var/lib/jetty/webapps/docs.war

EXPOSE 8080
CMD ["java","-jar","/opt/jetty/start.jar"]
