FROM gradle:8.8.0-jdk17 AS build
MAINTAINER kousik
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:17.0.1-jdk-slim
RUN apt-get update && apt-get install -y netcat
RUN mkdir /app
RUN mkdir /app/logs

# Create a user with a specific UID between 10000 and 20000
RUN useradd -r -u 10001 -m -d /home/khaofit -s /bin/bash khaofit

# Set ownership of the home directory
RUN chown -R 10001:10001 /home/khaofit /app /app/logs

# Copy jar file to the user's home directory
COPY --from=build /home/gradle/src/build/libs/khaofitservice-0.0.1-SNAPSHOT.jar /home/khaofit/khaofitservice-0.0.1-SNAPSHOT.jar

# Set user to the specific UID
USER 10001

WORKDIR /home/khaofit

CMD ["java", "-jar", "/home/khaofit/khaofitservice-0.0.1-SNAPSHOT.jar"]
