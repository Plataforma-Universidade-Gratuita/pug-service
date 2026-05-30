FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests dependency:go-offline

COPY src/ src/

RUN ./mvnw -B -DskipTests package


FROM eclipse-temurin:21-jre

ENV LANG=C.UTF-8
ENV LANGUAGE=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV QUARKUS_HTTP_HOST=0.0.0.0

WORKDIR /app

RUN useradd --system --uid 1001 --create-home quarkus

COPY --from=build /workspace/target/quarkus-app/ /app/

RUN chown -R quarkus:quarkus /app

USER quarkus

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
