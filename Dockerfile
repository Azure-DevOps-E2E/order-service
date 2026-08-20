FROM maven:3.9.16-eclipse-temurin-21-alpine AS build

WORKDIR /src
COPY pom.xml ./
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline

COPY src ./src
RUN mvn --batch-mode --no-transfer-progress verify

FROM eclipse-temurin:21.0.11_10-jre-ubi10-minimal

WORKDIR /app
ARG APP_VERSION=1.0.0
ARG APP_IMAGE_TAG=1.0.0
ENV APP_VERSION=${APP_VERSION} \
    APP_IMAGE_TAG=${APP_IMAGE_TAG}
COPY --from=build --chown=10001:10001 /src/target/order-service-1.0.0.jar app.jar

USER 10001:10001
EXPOSE 8083
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
