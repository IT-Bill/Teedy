# ── 第一阶段：Maven 构建 ──
FROM maven:3.8.5-openjdk-11 AS builder
WORKDIR /app

# 1. 先复制根 pom 以及各模块 pom，加速依赖下载
COPY pom.xml ./
COPY docs-core/pom.xml docs-core/pom.xml
COPY docs-web-common/pom.xml docs-web-common/pom.xml
COPY docs-web/pom.xml docs-web/pom.xml

# 离线下载依赖
RUN mvn dependency:go-offline -B

# 2. 再把所有源码复制进来
COPY . .

# 3. 在根目录打包（跳过测试）
RUN mvn clean package -DskipTests

# ── 第二阶段：Jetty 运行 ──
FROM jetty:11-jre11
# 把打好的 docs-web 模块 war 部署到 Jetty
COPY --from=builder /app/docs-web/target/docs-web-*.war /var/lib/jetty/webapps/docs.war

EXPOSE 8080
CMD ["java", "-jar", "/opt/jetty/start.jar"]
