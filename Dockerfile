FROM maven:3.8.5-openjdk-11 AS builder
WORKDIR /app

# 1. 复制 POMs，用于缓存依赖
COPY pom.xml ./
COPY docs-core/pom.xml docs-core/pom.xml
COPY docs-web-common/pom.xml docs-web-common/pom.xml
COPY docs-web/pom.xml docs-web/pom.xml

# 2. 安装 docs-core，跳过 PMD
COPY docs-core/src docs-core/src
RUN mvn -pl docs-core clean install -DskipTests -Dpmd.skip=true

# 3. 离线下载其它依赖
RUN mvn dependency:go-offline -B

# 4. 复制全量源码并打包，跳过测试和 PMD
COPY . .
RUN mvn clean package -DskipTests -Dpmd.skip=true

# ——运行阶段——
FROM jetty:11-jre11
COPY --from=builder /app/docs-web/target/docs-web-*.war /var/lib/jetty/webapps/docs.war
EXPOSE 8080
CMD ["java","-jar","/opt/jetty/start.jar"]