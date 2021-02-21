FROM openjdk:15-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG DEPLOYABLE_FILE
ADD $DEPLOYABLE_FILE /
COPY ${DEPLOYABLE_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]



