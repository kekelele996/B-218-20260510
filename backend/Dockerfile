# 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 复制 pom.xml 并下载依赖（利用缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 使用 Docker 专用配置替换默认配置
RUN cp src/main/resources/application-docker.properties src/main/resources/application.properties

# 构建
RUN mvn package -DskipTests -B

# 运行阶段
FROM tomcat:9.0-jdk17

# 删除默认应用
RUN rm -rf /usr/local/tomcat/webapps/*

# 复制 WAR 包
COPY --from=builder /app/target/pet-adoption.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
