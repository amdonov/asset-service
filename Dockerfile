FROM java:8
MAINTAINER Aaron Donovan "amdonov@gmail.com"
COPY . /usr/src/asset-service
WORKDIR /usr/src/asset-service
RUN ./gradlew assemble
EXPOSE 8080
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "build/libs/asset-service-0.1.0-SNAPSHOT-all.jar"]
CMD ["-h"]

