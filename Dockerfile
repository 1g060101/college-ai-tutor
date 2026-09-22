# ============================================================
# Stage 1: 后端编译
# ============================================================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build

# 先拷贝 Maven 配置，利用 Docker 层缓存
COPY backend/pom.xml ./pom.xml
COPY backend/shared/pom.xml   shared/pom.xml
COPY backend/domain/pom.xml   domain/pom.xml
COPY backend/application/pom.xml   application/pom.xml
COPY backend/infrastructure/pom.xml infrastructure/pom.xml
COPY backend/interfaces/pom.xml    interfaces/pom.xml
COPY backend/start/pom.xml         start/pom.xml

# 下载依赖（利用缓存）
RUN apt-get update && apt-get install -y findutils && \
    curl -fsSL https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz -o maven.tar.gz && \
    tar -xzf maven.tar.gz && \
    ./apache-maven-3.9.9/bin/mvn dependency:go-offline -B || true

# 拷贝源码并编译
COPY backend/ ./
RUN ./apache-maven-3.9.9/bin/mvn clean package -DskipTests -B

# ============================================================
# Stage 2: 运行镜像
# ============================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# 拷贝编译好的 jar
COPY --from=builder /build/start/target/*.jar app.jar

# 拷贝前端静态资源
COPY frontend/src ./static

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"
ENV PORT=8080

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]