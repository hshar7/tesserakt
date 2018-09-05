FROM openjdk:10
RUN mkdir /code
ADD ./target/tesserakt-0.0.1-SNAPSHOT.jar /code
WORKDIR /code
CMD ["java", "-jar", "tesserakt-0.0.1-SNAPSHOT.jar"]