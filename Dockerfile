FROM gradle:8.8.0-jdk17 AS build
MAINTAINER kousik
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:17.0.1-jdk-slim
RUN apt-get update && apt-get install -y netcat
RUN mkdir /app
RUN mkdir /app/logs
#add user and usergroup
#RUN addgroup -g 1001 -S ambula
RUN useradd -ms /bin/bash khaofit
#Copy jar
COPY --from=build /home/gradle/src/build/libs/khaofitservice-0.0.1-SNAPSHOT.jar /home/khaofit/khaofitservice-0.0.1-SNAPSHOT.jar
USER khaofit
WORKDIR /home/khaofit
#RUN chown -R ambula:ambula /app
CMD ["java","-jar","/home/khaofit/khaofitservice-0.0.1-SNAPSHOT.jar"]