FROM openjdk:11-jre-slim as builder
WORKDIR application
ARG JAR_FILE
ADD $JAR_FILE $JAR_FILE
RUN java -Djarmode=layertools -jar $JAR_FILE extract

FROM openjdk:11-jre-slim

EXPOSE 8080

WORKDIR application
COPY --from=builder /application/dependencies/ ./
COPY --from=builder /application/spring-boot-loader/ ./
COPY --from=builder /application/snapshot-dependencies/ ./
COPY --from=builder /application/application/ ./
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "org.springframework.boot.loader.JarLauncher"]

