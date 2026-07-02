FROM gradle:8.10.2-jdk21 AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean installDist --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/install/mathstack-backend ./

EXPOSE 8080

CMD ["./bin/mathstack-backend"]