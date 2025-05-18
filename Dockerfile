# ——第一阶段：Maven 构建——
FROM maven:3.8.5-openjdk-11 AS builder
WORKDIR /app

# 1. 先复制父 POM 和各子模块的 POM，利用缓存加速依赖解析
COPY pom.xml ./
COPY docs-core/pom.xml docs-core/pom.xml
COPY docs-web-common/pom.xml docs-web-common/pom.xml
COPY docs-web/pom.xml docs-web/pom.xml

# 2. 只复制 docs-core 的源码，并安装到本地仓库
COPY docs-core/src docs-core/src
RUN mvn -pl docs-core clean install -DskipTests

# 3. 现在再离线下载其它第三方依赖（此时 docs-core 已在本地）
RUN mvn dependency:go-offline -B

# 4. 复制剩余源码，一次性打包所有模块
COPY . .
RUN mvn clean package -DskipTests

# ——第二阶段：运行时镜像——
FROM jetty:11-jre11
# 将打好的 docs-web WAR 部署到 Jetty
COPY --from=builder /app/docs-web/target/docs-web-*.war /var/lib/jetty/webapps/docs.war

EXPOSE 8080
CMD ["java","-jar","/opt/jetty/start.jar"]
