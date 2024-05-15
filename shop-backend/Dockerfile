FROM gradle:8.7.0-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM amazoncorretto:22-headless
EXPOSE 8160
RUN mkdir /app
RUN mkdir /app/images
COPY --from=build /home/gradle/src/build/libs/*.jar /app/webapp-morsianin.jar
ENTRYPOINT ["java","-jar","/app/webapp-morsianin.jar"]
