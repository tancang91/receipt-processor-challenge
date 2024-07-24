FROM amazoncorretto:21-alpine-jdk AS builder
WORKDIR /opt/app
COPY gradle/ ./gradle/
COPY build.gradle gradlew settings.gradle ./
COPY src/ ./src/
RUN ./gradlew build

FROM amazoncorretto:21-alpine-jdk
WORKDIR /opt/app
COPY --from=builder /opt/app/build/libs/*.jar ./*.jar
ENTRYPOINT ["java", "-jar", "*.jar"]