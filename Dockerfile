FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY . /app
RUN ./gradlew -v || true
RUN ./gradlew shadowJar --no-daemon -x test || true
ENV PORT 8080
EXPOSE 8080
CMD ["java","-jar","build/libs/elcasadorserver-1.0.0-all.jar"]
