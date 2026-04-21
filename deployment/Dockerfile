FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace

COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /workspace

COPY --from=builder /workspace/applications/app-service/build/libs/*.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -jar /workspace/app.jar"]